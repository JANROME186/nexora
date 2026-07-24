#!/usr/bin/env python3
"""Generate compact backlog prompts with optional local Ollama preprocessing.

The script intentionally works without Ollama. When Ollama is available, it can compress the draft
prompt; otherwise it uses deterministic local heuristics so the framework stays agent agnostic.
"""

from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
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
DEFAULT_HANDOFF_DIR = "projects/healthcare-operations-platform/08-qa/handoffs"


def read_yaml(path: Path) -> dict:
    if not path.exists():
        return {}
    data = yaml.safe_load(path.read_text(encoding="utf-8"))
    return data if isinstance(data, dict) else {}


def infer_active_task(root: Path) -> tuple[str, str, list[str], str | None]:
    prompt_data = read_yaml(root / DEFAULT_HOP_PROMPT_FILE)
    active_block = prompt_data
    validation_commands = prompt_data.get("validation_commands")
    if isinstance(validation_commands, dict) and validation_commands.get("backlog_item_id"):
        active_block = validation_commands
    task_id = active_block.get("backlog_item_id")
    title = active_block.get("name")
    notes = active_block.get("mandatory_execution_notes") or []
    previous = active_block.get("previous_backlog_item") or {}
    summary_ref = None
    previous_id = previous.get("backlog_item_id") if isinstance(previous, dict) else None
    if previous_id:
        candidate = root / DEFAULT_HANDOFF_DIR / f"{previous_id}-summary.md"
        if candidate.exists():
            summary_ref = candidate.relative_to(root).as_posix()

    if task_id and title:
        return str(task_id), str(title), stringify_notes(notes), summary_ref

    backlog_data = read_yaml(root / DEFAULT_HOP_BACKLOG_FILE)
    baseline = ((backlog_data.get("product") or {}).get("current_baseline") or {})
    task_id = baseline.get("active_backlog_item")
    title = "Active HOP backlog item"
    if task_id:
        title = find_backlog_title(backlog_data, str(task_id)) or title
    if not task_id:
        raise SystemExit("Cannot infer active backlog item. Provide --task-id and --title.")
    return str(task_id), title, [], summary_ref


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
    return (focused or lines)[:limit]


def ollama_compress(text: str, model: str) -> str | None:
    if not shutil.which("ollama"):
        return None
    prompt = (
        "Compress this backlog context into a precise execution prompt. "
        "Keep only task, root, pointers, deliverables and closure criteria. "
        "Do not add vendor-agent requirements.\n\n"
        + text
    )
    result = subprocess.run(
        ["ollama", "run", model],
        input=prompt,
        text=True,
        capture_output=True,
        check=False,
        timeout=120,
    )
    if result.returncode != 0:
        return None
    return result.stdout.strip()


def build_prompt(
    root: Path,
    task_id: str,
    title: str,
    context_lines: list[str],
    summary_ref: str | None,
    mandatory_notes: list[str],
) -> str:
    pointer_block = "\n".join(f"- {line}" for line in context_lines[:16]) or "- Inspeccionar punteros activos con rg antes de editar."
    if summary_ref:
        pointer_block = f"- Handoff previo: {summary_ref}\n{pointer_block}"
    notes_block = "\n".join(f"- {note}" for note in mandatory_notes[:8])
    if not notes_block:
        notes_block = "- Cargar el bloque activo del prompt operativo y ejecutar solo sus actividades."
    return f"""# TASK: {task_id} - {title}
ROOT: {root.as_posix()}

## 1. Alcance / Objetivos Directos
- Ejecutar solo el backlog activo.
- Usar lazy loading: no pegar archivos completos; inspeccionar secciones puntuales con rg/read.
- Mantener ejecucion agent-agnostic y validar deuda tecnica, calidad, seguridad, cobertura y punteros.
- Actividades obligatorias:
{notes_block}

## 2. Contexto Inmediato (Punteros)
{pointer_block}
- Modelos base: projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/
- Prompt operativo: projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml

## 3. Entregables
- Cambios del backlog activo y evidencias requeridas.
- QA: projects/healthcare-operations-platform/08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.md/yaml.
- Security: projects/healthcare-operations-platform/08-qa/security-quality/{task_id}/security-quality-evidence.md/yaml.
- Actualizar PROJECT_STATE, SOURCE_OF_TRUTH, backlog/prompts, runbook e indices aplicables.
- Crear projects/healthcare-operations-platform/08-qa/handoffs/{task_id}-summary.md con Status, Cambios Clave, Deuda Tecnica Creada y Siguiente Paso.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados o bloqueo formal sin avanzar punteros.
- YAML/Markdown/frontmatter parseable segun aplique, git diff --check limpio.
- Commit sugerido: feat(hop): compile marketplace backend outputs.
- git status --short limpio si no hay bloqueantes.
"""


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate a compact Nexora backlog prompt.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root path.")
    parser.add_argument("--task-id", default=None, help="Backlog task id. Inferred from HOP when omitted.")
    parser.add_argument("--title", default=None, help="Backlog task title. Inferred from HOP when omitted.")
    parser.add_argument("--summary-ref", default=None, help="Previous task summary path.")
    parser.add_argument("--ollama-model", default="llama3.2", help="Optional Ollama model.")
    parser.add_argument("--output", default=None, help="Output file for the synthetic prompt.")
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
    inferred_task_id, inferred_title, mandatory_notes, inferred_summary_ref = infer_active_task(root)
    task_id = args.task_id or inferred_task_id
    title = args.title or inferred_title
    summary_ref = args.summary_ref or inferred_summary_ref
    context = run_rg(root, args.paths, DEFAULT_PATTERNS)
    draft = build_prompt(
        root,
        task_id,
        title,
        compact_lines(context, task_id, root),
        summary_ref,
        mandatory_notes,
    )
    compressed = ollama_compress(draft, args.ollama_model)
    final = compressed if compressed else draft

    if args.output:
        Path(args.output).write_text(final, encoding="utf-8")
    else:
        print(final)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
