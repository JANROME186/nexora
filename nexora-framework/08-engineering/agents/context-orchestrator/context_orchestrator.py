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
    "HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml"
)
DEFAULT_HOP_BACKLOG_FILE = (
    "projects/healthcare-operations-platform/06-delivery/commercial-product/"
    "HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml"
)
DEFAULT_PROJECT_PATH = "projects/healthcare-operations-platform"
DEFAULT_HANDOFF_DIR = "projects/healthcare-operations-platform/08-qa/handoffs"
DEFAULT_PROMPT_OUTPUT_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts"
DEFAULT_ORCHESTRATION_CACHE_DIR = "projects/healthcare-operations-platform/08-qa/generated-prompts/cache"
DEFAULT_OLLAMA_MODEL = "qwen2.5-coder:0.5b"
DEFAULT_OLLAMA_TIMEOUT_SECONDS = 300


def read_yaml(path: Path) -> dict:
    if not path.exists():
        return {}
    data = yaml.safe_load(path.read_text(encoding="utf-8"))
    return data if isinstance(data, dict) else {}


def infer_active_task(root: Path) -> tuple[str, str, list[str], str | None, dict]:
    prompt_data = read_yaml(root / DEFAULT_HOP_PROMPT_FILE)
    active_block = prompt_data
    validation_commands = prompt_data.get("validation_commands")
    if isinstance(validation_commands, dict) and validation_commands.get("backlog_item_id"):
        active_block = validation_commands
    task_id = active_block.get("backlog_item_id")
    title = active_block.get("name")
    notes = active_block.get("mandatory_execution_notes") or []
    coverage_floor = active_block.get("coverage_floor") or {}
    previous = active_block.get("previous_backlog_item") or {}
    summary_ref = None
    previous_id = previous.get("backlog_item_id") if isinstance(previous, dict) else None
    if previous_id:
        candidate = root / DEFAULT_HANDOFF_DIR / f"{previous_id}-summary.md"
        if candidate.exists():
            summary_ref = candidate.relative_to(root).as_posix()

    if task_id and title:
        return str(task_id), str(title), stringify_notes(notes), summary_ref, coverage_floor if isinstance(coverage_floor, dict) else {}

    backlog_data = read_yaml(root / DEFAULT_HOP_BACKLOG_FILE)
    baseline = ((backlog_data.get("product") or {}).get("current_baseline") or {})
    task_id = baseline.get("active_backlog_item")
    title = "Active HOP backlog item"
    if task_id:
        title = find_backlog_title(backlog_data, str(task_id)) or title
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
        result.append("Migrar/optimizar artefactos YAML/MD pesados a Markdown con frontmatter compacto.")
        result.append("Usar solo Python, PyYAML y Ollama local; no consumir tokens comerciales.")
    elif task_id == "COM-MOD-017-BE-002":
        result.append("Implementar enforcement custom de entitlements para instalación, activación y consumo runtime de paquetes marketplace.")
        result.append("Implementar boundary provider-agnostic para billing sin acoplar HOP a un proveedor propietario.")
        result.append("Retomar desde el handoff compacto de NXF-FMT-002; no precargar inventarios YAML amplios.")
    elif workstream == "backend":
        result.append(
            "Compilar outputs backend para marketplace catalog, package manifest, offer, "
            "license plan, entitlement, installation y billing-adapter."
        )
    else:
        result.append(f"Atender el backlog activo: {title}.")

    result.append("Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.")
    if workstream == "format_migration":
        result.append("Cerrar o reducir TD-FMT-001 como deuda bloqueante de formato antes de reanudar HOP.")
    elif coverage:
        result.append(f"Preservar piso de cobertura {coverage}.")
    elif coverage_floor.get("final_target_percent") is not None:
        result.append(f"Preservar o mejorar cobertura; objetivo final >= {coverage_floor['final_target_percent']}%.")
    if workstream != "format_migration":
        result.append("Revisar deuda técnica abierta y reducir al menos 1 item aplicable antes del feature work.")

    gate_by_workstream = {
        "format_migration": "Ejecutar inventario, piloto, conversión por lotes, validación de referencias, parseo YAML y git diff --check.",
        "backend": "Ejecutar gates backend obligatorios: Maven, Java, Docker/BD local, SAST, dependencias, cobertura y scans de seguridad.",
        "frontend": "Ejecutar gates frontend obligatorios: typecheck, tests/cobertura, build, SAST, dependencias, i18n y scans de seguridad.",
        "mobile": "Ejecutar gates app/mobile obligatorios: typecheck, tests/cobertura, build, SAST, dependencias, i18n y scans de seguridad.",
        "quality": "Ejecutar gates de cierre, punteros, evidencias, deuda técnica, seguridad, cobertura y estado git.",
        "definition": "Ejecutar gates documentales: YAML/MD parseable, trazabilidad, punteros, deuda técnica y estado git.",
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
            "Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml` y `PROJECT_STATE.yaml` bajo demanda.",
        ]
    )
    return pointers


def task_artifact_profile(task_id: str) -> dict[str, str]:
    if task_id.startswith("NXF-FMT"):
        return {
            "context_path": "../../nexora-framework/02-standards/standards/frontmatter-artifact-migration-standard.yaml",
            "qa_evidence_pattern": f"08-qa/format-migration/{task_id}-validation.md",
            "security_evidence_pattern": "not_applicable_format_migration_no_runtime_code",
            "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
            "commit_suggestion": "chore(framework): optimize artifact formats",
        }
    if task_id == "COM-MOD-017-BE-002":
        return {
            "context_path": "01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/",
            "qa_evidence_pattern": f"08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.md/yaml",
            "security_evidence_pattern": f"08-qa/security-quality/{task_id}/security-quality-evidence.md/yaml",
            "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
            "commit_suggestion": "feat(hop): implement marketplace entitlement enforcement",
        }
    return {
        "context_path": "01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/",
        "qa_evidence_pattern": f"08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.md/yaml",
        "security_evidence_pattern": f"08-qa/security-quality/{task_id}/security-quality-evidence.md/yaml",
        "handoff_path": f"08-qa/handoffs/{task_id}-summary.md",
        "commit_suggestion": "feat(hop): compile marketplace backend outputs",
    }


def canonical_json(data: object) -> str:
    return json.dumps(data, ensure_ascii=True, sort_keys=True, separators=(",", ":"))


def sha256_text(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


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
) -> dict:
    profile = task_artifact_profile(task_id)
    return {
        "root": root.as_posix(),
        "project": DEFAULT_PROJECT_PATH,
        "task_id": task_id,
        "title": title,
        "summary_ref": summary_ref,
        "mandatory_notes": compact_mandatory_notes(task_id, title, mandatory_notes, coverage_floor),
        "context_lines": context_pointer_block(task_id, summary_ref),
        "coverage_floor": relevant_coverage_floor(coverage_floor, task_id),
        "workstream": infer_workstream(task_id),
        "context_path": profile["context_path"],
        "operational_prompt_path": "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml",
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
    prompt_path = root / DEFAULT_PROMPT_OUTPUT_DIR / f"{task_id}-prompt.md"
    cache_path = root / DEFAULT_ORCHESTRATION_CACHE_DIR / f"{task_id}-prompt-cache.json"
    return prompt_path, cache_path


def read_cached_prompt(cache_path: Path, context_hash: str) -> str | None:
    if not cache_path.exists():
        return None
    try:
        cache = json.loads(cache_path.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return None
    if cache.get("context_hash") != context_hash:
        return None
    prompt = cache.get("prompt")
    return prompt if isinstance(prompt, str) else None


def write_cache(cache_path: Path, context_hash: str, prompt: str, mode: str) -> None:
    cache_path.parent.mkdir(parents=True, exist_ok=True)
    cache = {
        "context_hash": context_hash,
        "prompt_hash": sha256_text(prompt),
        "orchestration_mode": mode,
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
    orchestration_mode: str = "ollama_primary",
) -> str:
    profile = task_artifact_profile(task_id)
    pointer_block = "\n".join(f"- {line}" for line in context_pointer_block(task_id, summary_ref))
    pointer_block = f"{pointer_block}\n- Contexto principal: `{profile['context_path']}`"
    notes_block = "\n".join(f"- {note}" for note in compact_mandatory_notes(task_id, title, mandatory_notes, coverage_floor))
    return f"""# TASK: {task_id} - {title}
ROOT: {root.as_posix()}
PROJECT: {DEFAULT_PROJECT_PATH}
ORCHESTRATION: {orchestration_mode}

## 1. Alcance / Objetivos Directos
{notes_block}

## 2. Contexto Inmediato (Punteros)
{pointer_block}

## 3. Entregables
- Cambios {infer_workstream(task_id)} y validaciones asociadas.
- QA Evidence: `{profile['qa_evidence_pattern']}`
- Security Evidence: `{profile['security_evidence_pattern']}`
- Transición: crear `{profile['handoff_path']}`.
- Actualizar `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; YAML/MD parseables; `git diff --check` limpio.
- Commit: `{profile['commit_suggestion']}`.
- `git status --short` limpio si no hay bloqueantes.
"""


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate a compact Nexora backlog prompt.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root path.")
    parser.add_argument("--task-id", default=None, help="Backlog task id. Inferred from HOP when omitted.")
    parser.add_argument("--title", default=None, help="Backlog task title. Inferred from HOP when omitted.")
    parser.add_argument("--summary-ref", default=None, help="Previous task summary path.")
    parser.add_argument("--ollama-model", default=DEFAULT_OLLAMA_MODEL, help="Required local Ollama model.")
    parser.add_argument("--allow-deterministic-fallback", action="store_true", help="Allow Python deterministic fallback when Ollama/model is unavailable. Intended only for bootstrap diagnostics.")
    parser.add_argument("--refresh", action="store_true", help="Regenerate the prompt even when the context hash matches the cache.")
    parser.add_argument("--output", default=None, help="Output file for the synthetic prompt. Defaults to the HOP generated-prompts folder.")
    parser.add_argument("--stdout", action="store_true", help="Print the prompt content instead of only the output path.")
    parser.add_argument(
        "--paths",
        nargs="*",
        default=[
            "PROJECT_STATE.yaml",
            "SOURCE_OF_TRUTH.yaml",
            "projects/healthcare-operations-platform/PROJECT_STATE.yaml",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml",
            "projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml",
        ],
        help="Relative files to inspect with ripgrep.",
    )
    args = parser.parse_args()

    root = Path(args.root).resolve()
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
    )
    context_hash = sha256_text(canonical_json(canonical_context))
    default_output_path, cache_path = cache_paths(root, task_id)
    output_path = Path(args.output) if args.output else default_output_path
    cached_prompt = None if args.refresh else read_cached_prompt(cache_path, context_hash)

    if cached_prompt is not None:
        final = cached_prompt
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
            orchestration_mode,
        )
        if ollama_metadata:
            final = final.rstrip() + "\n\n<!-- ollama_plan_hash: " + sha256_text(canonical_json(ollama_metadata)) + " -->\n"
        write_cache(cache_path, context_hash, final, orchestration_mode)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(final, encoding="utf-8", newline="\n")

    if args.stdout:
        print(final)
    else:
        print(output_path.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
