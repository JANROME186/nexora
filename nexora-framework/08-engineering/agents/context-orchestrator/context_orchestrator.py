#!/usr/bin/env python3
"""Generate compact backlog prompts with Ollama-first local orchestration.

Ollama is the mandatory primary local orchestrator for normal framework execution. Determinism is
guaranteed by rendering the final prompt from a canonical context and by reusing a cache keyed by the
context hash. The Python-only deterministic fallback is disabled by default and may be used only for
explicit bootstrap diagnostics, never to close delivery backlog work.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import shutil
import subprocess
import urllib.error
import urllib.request
from datetime import datetime
from pathlib import Path

import yaml


DEFAULT_PATTERNS = (
    "active_backlog_item",
    "current_backlog_item",
    "next_backlog_item",
    "backlog_item_id",
    "mandatory_execution_notes",
    "previous_backlog_item",
)

DEFAULT_HOP_PROMPT_FILE = (
    "projects/healthcare-operations-platform/06-delivery/commercial-product/"
    "HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md"
)
DEFAULT_HOP_BACKLOG_FILE = (
    "projects/healthcare-operations-platform/06-delivery/commercial-product/"
    "HOP_COMMERCIAL_PRODUCT_BACKLOG.md"
)
DEFAULT_HOP_MASTER_BACKLOG_FILE = (
    "projects/healthcare-operations-platform/06-delivery/commercial-product/"
    "backlog-map/MASTER_BACKLOG_PLAN.md"
)
DEFAULT_HOP_BACKLOG_ITEM_INDEX_FILE = (
    "projects/healthcare-operations-platform/06-delivery/commercial-product/"
    "backlog-map/BACKLOG_ITEM_INDEX.md"
)
DEFAULT_PROJECT_PATH = "projects/healthcare-operations-platform"
DEFAULT_HANDOFF_DIR = "projects/healthcare-operations-platform/08-qa/handoffs"
DEFAULT_PROMPT_OUTPUT_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts"
DEFAULT_ACTIVE_PROMPT_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt"
DEFAULT_HISTORY_PROMPT_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts/history_prompt"
DEFAULT_ORCHESTRATION_CACHE_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts/cache"
DEFAULT_OLLAMA_MODEL = "qwen2.5-coder:0.5b"
DEFAULT_OLLAMA_TIMEOUT_SECONDS = 300
DEFAULT_ORCHESTRATOR_LOG = os.environ.get("NEXORA_ORCHESTRATOR_LOG", ".nexora/runtime/orchestrator-events.jsonl")
PROMPT_RENDERER_VERSION = "module-aware-active-history-prompt-v6"
EXECUTION_FLOWS = ("manual", "cli")


def extract_structured_payload(text: str) -> str:
    payload_start = text.find("<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->")
    if payload_start != -1:
        text = text[payload_start:]
    marker = "```yaml\n"
    start = text.find(marker)
    if start != -1:
        start += len(marker)
        end = text.find("\n```", start)
        if end != -1:
            return text[start:end]
    if text.startswith("---\n"):
        end = text.find("\n---\n", 4)
        if end != -1:
            return text[4:end]
    return text


def read_structured(path: Path) -> dict:
    if not path.exists():
        if path.suffix == ".md":
            legacy = path.with_suffix(".yaml")
            if legacy.exists():
                path = legacy
        elif path.suffix in {".yaml", ".yml"}:
            migrated = path.with_suffix(".md")
            if migrated.exists():
                path = migrated
        if not path.exists():
            return {}
    raw = path.read_text(encoding="utf-8")
    data = yaml.safe_load(extract_structured_payload(raw))
    return data if isinstance(data, dict) else {}


def read_yaml(path: Path) -> dict:
    return read_structured(path)


def read_yaml_compat(path: Path) -> dict:
    if path.exists():
        return read_structured(path)
    migrated = path.with_suffix(".md")
    if migrated.exists():
        return read_structured(migrated)
    return {}


def infer_active_task(root: Path) -> tuple[str, str, list[str], str | None, dict]:
    prompt_data = read_yaml(root / DEFAULT_HOP_PROMPT_FILE)
    backlog_data = read_yaml(root / DEFAULT_HOP_BACKLOG_FILE)
    master_backlog_data = read_yaml(root / DEFAULT_HOP_MASTER_BACKLOG_FILE)
    item_index_data = read_yaml(root / DEFAULT_HOP_BACKLOG_ITEM_INDEX_FILE)
    baseline = (
        ((backlog_data.get("product") or {}).get("current_baseline") or {})
        or (master_backlog_data.get("current_baseline") or {})
    )
    baseline_task_id = baseline.get("active_backlog_item")
    active_block = prompt_data
    validation_commands = prompt_data.get("validation_commands")
    if (
        not baseline_task_id
        and isinstance(validation_commands, dict)
        and validation_commands.get("backlog_item_id")
    ):
        active_block = validation_commands
    task_id = baseline_task_id or active_block.get("backlog_item_id")
    title = active_block.get("name")
    notes = active_block.get("mandatory_execution_notes") or []
    coverage_floor = active_block.get("coverage_floor") or {}
    previous = active_block.get("previous_backlog_item") or {}
    if baseline_task_id and isinstance(validation_commands, dict):
        previous = validation_commands.get("previous_backlog_item") or previous
    summary_ref = None
    previous_id = previous.get("backlog_item_id") if isinstance(previous, dict) else None
    if previous_id:
        candidate = root / DEFAULT_HANDOFF_DIR / f"{previous_id}-summary.md"
        if candidate.exists():
            summary_ref = candidate.relative_to(root).as_posix()

    if task_id and title:
        if baseline_task_id and str(task_id) == str(baseline_task_id):
            title = (
                find_backlog_title(backlog_data, str(task_id))
                or find_backlog_title(master_backlog_data, str(task_id))
                or find_backlog_title(item_index_data, str(task_id))
                or title
            )
        return str(task_id), str(title), stringify_notes(notes), summary_ref, coverage_floor if isinstance(coverage_floor, dict) else {}

    title = "Active HOP backlog item"
    if task_id:
        title = (
            find_backlog_title(backlog_data, str(task_id))
            or find_backlog_title(master_backlog_data, str(task_id))
            or find_backlog_title(item_index_data, str(task_id))
            or title
        )
    if not task_id:
        raise SystemExit("Cannot infer active backlog item. Provide --task-id and --title.")
    return str(task_id), title, [], summary_ref, {}


def stringify_notes(notes: object) -> list[str]:
    if not isinstance(notes, list):
        return []
    result: list[str] = []
    for note in notes:
        if isinstance(note, dict):
            for key, value in note.items():
                result.append(f"{key}: {value}")
        else:
            result.append(str(note))
    return result


def find_backlog_title(data: dict, task_id: str) -> str | None:
    stack: list[object] = [data]
    while stack:
        current = stack.pop()
        if isinstance(current, dict):
            if current.get("id") == task_id and current.get("name"):
                return str(current["name"])
            stack.extend(current.values())
        elif isinstance(current, list):
            stack.extend(current)
    return None


def run_rg(root: Path, paths: list[str], patterns: tuple[str, ...]) -> str:
    rg = shutil.which("rg")
    if not rg:
        return ""
    target_paths = [str(root / p) for p in paths if (root / p).exists()]
    if not target_paths:
        return ""
    pattern = "|".join(re.escape(p) for p in patterns)
    result = subprocess.run(
        [rg, "-n", pattern, *target_paths],
        text=True,
        capture_output=True,
        check=False,
    )
    return result.stdout.strip()


def compact_lines(text: str, task_id: str, root: Path, limit: int = 8) -> list[str]:
    lines = [line for line in text.splitlines() if line.strip()]
    skip_markers = (
        "command_template",
        "stale next_backlog_item",
        "blocked_by_environment",
        "If any audit item fails",
        "previous next-backlog",
        "MVP-MOD-006",
    )
    focused = [
        line.replace(str(root) + os.sep, "").replace(str(root.as_posix()) + "/", "")
        for line in lines
        if task_id in line
        or "active_backlog_item" in line
        or "current_backlog_item" in line
        or "next_backlog_item" in line
    ]
    focused = [line for line in focused if not any(marker in line for marker in skip_markers)]
    focused = sorted(dict.fromkeys(focused))
    return (focused or lines)[:limit]


def infer_workstream(task_id: str) -> str:
    if task_id.startswith("NXF-FMT"):
        return "format_migration"
    if "-BE-" in task_id:
        return "backend"
    if "-INT-" in task_id:
        return "integration"
    if "-FE-" in task_id or "-WEB-" in task_id or "-PORTAL-" in task_id:
        return "frontend"
    if "-APP-" in task_id:
        return "mobile"
    if "-QA-" in task_id or task_id.endswith("-CLOSEOUT"):
        return "quality"
    return "definition"


def relevant_coverage_floor(coverage_floor: dict, task_id: str) -> str | None:
    workstream = infer_workstream(task_id)
    key_by_workstream = {
        "backend": "backend_java_maven_line_coverage_percent_if_backend_is_touched",
        "integration": "backend_java_maven_line_coverage_percent_if_backend_is_touched",
        "frontend": "frontend_typescript_web_line_coverage_percent",
        "mobile": "mobile_typescript_foundation_line_coverage_percent",
    }
    key = key_by_workstream.get(workstream)
    if not key:
        return None
    value = coverage_floor.get(key)
    if value is None:
        return None
    label = {
        "backend": "Backend",
        "integration": "Backend/Integration",
        "frontend": "Frontend/Web",
        "mobile": "App/Mobile",
    }[workstream]
    return f"{label} >= {value}%"


def compact_title(title: str) -> str:
    replacements = {
        "Compile marketplace catalog, offer, entitlement and installation backend outputs": (
            "Marketplace & Entitlements Backend Compilation"
        ),
        "Execute framework and HOP frontmatter optimization before functional backlog resumes": (
            "Framework and HOP Frontmatter Optimization"
        ),
        "Implement custom entitlement enforcement and billing provider adapter boundary": (
            "Marketplace Entitlement Enforcement and Billing Boundary"
        )
    }
    return replacements.get(title, title)


def project_relative(path: str) -> str:
    normalized = path.replace("\\", "/")
    prefix = DEFAULT_PROJECT_PATH + "/"
    return normalized[len(prefix) :] if normalized.startswith(prefix) else normalized


def compact_mandatory_notes(task_id: str, title: str, notes: list[str], coverage_floor: dict) -> list[str]:
    """Convert verbose source notes into a layer-aware Spanish checklist."""
    workstream = infer_workstream(task_id)
    coverage = relevant_coverage_floor(coverage_floor, task_id)
    result: list[str] = []

    if workstream == "format_migration":
        result.append("Pausar desarrollo funcional de HOP hasta cerrar la optimización de formato.")
        result.append("Migrar/optimizar artefactos legados pesados a Markdown con frontmatter compacto.")
        result.append("Usar solo Python, PyYAML y Ollama local; no consumir tokens comerciales.")
    elif task_id == "COM-MOD-017-BE-002":
        result.append("Implementar enforcement custom de entitlements para instalación, activación y consumo runtime de paquetes marketplace.")
        result.append("Implementar boundary provider-agnostic para billing sin acoplar HOP a un proveedor propietario.")
        result.append("Retomar desde el handoff compacto de NXF-FMT-002; no precargar inventarios amplios.")
    elif task_id == "COM-MOD-017-BE-001":
        result.append(
            "Compilar outputs backend para marketplace catalog, package manifest, offer, "
            "license plan, entitlement, installation y billing-adapter."
        )
    elif task_id.startswith("HOP-HARD-"):
        result.append(f"Atender el slice de hardening activo: {title}.")
        result.append("Cargar el item activo y cerrar o reducir materialmente todos sus mapped_items; no basta con atender solo uno.")
    elif workstream == "backend":
        result.append(f"Compilar outputs backend para el backlog activo: {title}.")
    elif workstream == "integration":
        result.append(f"Implementar boundaries/adapters de integración para el backlog activo: {title}.")
    else:
        result.append(f"Atender el backlog activo: {title}.")

    result.append("Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.")
    if workstream == "format_migration":
        result.append("Cerrar o reducir TD-FMT-001 como deuda bloqueante de formato antes de reanudar HOP.")
    elif coverage:
        result.append(f"Preservar piso de cobertura {coverage}.")
    elif coverage_floor.get("final_target_percent") is not None:
        result.append(f"Preservar o mejorar cobertura; objetivo final >= {coverage_floor['final_target_percent']}%.")
    if task_id.startswith("HOP-HARD-"):
        result.append("Actualizar cada deuda mapeada con evidencia objetiva, estado resultante, riesgo residual y siguiente dueño si no puede cerrarse.")
    elif workstream != "format_migration":
        result.append("Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.")

    gate_by_workstream = {
        "format_migration": "Ejecutar inventario, piloto, conversión por lotes, validación de referencias, parseo Markdown/frontmatter y git diff --check.",
        "backend": "Ejecutar gates backend obligatorios: Maven, Java, Docker/BD local, SAST, dependencias, cobertura y scans de seguridad.",
        "integration": "Ejecutar gates backend/integración obligatorios: Maven, Java, Docker/BD local, contratos/adapters, SAST, dependencias, cobertura y scans de seguridad.",
        "frontend": "Ejecutar gates frontend obligatorios: typecheck, tests/cobertura, build, SAST, dependencias, i18n y scans de seguridad.",
        "mobile": "Ejecutar gates app/mobile obligatorios: typecheck, tests/cobertura, build, SAST, dependencias, i18n y scans de seguridad.",
        "quality": "Ejecutar gates de cierre, punteros, evidencias, deuda técnica, seguridad, cobertura y estado git.",
        "definition": "Ejecutar gates documentales: Markdown/frontmatter parseable, trazabilidad, punteros, deuda técnica y estado git.",
    }
    result.append(gate_by_workstream.get(workstream, gate_by_workstream["definition"]))
    result.append("No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.")
    return result


def context_pointer_block(task_id: str, summary_ref: str | None) -> list[str]:
    pointers: list[str] = []
    if summary_ref and not task_id.startswith("NXF-FMT"):
        pointers.append(f"Handoff previo: `{project_relative(summary_ref)}`")
    pointers.extend(
        [
            "Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` y `PROJECT_STATE.md` bajo demanda.",
        ]
    )
    return pointers


def task_artifact_profile(task_id: str) -> dict[str, str]:
    if task_id.startswith("NXF-FMT"):
        return {
            "context_path": "../../nexora-framework/02-standards/standards/frontmatter-artifact-migration-standard.md",
            "qa_evidence_pattern": f"08-qa/format-migration/{task_id}-validation.md",
            "security_evidence_pattern": "not_applicable_format_migration_no_runtime_code",
            "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
            "commit_suggestion": "chore(framework): optimize artifact formats",
        }
    if task_id.startswith("HOP-HARD-"):
        workstream = infer_workstream(task_id)
        commit_by_workstream = {
            "backend": "fix(hop): burn down backend hardening debt",
            "integration": "fix(hop): burn down integration and platform hardening debt",
            "frontend": "fix(hop): burn down frontend hardening debt",
            "mobile": "fix(hop): burn down mobile and portal hardening debt",
            "quality": "test(hop): validate final hardening debt burn-down",
            "definition": "docs(hop): define final hardening debt burn-down",
        }
        return {
            "context_path": "06-delivery/commercial-product/backlog-map/modules/HOP-FINAL-HARDENING.md",
            "qa_evidence_pattern": f"08-qa/qa/final-hardening/{task_id}-validation.md",
            "security_evidence_pattern": f"08-qa/security-quality/{task_id}/security-quality-evidence.md",
            "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
            "commit_suggestion": commit_by_workstream.get(workstream, "fix(hop): burn down final hardening debt"),
        }
    module_id = "-".join(task_id.split("-")[:3]) if task_id.startswith("COM-MOD-") else task_id
    module_profile = {
        "COM-MOD-014": {
            "context_path": "01-product-definition/business-capabilities/packages/",
            "qa_folder": "imaging-operations",
            "commit_subject": "imaging operations",
        },
        "COM-MOD-015": {
            "context_path": "01-product-definition/business-capabilities/packages/",
            "qa_folder": "ai-overlay",
            "commit_subject": "AI overlay",
        },
        "COM-MOD-017": {
            "context_path": "01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/",
            "qa_folder": "product-marketplace-and-extension-packaging",
            "commit_subject": "marketplace",
        },
    }.get(
        module_id,
        {
            "context_path": "01-product-definition/business-capabilities/packages/",
            "qa_folder": "commercial-product-delivery",
            "commit_subject": "commercial product",
        },
    )
    if task_id == "COM-MOD-017-BE-002":
        return {
            "context_path": module_profile["context_path"],
            "qa_evidence_pattern": f"08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.md",
            "security_evidence_pattern": f"08-qa/security-quality/{task_id}/security-quality-evidence.md",
            "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
            "commit_suggestion": "feat(hop): implement marketplace entitlement enforcement",
        }
    workstream = infer_workstream(task_id)
    commit_by_workstream = {
        "backend": f"feat(hop): compile {module_profile['commit_subject']} backend outputs",
        "integration": f"feat(hop): implement {module_profile['commit_subject']} integration boundaries",
        "frontend": f"feat(hop): compile {module_profile['commit_subject']} UI",
        "mobile": f"feat(hop): compile {module_profile['commit_subject']} mobile surfaces",
        "quality": f"test(hop): validate {module_profile['commit_subject']} backlog closure",
        "definition": f"docs(hop): define {module_profile['commit_subject']} capability packages",
        "format_migration": "chore(framework): optimize artifact formats",
    }
    return {
        "context_path": module_profile["context_path"],
        "qa_evidence_pattern": f"08-qa/qa/{module_profile['qa_folder']}/{task_id}-validation.md",
        "security_evidence_pattern": f"08-qa/security-quality/{task_id}/security-quality-evidence.md",
        "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
        "commit_suggestion": commit_by_workstream.get(workstream, f"chore(hop): close {module_profile['commit_subject']} backlog item"),
    }


def canonical_json(data: object) -> str:
    return json.dumps(data, ensure_ascii=True, sort_keys=True, separators=(",", ":"))


def sha256_text(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def log_event(root: Path, event: str, **fields: object) -> None:
    log_path = root / DEFAULT_ORCHESTRATOR_LOG
    log_path.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "at": datetime.now().isoformat(timespec="seconds"),
        "tool": "context_orchestrator",
        "event": event,
        **fields,
    }
    with log_path.open("a", encoding="utf-8", newline="\n") as stream:
        stream.write(json.dumps(payload, ensure_ascii=False, sort_keys=True) + "\n")
    print(f"[nexora-orchestrator] {event}: {json.dumps(fields, ensure_ascii=False, sort_keys=True)}", flush=True)


def default_execution_flow() -> str:
    value = os.environ.get("NEXORA_EXECUTION_FLOW", "manual").strip().lower()
    return value if value in EXECUTION_FLOWS else "manual"


def parse_json_object(text: str) -> dict:
    try:
        parsed = json.loads(text)
        return parsed if isinstance(parsed, dict) else {}
    except json.JSONDecodeError:
        pass
    start = text.find("{")
    end = text.rfind("}")
    if start == -1 or end == -1 or end <= start:
        return {}
    try:
        parsed = json.loads(text[start : end + 1])
        return parsed if isinstance(parsed, dict) else {}
    except json.JSONDecodeError:
        return {}


def build_canonical_context(
    root: Path,
    task_id: str,
    title: str,
    context_lines: list[str],
    summary_ref: str | None,
    mandatory_notes: list[str],
    coverage_floor: dict,
    execution_flow: str,
) -> dict:
    profile = task_artifact_profile(task_id)
    return {
        "root": root.as_posix(),
        "project": DEFAULT_PROJECT_PATH,
        "task_id": task_id,
        "title": title,
        "execution_flow": execution_flow,
        "summary_ref": summary_ref,
        "mandatory_notes": compact_mandatory_notes(task_id, title, mandatory_notes, coverage_floor),
        "context_lines": context_pointer_block(task_id, summary_ref),
        "coverage_floor": relevant_coverage_floor(coverage_floor, task_id),
        "workstream": infer_workstream(task_id),
        "context_path": profile["context_path"],
        "operational_prompt_path": "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md",
        "qa_evidence_pattern": profile["qa_evidence_pattern"],
        "security_evidence_pattern": profile["security_evidence_pattern"],
        "handoff_path": profile["handoff_path"],
        "commit_suggestion": profile["commit_suggestion"],
    }


def ollama_models() -> list[str]:
    try:
        with urllib.request.urlopen("http://127.0.0.1:11434/api/tags", timeout=10) as response:
            body = json.loads(response.read().decode("utf-8"))
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError):
        return []
    models = body.get("models", [])
    if not isinstance(models, list):
        return []
    names = [model.get("name") for model in models if isinstance(model, dict)]
    return sorted(name for name in names if isinstance(name, str))


def require_ollama_model(model: str, allow_fallback: bool) -> bool:
    models = ollama_models()
    if model in models:
        return True
    if allow_fallback:
        return False
    installed = ", ".join(models) if models else "none"
    raise SystemExit(
        "Ollama model prerequisite not satisfied. "
        f"Required model: {model}. Installed models: {installed}. "
        f"Install with: ollama pull {model}"
    )


def ollama_plan(context: dict, model: str, allow_fallback: bool) -> tuple[dict, str]:
    """Ask Ollama for a compact plan, then keep only deterministic allowlisted fields."""
    if not require_ollama_model(model, allow_fallback):
        return {}, "fallback_ollama_model_missing"
    payload = {
        "model": model,
        "prompt": (
            "SYSTEM RULES: DEDUPLICACION: if context contains repeated grep/search lines for the "
            "same task id or state, keep only one file reference and never paste repeated matches. "
            "RELEVANCIA: for backend tasks omit frontend/mobile-specific details unless directly "
            "affected; for frontend tasks omit backend-only metrics unless directly affected. "
            "IDIOMA UNIFICADO: produce all generated prompt content in Spanish only. "
            "VALIDADOR PROTEGIDO: execution agents must not modify backlog_validator.py or "
            "tool-registry.md to close product backlog work. "
            "SESION CORTA: no spawn commercial subagents for file exploration; use local tools, "
            "Ollama, subscription-backed CLI or filesystem task ingestion through "
            "commercial_agent_router when external execution is required. "
            "FLUJO DE EJECUCION: respect context.execution_flow. For manual, generate an IDE/task "
            "handoff prompt that the operator can paste into Antigravity, Kiro or another IDE "
            "agent; do not instruct direct CLI execution. For cli, generate a focused prompt for "
            "subscription-backed CLI routing and fall back to manual if the provider is unavailable. "
            "Return only this JSON object shape with short arrays and no markdown: "
            "{\"objectives\":[],\"deliverables\":[],\"closure_criteria\":[]}. "
            "Use the provided canonical context. Do not invent files. Do not add vendor-agent "
            "requirements.\n\n"
            + canonical_json(context)
        ),
        "stream": False,
        "format": "json",
        "options": {
            "temperature": 0,
            "top_p": 0,
            "seed": 42,
            "num_ctx": 4096,
            "num_predict": 256,
        },
    }
    request = urllib.request.Request(
        "http://127.0.0.1:11434/api/generate",
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=DEFAULT_OLLAMA_TIMEOUT_SECONDS) as response:
            body = json.loads(response.read().decode("utf-8"))
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as exc:
        if allow_fallback:
            return {}, f"fallback_ollama_unavailable:{type(exc).__name__}"
        raise SystemExit(f"Ollama orchestration failed: {type(exc).__name__}") from exc

    raw = body.get("response", "")
    parsed = parse_json_object(raw)
    if not parsed:
        if allow_fallback:
            return {}, "fallback_invalid_ollama_json"
        return {}, "ollama_primary_deterministic_prompt"
    allowed = {
        key: parsed.get(key)
        for key in ("objectives", "deliverables", "closure_criteria")
        if isinstance(parsed.get(key), list)
    }
    return allowed, "ollama_primary"


def cache_paths(root: Path, task_id: str) -> tuple[Path, Path]:
    prompt_path = root / DEFAULT_ACTIVE_PROMPT_DIR / f"{task_id}-prompt.md"
    cache_path = root / DEFAULT_ORCHESTRATION_CACHE_DIR / f"{task_id}-prompt-cache.json"
    return prompt_path, cache_path


def archive_other_active_prompts(root: Path, active_prompt_path: Path) -> None:
    active_dir = root / DEFAULT_ACTIVE_PROMPT_DIR
    history_dir = root / DEFAULT_HISTORY_PROMPT_DIR
    if not active_dir.exists():
        return
    history_dir.mkdir(parents=True, exist_ok=True)
    for prompt_path in active_dir.glob("*.md"):
        if prompt_path.resolve() == active_prompt_path.resolve():
            continue
        archived_path = history_dir / prompt_path.name
        if archived_path.exists():
            prompt_path.unlink()
            continue
        shutil.move(str(prompt_path), str(archived_path))


def read_cached_prompt(cache_path: Path, context_hash: str) -> str | None:
    if not cache_path.exists():
        return None
    try:
        cache = json.loads(cache_path.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return None
    if cache.get("context_hash") != context_hash:
        return None
    if cache.get("renderer_version") != PROMPT_RENDERER_VERSION:
        return None
    prompt = cache.get("prompt")
    return prompt if isinstance(prompt, str) else None


def write_cache(cache_path: Path, context_hash: str, prompt: str, mode: str) -> None:
    cache_path.parent.mkdir(parents=True, exist_ok=True)
    cache = {
        "context_hash": context_hash,
        "prompt_hash": sha256_text(prompt),
        "orchestration_mode": mode,
        "renderer_version": PROMPT_RENDERER_VERSION,
        "prompt": prompt,
    }
    cache_path.write_text(json.dumps(cache, ensure_ascii=True, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def build_prompt(
    root: Path,
    task_id: str,
    title: str,
    context_lines: list[str],
    summary_ref: str | None,
    mandatory_notes: list[str],
    coverage_floor: dict,
    execution_flow: str,
    orchestration_mode: str = "ollama_primary",
) -> str:
    profile = task_artifact_profile(task_id)
    pointer_block = "\n".join(f"- {line}" for line in context_pointer_block(task_id, summary_ref))
    pointer_block = f"{pointer_block}\n- Contexto principal: `{profile['context_path']}`"
    notes_block = "\n".join(f"- {note}" for note in compact_mandatory_notes(task_id, title, mandatory_notes, coverage_floor))
    if execution_flow == "cli":
        channel = "CLI con suscripción local"
        execution_rules = "\n".join(
            [
                "- Ejecutar por `tool: commercial_agent_router` usando un proveedor CLI habilitado por el operador.",
                "- No usar proveedores deshabilitados, con cuota agotada o que requieran API keys token-billed.",
                "- Si el CLI no puede ejecutarse por permisos, login o sandbox, el router debe intentar fallback automático con otro proveedor disponible; usar `--execution-flow manual` solo cuando no exista ruta automática viable.",
            ]
        )
    else:
        channel = "Manual / IDE task handoff"
        execution_rules = "\n".join(
            [
                "- Flujo preferente cuando no se permite o no conviene ejecutar CLI desde el orquestador.",
                "- El operador debe entregar este prompt optimizado al IDE/agente local elegido, por ejemplo Antigravity, Kiro u otro entorno con suscripción existente.",
                "- El agente de IDE debe trabajar en `ROOT`, usar `PROJECT` como carpeta objetivo, cerrar el backlog, hacer commit si no hay bloqueantes y ejecutar `tool: backlog_closure_validator` después del commit.",
                "- No invocar CLI comerciales desde este prompt manual; si requiere permisos, login, Docker u otra acción externa, pedir apoyo explícito al operador y continuar cuando quede resuelto.",
            ]
        )
    return f"""# TASK: {task_id} - {title}
ROOT: {root.as_posix()}
PROJECT: {DEFAULT_PROJECT_PATH}
ORCHESTRATION: {orchestration_mode}
EXECUTION_FLOW: {execution_flow}
CHANNEL: {channel}

## 1. Alcance / Objetivos Directos
{notes_block}

## 2. Flujo de Ejecución
{execution_rules}

## 3. Contexto Inmediato (Punteros)
{pointer_block}

## 4. Entregables
- Cambios {infer_workstream(task_id)} y validaciones asociadas.
- QA Evidence: `{profile['qa_evidence_pattern']}`
- Security Evidence: `{profile['security_evidence_pattern']}`
- Transición: crear `{profile['handoff_path']}`.
- Actualizar `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog/prompts, runbook e índices aplicables.

## 5. Criterios de Cierre
- Gates obligatorios ejecutados; Markdown/frontmatter parseable; `git diff --check` limpio.
- Commit: `{profile['commit_suggestion']}`.
- No lanzar subagentes comerciales para exploración, lectura masiva, QA documental o formateo; usar herramientas locales/Ollama y `tool: commercial_agent_router` solo para CLI con suscripción local o task ingestion por archivo. No usar API keys por consumo salvo ADR excepcional.
- Finalizar con protocolo handoff & exit: no pedir ni iniciar el siguiente backlog en el mismo chat/sesión.
- Después del commit, ejecutar `tool: backlog_closure_validator`; la herramienta toma el prompt desde `active_prompt/` sin parámetros.
- El validador debe terminar con código 0, reportar `status: closed`, `Hard findings: 0` y generar evidencia en `08-qa/backlog-validations/{task_id}-closure-validation.md`.
- No modificar `backlog_validator.py` ni `tool-registry.md` para cerrar el backlog; son controles protegidos.
- Si el validador genera `{task_id}-closure-fix-prompt.md` o reporta inconsistencias, no declarar cierre; corregir solo producto/evidencia/registros y repetir commit + validación estricta.
- Máximo 3 intentos de cierre. Si después de 3 intentos el validador sigue fallando, detenerse y reportar hallazgos vigentes, correcciones realizadas y justificación técnica de por qué se considera que debería poder cerrar.
- `git status --short` limpio después del commit y de la validación final.
"""


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate a compact Nexora backlog prompt.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root path.")
    parser.add_argument("--task-id", default=None, help="Backlog task id. Inferred from HOP when omitted.")
    parser.add_argument("--title", default=None, help="Backlog task title. Inferred from HOP when omitted.")
    parser.add_argument("--summary-ref", default=None, help="Previous task summary path.")
    parser.add_argument("--ollama-model", default=DEFAULT_OLLAMA_MODEL, help="Required local Ollama model.")
    parser.add_argument("--allow-deterministic-fallback", action="store_true", help="Allow Python deterministic fallback when Ollama/model is unavailable. Intended only for bootstrap diagnostics.")
    parser.add_argument(
        "--execution-flow",
        choices=EXECUTION_FLOWS,
        default=default_execution_flow(),
        help="Execution flow for the generated prompt. Use manual for IDE handoff or cli for subscription-backed CLI routing.",
    )
    parser.add_argument("--refresh", action="store_true", help="Regenerate the prompt even when the context hash matches the cache.")
    parser.add_argument("--output", default=None, help="Output file for the synthetic prompt. Defaults to the HOP active_prompt folder.")
    parser.add_argument("--stdout", action="store_true", help="Print the prompt content instead of only the output path.")
    parser.add_argument(
        "--paths",
        nargs="*",
        default=[
            "PROJECT_STATE.md",
            "SOURCE_OF_TRUTH.md",
            "projects/healthcare-operations-platform/PROJECT_STATE.md",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md",
        ],
        help="Relative files to inspect with ripgrep.",
    )
    args = parser.parse_args()

    root = Path(args.root).resolve()
    log_event(root, "orchestrator_start", execution_flow=args.execution_flow, refresh=args.refresh)
    inferred_task_id, inferred_title, mandatory_notes, inferred_summary_ref, coverage_floor = infer_active_task(root)
    task_id = args.task_id or inferred_task_id
    title = compact_title(args.title or inferred_title)
    summary_ref = args.summary_ref or inferred_summary_ref
    context = run_rg(root, args.paths, DEFAULT_PATTERNS)
    context_lines = compact_lines(context, task_id, root)
    canonical_context = build_canonical_context(
        root,
        task_id,
        title,
        context_lines,
        summary_ref,
        mandatory_notes,
        coverage_floor,
        args.execution_flow,
    )
    context_hash = sha256_text(canonical_json(canonical_context))
    log_event(root, "canonical_context_built", task_id=task_id, context_hash=context_hash, execution_flow=args.execution_flow)
    default_output_path, cache_path = cache_paths(root, task_id)
    output_path = Path(args.output) if args.output else default_output_path
    cached_prompt = None if args.refresh else read_cached_prompt(cache_path, context_hash)

    if cached_prompt is not None:
        final = cached_prompt
        log_event(root, "prompt_cache_hit", task_id=task_id, cache_path=str(cache_path))
    else:
        ollama_metadata, orchestration_mode = ollama_plan(
            canonical_context,
            args.ollama_model,
            args.allow_deterministic_fallback,
        )
        final = build_prompt(
            root,
            task_id,
            title,
            context_lines,
            summary_ref,
            mandatory_notes,
            coverage_floor,
            args.execution_flow,
            orchestration_mode,
        )
        if ollama_metadata:
            final = final.rstrip() + "\n\n<!-- ollama_plan_hash: " + sha256_text(canonical_json(ollama_metadata)) + " -->\n"
        write_cache(cache_path, context_hash, final, orchestration_mode)
        log_event(root, "prompt_generated", task_id=task_id, orchestration_mode=orchestration_mode, cache_path=str(cache_path))

    output_path.parent.mkdir(parents=True, exist_ok=True)
    if args.output is None:
        archive_other_active_prompts(root, output_path)
    output_path.write_text(final, encoding="utf-8", newline="\n")
    log_event(root, "prompt_written", task_id=task_id, output_path=str(output_path.resolve()))

    if args.stdout:
        print(final)
    else:
        print(output_path.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
