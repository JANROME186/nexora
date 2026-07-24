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


DEFAULT_PATTERNS = (
    "active_backlog_item",
    "current_backlog_item",
    "next_backlog_item",
    "backlog_item_id",
    "mandatory_execution_notes",
    "previous_backlog_item",
)


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


def build_prompt(root: Path, task_id: str, title: str, context_lines: list[str], summary_ref: str | None) -> str:
    pointer_block = "\n".join(f"- {line}" for line in context_lines[:16]) or "- Inspeccionar punteros activos con rg antes de editar."
    if summary_ref:
        pointer_block = f"- Handoff previo: {summary_ref}\n{pointer_block}"
    return f"""# TASK: {task_id} - {title}
ROOT: {root.as_posix()}

## 1. Alcance / Objetivos Directos
- Ejecutar solo el backlog activo.
- Usar lazy loading: no pegar archivos completos; inspeccionar secciones puntuales con rg/read.
- Mantener ejecucion agent-agnostic y validar deuda tecnica, calidad, seguridad, cobertura y punteros.

## 2. Contexto Inmediato (Punteros)
{pointer_block}

## 3. Entregables
- Cambios del backlog activo y evidencias requeridas.
- Actualizar registros/punteros aplicables.
- Crear {task_id}-summary.md con Status, Cambios Clave, Deuda Tecnica Creada y Siguiente Paso.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados o bloqueo formal sin avanzar punteros.
- YAML/Markdown/frontmatter parseable segun aplique, git diff --check limpio.
- Commit Conventional Commit y git status --short limpio si no hay bloqueantes.
"""


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate a compact Nexora backlog prompt.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root path.")
    parser.add_argument("--task-id", required=True, help="Backlog task id.")
    parser.add_argument("--title", required=True, help="Backlog task title.")
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
    context = run_rg(root, args.paths, DEFAULT_PATTERNS)
    draft = build_prompt(root, args.task_id, args.title, compact_lines(context, args.task_id, root), args.summary_ref)
    compressed = ollama_compress(draft, args.ollama_model)
    final = compressed if compressed else draft

    if args.output:
        Path(args.output).write_text(final, encoding="utf-8")
    else:
        print(final)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
