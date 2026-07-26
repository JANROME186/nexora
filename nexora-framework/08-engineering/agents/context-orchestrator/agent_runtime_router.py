#!/usr/bin/env python3
"""Route optimized backlog prompts through local subscription or local agent runtimes.

The router keeps normal Nexora execution stateless and short-lived. Ollama is the mandatory local
default. External execution must use subscription-backed local CLI sessions or filesystem task
ingestion. API-key providers are intentionally excluded from the default Nexora route because the
framework must not create token-consumption cost on top of already-paid IDE/tool subscriptions.
"""

from __future__ import annotations

import argparse
import calendar
import json
import os
import shutil
import subprocess
import sys
import urllib.error
import urllib.request
from dataclasses import dataclass
from datetime import datetime, timedelta
from pathlib import Path
from typing import Any


PROJECT_PATH = os.environ.get("NEXORA_PROJECT_PATH", "projects/healthcare-operations-platform")
ACTIVE_PROMPT_DIR = os.environ.get(
    "NEXORA_ACTIVE_PROMPT_DIR",
    f"{PROJECT_PATH}/08-qa/generated-prompts/active_prompt",
)
DEFAULT_STATE_PATH = os.environ.get("NEXORA_QUOTA_TRACKER", ".nexora/runtime/quota_tracker.json")
DEFAULT_OLLAMA_MODEL = os.environ.get("NEXORA_OLLAMA_MODEL", "qwen2.5-coder:0.5b")
DEFAULT_AGENT_TASK_FILE = os.environ.get("NEXORA_AGENT_TASK_FILE", ".agent_next_task.md")
DEFAULT_AGENT_RESULT_FILE = os.environ.get("NEXORA_AGENT_RESULT_FILE", ".agent_task_summary.md")
DEFAULT_TIMEOUT_SECONDS = 600


DEFAULT_PROVIDERS: dict[str, dict[str, Any]] = {
    "ollama_local": {
        "tier": "low",
        "runtime": "ollama",
        "model": DEFAULT_OLLAMA_MODEL,
        "unlimited": True,
        "enabled": True,
        "is_blocked_until": None,
        "priority": 100,
    },
    "filesystem_task_ingestion": {
        "tier": "medium",
        "runtime": "task_ingestion",
        "model": "ide-subscription-file-ingestion",
        "task_file": DEFAULT_AGENT_TASK_FILE,
        "result_file": DEFAULT_AGENT_RESULT_FILE,
        "enabled": True,
        "is_blocked_until": None,
        "priority": 30,
    },
    "claude_code_cli": {
        "tier": "high",
        "runtime": "claude_cli",
        "model": "claude-code-subscription",
        "command": "claude",
        "args": ["-p"],
        "enabled": False,
        "window_reset_hours": 3,
        "is_blocked_until": None,
        "priority": 15,
    },
    "codex_cli": {
        "tier": "high",
        "runtime": "codex_cli",
        "model": "codex-chatgpt-subscription",
        "command": "codex",
        "enabled": False,
        "window_reset_hours": 3,
        "is_blocked_until": None,
        "priority": 18,
        "result_file": DEFAULT_AGENT_RESULT_FILE,
    },
    "github_copilot_cli": {
        "tier": "high",
        "runtime": "github_copilot_cli",
        "model": "github-copilot-subscription",
        "command": "gh",
        "args": ["copilot", "explain"],
        "enabled": False,
        "window_reset_hours": 3,
        "is_blocked_until": None,
        "priority": 20,
    },
}

DISALLOWED_API_KEY_RUNTIMES = {"openai_sdk", "google_genai_sdk", "anthropic_sdk"}


class ProviderUnavailable(RuntimeError):
    """Provider is not configured or cannot execute."""


class ProviderRateLimited(RuntimeError):
    """Provider reported rate limiting or quota exhaustion."""


@dataclass(frozen=True)
class RouteDecision:
    provider: str
    runtime: str
    model: str
    complexity: str
    reason: str


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.exists() else ""


def active_prompt_path(root: Path) -> Path:
    active_dir = root / ACTIVE_PROMPT_DIR
    prompts = sorted(path for path in active_dir.glob("*.md") if path.is_file())
    if len(prompts) != 1:
        found = ", ".join(path.name for path in prompts) if prompts else "none"
        raise SystemExit(f"Expected exactly one active prompt. Found {len(prompts)}: {found}.")
    return prompts[0]


def infer_task_id(prompt: str) -> str:
    first_line = prompt.splitlines()[0] if prompt.splitlines() else ""
    if first_line.startswith("# TASK: ") and " - " in first_line:
        return first_line.removeprefix("# TASK: ").split(" - ", 1)[0].strip()
    return "UNKNOWN"


def infer_complexity(task_id: str, prompt: str) -> str:
    text = f"{task_id}\n{prompt}".lower()
    if any(marker in task_id for marker in ("-BE-", "-CORE-", "-ARCH-")):
        return "high"
    if any(marker in task_id for marker in ("-INT-", "-FE-", "-APP-", "-WEB-")):
        return "medium"
    if any(marker in task_id for marker in ("-QA-", "-DOC-", "-DEF-", "NXF-FMT")):
        return "low"
    if any(word in text for word in ("architecture", "backend", "seguridad", "security", "database")):
        return "high"
    if any(word in text for word in ("refactor", "test", "frontend", "mobile", "integracion")):
        return "medium"
    return "low"


def load_state(path: Path) -> dict[str, Any]:
    if path.exists():
        state = json.loads(path.read_text(encoding="utf-8"))
    else:
        state = {"providers": {}, "events": []}
    return normalize_state(state)


def normalize_state(state: dict[str, Any]) -> dict[str, Any]:
    providers = state.setdefault("providers", {})
    if not isinstance(providers, dict):
        providers = {}
        state["providers"] = providers
    for provider_id in list(providers):
        provider = providers.get(provider_id)
        if isinstance(provider, dict) and provider.get("runtime") in DISALLOWED_API_KEY_RUNTIMES:
            providers.pop(provider_id, None)
    for provider_id, provider in DEFAULT_PROVIDERS.items():
        current = providers.get(provider_id)
        if not isinstance(current, dict):
            providers[provider_id] = dict(provider)
            continue
        merged = dict(provider)
        merged.update(current)
        providers[provider_id] = merged
    if not isinstance(state.get("events"), list):
        state["events"] = []
    return state


def save_state(path: Path, state: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(state, ensure_ascii=True, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def is_blocked(provider: dict[str, Any], now: datetime) -> bool:
    value = provider.get("is_blocked_until")
    if not value:
        return False
    try:
        blocked_until = datetime.fromisoformat(str(value))
    except ValueError:
        return False
    return blocked_until > now


def month_progress(now: datetime) -> float:
    days = calendar.monthrange(now.year, now.month)[1]
    return min(1.0, max(0.0, now.day / days))


def monthly_underuse_boost(provider: dict[str, Any], now: datetime) -> int:
    limit = provider.get("monthly_limit_tokens")
    if not limit:
        return 0
    used = float(provider.get("tokens_used_this_month") or 0)
    target = float(limit) * month_progress(now)
    return -25 if used < target else 0


def configured(provider: dict[str, Any]) -> bool:
    runtime = provider.get("runtime")
    if runtime == "ollama":
        return True
    if runtime in {"claude_cli", "github_copilot_cli", "codex_cli"}:
        return shutil.which(str(provider.get("command") or "claude")) is not None
    if runtime == "task_ingestion":
        task_file = Path(str(provider.get("task_file") or DEFAULT_AGENT_TASK_FILE))
        return bool(task_file.name)
    return False


def candidate_ids(complexity: str) -> list[str]:
    if complexity == "high":
        return ["claude_code_cli", "codex_cli", "github_copilot_cli", "filesystem_task_ingestion", "ollama_local"]
    if complexity == "medium":
        return ["codex_cli", "github_copilot_cli", "filesystem_task_ingestion", "ollama_local"]
    return ["ollama_local", "filesystem_task_ingestion"]


def select_provider(state: dict[str, Any], complexity: str, forced_provider: str | None = None) -> RouteDecision:
    providers = state.get("providers") or {}
    now = datetime.now()
    candidates = [forced_provider] if forced_provider else candidate_ids(complexity)
    ranked: list[tuple[int, str, dict[str, Any]]] = []
    skipped: list[str] = []
    for provider_id in candidates:
        provider = providers.get(provider_id)
        if not isinstance(provider, dict):
            skipped.append(f"{provider_id}:missing")
            continue
        if not provider.get("enabled", False):
            skipped.append(f"{provider_id}:disabled")
            continue
        if is_blocked(provider, now):
            skipped.append(f"{provider_id}:blocked")
            continue
        if not configured(provider):
            skipped.append(f"{provider_id}:not_configured")
            continue
        score = int(provider.get("priority") or 100) + monthly_underuse_boost(provider, now)
        ranked.append((score, provider_id, provider))
    if not ranked:
        provider = providers["ollama_local"]
        return RouteDecision(
            provider="ollama_local",
            runtime=str(provider["runtime"]),
            model=str(provider["model"]),
            complexity=complexity,
            reason="fallback: " + ", ".join(skipped),
        )
    _, provider_id, provider = sorted(ranked, key=lambda item: item[0])[0]
    return RouteDecision(
        provider=provider_id,
        runtime=str(provider["runtime"]),
        model=str(provider.get("model") or ""),
        complexity=complexity,
        reason="selected_by_dynamic_routing",
    )


def record_event(state: dict[str, Any], event: dict[str, Any]) -> None:
    events = state.setdefault("events", [])
    if isinstance(events, list):
        events.append({"at": datetime.now().isoformat(timespec="seconds"), **event})
        del events[:-100]


def block_provider(state: dict[str, Any], provider_id: str, hours: int) -> None:
    provider = (state.get("providers") or {}).get(provider_id)
    if not isinstance(provider, dict):
        raise SystemExit(f"Unknown provider: {provider_id}")
    blocked_until = datetime.now() + timedelta(hours=hours)
    provider["is_blocked_until"] = blocked_until.isoformat(timespec="seconds")
    record_event(state, {"type": "provider_blocked", "provider": provider_id, "hours": hours})


def call_ollama(prompt: str, model: str) -> str:
    payload = {
        "model": model,
        "prompt": prompt,
        "stream": False,
        "options": {"temperature": 0, "top_p": 0, "seed": 42, "num_ctx": 8192},
    }
    request = urllib.request.Request(
        "http://127.0.0.1:11434/api/generate",
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=DEFAULT_TIMEOUT_SECONDS) as response:
            body = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        if exc.code == 429:
            raise ProviderRateLimited("ollama returned 429") from exc
        raise
    return str(body.get("response", ""))


def call_cli(prompt: str, command: str, args: list[str]) -> str:
    result = subprocess.run(
        [command, *args, prompt],
        text=True,
        capture_output=True,
        encoding="utf-8",
        check=False,
        timeout=DEFAULT_TIMEOUT_SECONDS,
    )
    combined = f"{result.stdout}\n{result.stderr}".lower()
    if result.returncode != 0 and ("429" in combined or "rate" in combined or "quota" in combined):
        raise ProviderRateLimited(combined)
    if result.returncode != 0:
        raise ProviderUnavailable(result.stderr.strip() or f"{command} exited {result.returncode}")
    return result.stdout


def call_task_ingestion(root: Path, prompt: str, provider: dict[str, Any]) -> str:
    task_path = root / str(provider.get("task_file") or DEFAULT_AGENT_TASK_FILE)
    result_path = root / str(provider.get("result_file") or DEFAULT_AGENT_RESULT_FILE)
    task_path.parent.mkdir(parents=True, exist_ok=True)
    task_body = (
        "# Nexora Active Agent Task\n\n"
        "Lee y atiende esta tarea usando la herramienta local/IDE autenticada con la suscripcion "
        "del operador. No uses API keys por consumo de tokens. Al finalizar, deja el resumen en:\n\n"
        f"`{result_path.as_posix()}`\n\n"
        "## Prompt optimizado\n\n"
        f"{prompt.rstrip()}\n"
    )
    task_path.write_text(task_body, encoding="utf-8", newline="\n")
    return f"task_ingestion_written={task_path.as_posix()}\nexpected_summary={result_path.as_posix()}\n"


def call_codex_cli(root: Path, prompt: str, provider: dict[str, Any]) -> str:
    command = str(provider.get("command") or "codex")
    result_file = str(provider.get("result_file") or DEFAULT_AGENT_RESULT_FILE)
    result_path = root / result_file
    result_path.parent.mkdir(parents=True, exist_ok=True)
    cmd = [
        command,
        "exec",
        "--cd",
        str(root),
        "--sandbox",
        "workspace-write",
        "--output-last-message",
        str(result_path),
        "-",
    ]
    result = subprocess.run(
        cmd,
        input=prompt,
        text=True,
        capture_output=True,
        encoding="utf-8",
        check=False,
        timeout=DEFAULT_TIMEOUT_SECONDS,
    )
    combined = f"{result.stdout}\n{result.stderr}".lower()
    if result.returncode != 0 and ("429" in combined or "rate" in combined or "quota" in combined):
        raise ProviderRateLimited(combined)
    if result.returncode != 0:
        raise ProviderUnavailable(result.stderr.strip() or f"{command} exited {result.returncode}")
    return result_path.read_text(encoding="utf-8") if result_path.exists() else result.stdout


def execute_provider(prompt: str, decision: RouteDecision, state: dict[str, Any]) -> str:
    provider = (state.get("providers") or {}).get(decision.provider) or {}
    runtime = decision.runtime
    if runtime == "ollama":
        return call_ollama(prompt, decision.model)
    if runtime == "claude_cli":
        return call_cli(prompt, str(provider.get("command") or "claude"), list(provider.get("args") or ["-p"]))
    if runtime == "github_copilot_cli":
        return call_cli(prompt, str(provider.get("command") or "gh"), list(provider.get("args") or ["copilot", "explain"]))
    if runtime == "task_ingestion":
        root = Path(os.environ.get("NEXORA_ROOT", os.getcwd())).resolve()
        return call_task_ingestion(root, prompt, provider)
    if runtime == "codex_cli":
        root = Path(os.environ.get("NEXORA_ROOT", os.getcwd())).resolve()
        return call_codex_cli(root, prompt, provider)
    raise ProviderUnavailable(f"Unsupported runtime: {runtime}")


def route_and_execute(
    prompt: str,
    state: dict[str, Any],
    complexity: str,
    forced_provider: str | None,
    execute: bool,
    block_hours: int,
) -> tuple[RouteDecision, str | None]:
    attempted: list[str] = []
    provider_override = forced_provider
    while True:
        decision = select_provider(state, complexity, provider_override)
        if decision.provider in attempted:
            decision = select_provider(state, "low", "ollama_local")
        if not execute:
            return decision, None
        try:
            output = execute_provider(prompt, decision, state)
            record_event(state, {"type": "provider_success", "provider": decision.provider, "complexity": complexity})
            return decision, output
        except ProviderRateLimited:
            attempted.append(decision.provider)
            block_provider(state, decision.provider, block_hours)
            provider_override = None
            if decision.provider == "ollama_local":
                raise


def main() -> int:
    parser = argparse.ArgumentParser(description="Route an optimized Nexora prompt to the best available runtime.")
    parser.add_argument("--root", default=os.environ.get("NEXORA_ROOT", os.getcwd()), help="Repository root.")
    parser.add_argument("--prompt", default=None, help="Prompt file. Defaults to the only active prompt.")
    parser.add_argument("--state", default=DEFAULT_STATE_PATH, help="Local quota tracker path.")
    parser.add_argument("--complexity", choices=["auto", "low", "medium", "high"], default="auto")
    parser.add_argument("--provider", default=None, help="Force provider id from the quota tracker.")
    parser.add_argument("--execute", action="store_true", help="Invoke the selected provider. Default is dry-run routing only.")
    parser.add_argument("--output", default=None, help="Write provider output to this file when --execute is used.")
    parser.add_argument("--init-state", action="store_true", help="Create the local quota tracker and exit.")
    parser.add_argument("--record-429", default=None, help="Mark a provider as blocked due to quota/rate limits and exit.")
    parser.add_argument("--block-hours", type=int, default=4, help="Provider pause window after 429/quota errors.")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    os.environ["NEXORA_ROOT"] = str(root)
    state_path = (root / args.state).resolve()
    state = load_state(state_path)
    if args.init_state:
        save_state(state_path, state)
        print(state_path)
        return 0
    if args.record_429:
        block_provider(state, args.record_429, args.block_hours)
        save_state(state_path, state)
        print(f"blocked={args.record_429}")
        return 0

    prompt_path = (root / args.prompt).resolve() if args.prompt else active_prompt_path(root)
    prompt = read_text(prompt_path)
    task_id = infer_task_id(prompt)
    complexity = infer_complexity(task_id, prompt) if args.complexity == "auto" else args.complexity
    decision, output = route_and_execute(prompt, state, complexity, args.provider, args.execute, args.block_hours)
    save_state(state_path, state)

    print(json.dumps(decision.__dict__, ensure_ascii=False, sort_keys=True))
    if output is not None:
        if args.output:
            output_path = (root / args.output).resolve()
            output_path.parent.mkdir(parents=True, exist_ok=True)
            output_path.write_text(output, encoding="utf-8", newline="\n")
            print(output_path)
        else:
            sys.stdout.write(output)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
