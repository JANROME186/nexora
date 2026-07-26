#!/usr/bin/env python3
"""Validate local/subscription CLI tools before the Nexora router executes backlog work."""

from __future__ import annotations

import argparse
import json
import os
import shutil
import subprocess
import tempfile
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


DEFAULT_CERTIFICATE = os.environ.get("NEXORA_CLI_PREFLIGHT_CERT", ".nexora/runtime/agent-cli-preflight.json")
DEFAULT_LOG = os.environ.get("NEXORA_ORCHESTRATOR_LOG", ".nexora/runtime/orchestrator-events.jsonl")
DEFAULT_TIMEOUT_SECONDS = int(os.environ.get("NEXORA_PREFLIGHT_TIMEOUT_SECONDS", "60"))
DEFAULT_OLLAMA_MODEL = os.environ.get("NEXORA_OLLAMA_MODEL", "qwen2.5-coder:0.5b")

PROVIDERS: dict[str, dict[str, Any]] = {
    "ollama_local": {
        "command": "ollama",
        "version_args": ["--version"],
        "smoke_runtime": "ollama",
        "required": True,
    },
    "claude_code_cli": {
        "command": "claude",
        "version_args": ["--version"],
        "smoke_args": ["-p", "--input-format", "text", "--output-format", "text", "--permission-mode", "dontAsk"],
        "smoke_input": "Return exactly NEXORA_PREFLIGHT_OK.",
    },
    "codex_cli": {
        "command": "codex",
        "version_args": ["--version"],
        "smoke_runtime": "codex_exec",
    },
    "github_copilot_cli": {
        "command": "gh",
        "version_args": ["--version"],
        "auth_args": ["auth", "status"],
        "smoke_args": [
            "copilot",
            "--",
            "-p",
            "Return exactly NEXORA_PREFLIGHT_OK.",
            "--allow-all-tools",
            "--allow-all-paths",
            "--stream",
            "off",
            "-s",
        ],
    },
    "gemini_cli": {
        "command": "gemini",
        "version_args": ["--version"],
        "smoke_args": ["-p", "Return exactly NEXORA_PREFLIGHT_OK.", "--output-format", "text"],
    },
    "kiro_ide_cli": {
        "command": "kiro",
        "version_args": ["--version"],
        "headless_output_supported": False,
    },
}


def now() -> str:
    return datetime.now(timezone.utc).isoformat(timespec="seconds")


def log_event(root: Path, event: str, **fields: Any) -> None:
    log_path = root / DEFAULT_LOG
    log_path.parent.mkdir(parents=True, exist_ok=True)
    payload = {"at": now(), "tool": "agent_cli_preflight", "event": event, **fields}
    with log_path.open("a", encoding="utf-8", newline="\n") as stream:
        stream.write(json.dumps(payload, ensure_ascii=False, sort_keys=True) + "\n")
    print(f"[nexora-preflight] {event}: {json.dumps(fields, ensure_ascii=False, sort_keys=True)}", flush=True)


def subprocess_command(command: str, args: list[str]) -> list[str]:
    resolved = shutil.which(command) or command
    if os.name == "nt" and Path(resolved).suffix.lower() in {".cmd", ".bat"}:
        return ["cmd", "/c", resolved, *args]
    return [resolved, *args]


def kill_process_tree(process: subprocess.Popen[str]) -> None:
    if os.name == "nt":
        subprocess.run(["taskkill", "/PID", str(process.pid), "/T", "/F"], text=True, capture_output=True, check=False)
        return
    process.kill()


def run_command(root: Path, provider_id: str, command: str, args: list[str], input_text: str | None = None) -> dict[str, Any]:
    started = datetime.now()
    resolved = subprocess_command(command, args)
    log_event(root, "command_start", provider=provider_id, command=resolved[0], args=resolved[1:])
    with tempfile.TemporaryDirectory(prefix="nexora-preflight-") as tmp:
        stdout_path = Path(tmp) / "stdout.txt"
        stderr_path = Path(tmp) / "stderr.txt"
        with stdout_path.open("w+", encoding="utf-8", newline="\n") as stdout_stream, stderr_path.open(
            "w+", encoding="utf-8", newline="\n"
        ) as stderr_stream:
            process = subprocess.Popen(
                resolved,
                stdin=subprocess.PIPE if input_text is not None else None,
                stdout=stdout_stream,
                stderr=stderr_stream,
                text=True,
                encoding="utf-8",
            )
            if input_text is not None and process.stdin:
                process.stdin.write(input_text)
                process.stdin.close()
            timed_out = False
            try:
                process.wait(timeout=DEFAULT_TIMEOUT_SECONDS)
            except subprocess.TimeoutExpired:
                timed_out = True
                kill_process_tree(process)
                try:
                    process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    kill_process_tree(process)
            stdout_stream.flush()
            stderr_stream.flush()
            stdout = stdout_path.read_text(encoding="utf-8", errors="replace")
            stderr = stderr_path.read_text(encoding="utf-8", errors="replace")
    elapsed = int((datetime.now() - started).total_seconds())
    result = {
        "command": resolved[0],
        "args": resolved[1:],
        "returncode": 124 if timed_out else process.returncode,
        "timed_out": timed_out,
        "elapsed_seconds": elapsed,
        "stdout_excerpt": stdout.strip()[:500],
        "stderr_excerpt": stderr.strip()[:500],
    }
    log_event(root, "command_end", provider=provider_id, returncode=result["returncode"], timed_out=timed_out, elapsed_seconds=elapsed)
    return result


def ollama_smoke(root: Path, provider_id: str) -> dict[str, Any]:
    payload = {
        "model": DEFAULT_OLLAMA_MODEL,
        "prompt": "Return exactly NEXORA_PREFLIGHT_OK.",
        "stream": False,
        "options": {"temperature": 0, "top_p": 0, "seed": 42, "num_predict": 16},
    }
    try:
        with urllib.request.urlopen(
            urllib.request.Request(
                "http://127.0.0.1:11434/api/generate",
                data=json.dumps(payload).encode("utf-8"),
                headers={"Content-Type": "application/json"},
                method="POST",
            ),
            timeout=DEFAULT_TIMEOUT_SECONDS,
        ) as response:
            body = json.loads(response.read().decode("utf-8"))
            text = str(body.get("response", "")).strip()
            return {"returncode": 0, "timed_out": False, "stdout_excerpt": text, "stderr_excerpt": ""}
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as exc:
        log_event(root, "ollama_smoke_failed", provider=provider_id, error=type(exc).__name__)
        return {"returncode": 1, "timed_out": False, "stdout_excerpt": "", "stderr_excerpt": type(exc).__name__}


def codex_smoke(root: Path, provider_id: str, command: str) -> dict[str, Any]:
    result_path = root / ".nexora/runtime/codex-preflight-summary.md"
    result_path.parent.mkdir(parents=True, exist_ok=True)
    return run_command(
        root,
        provider_id,
        command,
        [
            "exec",
            "--cd",
            str(root),
            "--sandbox",
            "read-only",
            "--output-last-message",
            str(result_path),
            "-",
        ],
        input_text="Return exactly NEXORA_PREFLIGHT_OK.",
    )


def contains_ok(result: dict[str, Any]) -> bool:
    text = f"{result.get('stdout_excerpt', '')}\n{result.get('stderr_excerpt', '')}"
    return result.get("returncode") == 0 and not result.get("timed_out") and "NEXORA_PREFLIGHT_OK" in text


def classify_failure(result: dict[str, Any]) -> str:
    text = f"{result.get('stdout_excerpt', '')}\n{result.get('stderr_excerpt', '')}".lower()
    if result.get("timed_out") or result.get("returncode") == 124:
        return "timeout"
    if any(token in text for token in ("login", "auth", "oauth", "not authenticated", "sign in")):
        return "operator_login_required"
    if any(token in text for token in ("quota", "rate limit", "monthly")):
        return "quota_or_rate_limit"
    if result.get("returncode") != 0:
        return "command_failed"
    return "unexpected_output"


def provider_preflight(root: Path, provider_id: str, smoke: bool) -> dict[str, Any]:
    provider = PROVIDERS[provider_id]
    command = str(provider["command"])
    resolved = shutil.which(command)
    result: dict[str, Any] = {
        "provider": provider_id,
        "checked_at": now(),
        "command": command,
        "resolved_path": resolved,
        "ready": False,
        "headless_output_supported": provider.get("headless_output_supported", True),
        "checks": {},
    }
    if provider.get("smoke_runtime") == "ollama":
        smoke_result = ollama_smoke(root, provider_id)
        result["checks"]["smoke"] = smoke_result
        if contains_ok(smoke_result):
            result["status"] = "ready"
            result["ready"] = True
            result["operator_action"] = "none"
        else:
            result["status"] = classify_failure(smoke_result)
            result["operator_action"] = "Start Ollama service and pull the configured model, then rerun preflight."
        return result
    if not resolved:
        result["status"] = "missing_binary"
        result["operator_action"] = f"Install or add {command} to PATH."
        return result

    version = run_command(root, provider_id, command, list(provider.get("version_args") or ["--version"]))
    result["checks"]["version"] = version
    if version["returncode"] != 0:
        result["status"] = "version_check_failed"
        result["operator_action"] = f"Run `{command} --version` manually and fix PATH/installation."
        return result

    auth_args = provider.get("auth_args")
    if isinstance(auth_args, list):
        auth = run_command(root, provider_id, command, auth_args)
        result["checks"]["auth"] = auth
        if auth["returncode"] != 0:
            result["status"] = "operator_login_required"
            result["operator_action"] = "Run the provider login/auth command manually, then rerun preflight."
            return result

    if not result["headless_output_supported"]:
        result["status"] = "ide_handoff_only"
        result["operator_action"] = "Use manual/IDE handoff flow; this CLI is not certified for automatic headless execution."
        return result

    if smoke:
        if provider.get("smoke_runtime") == "codex_exec":
            smoke_result = codex_smoke(root, provider_id, command)
        else:
            smoke_result = run_command(
                root,
                provider_id,
                command,
                list(provider.get("smoke_args") or []),
                input_text=str(provider.get("smoke_input")) if provider.get("smoke_input") else None,
            )
        result["checks"]["smoke"] = smoke_result
        if not contains_ok(smoke_result):
            result["status"] = classify_failure(smoke_result)
            result["operator_action"] = "Run the CLI manually with a tiny prompt or refresh login/quota before backlog execution."
            return result

    result["status"] = "ready"
    result["ready"] = True
    result["operator_action"] = "none"
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description="Preflight local agent CLIs before Nexora backlog routing.")
    parser.add_argument("--root", default=os.environ.get("NEXORA_ROOT", os.getcwd()), help="Repository root.")
    parser.add_argument("--provider", default="all", help="Provider id or all.")
    parser.add_argument("--certificate", default=DEFAULT_CERTIFICATE, help="Output certificate path.")
    parser.add_argument("--skip-smoke", action="store_true", help="Skip tiny prompt smoke checks. Version/auth checks still run.")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    provider_ids = list(PROVIDERS) if args.provider == "all" else [args.provider]
    unknown = [provider_id for provider_id in provider_ids if provider_id not in PROVIDERS]
    if unknown:
        raise SystemExit(f"Unknown provider(s): {', '.join(unknown)}")

    log_event(root, "preflight_start", providers=provider_ids, smoke=not args.skip_smoke)
    results = {provider_id: provider_preflight(root, provider_id, not args.skip_smoke) for provider_id in provider_ids}
    cert_path = (root / args.certificate).resolve()
    existing: dict[str, Any] = {}
    if cert_path.exists() and args.provider != "all":
        try:
            parsed = json.loads(cert_path.read_text(encoding="utf-8"))
            existing = parsed if isinstance(parsed, dict) else {}
        except json.JSONDecodeError:
            existing = {}
    merged_providers = {}
    if isinstance(existing.get("providers"), dict):
        merged_providers.update(existing["providers"])
    merged_providers.update(results)
    certificate = {
        "artifact": "NEXORA_AGENT_CLI_PREFLIGHT_CERTIFICATE",
        "generated_at": now(),
        "timeout_seconds": DEFAULT_TIMEOUT_SECONDS,
        "smoke_enabled": not args.skip_smoke,
        "providers": merged_providers,
    }
    cert_path.parent.mkdir(parents=True, exist_ok=True)
    cert_path.write_text(json.dumps(certificate, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    ready = [provider_id for provider_id, result in merged_providers.items() if result.get("ready")]
    not_ready = [provider_id for provider_id, result in merged_providers.items() if not result.get("ready")]
    log_event(root, "preflight_end", certificate=str(cert_path), ready=ready, not_ready=not_ready)
    print(json.dumps({"certificate": str(cert_path), "ready": ready, "not_ready": not_ready}, ensure_ascii=False, sort_keys=True))
    return 0 if ready or args.provider != "all" else 1


if __name__ == "__main__":
    raise SystemExit(main())
