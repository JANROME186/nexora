#!/usr/bin/env python3
"""Validate whether a generated backlog prompt was fully closed.

The validator uses deterministic repository checks as the source of truth and Ollama as the
mandatory local summarizer for the closure decision. If closure is incomplete, it writes a compact
follow-up prompt with only the missing work.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import subprocess
import urllib.error
import urllib.request
from pathlib import Path

import yaml


PROJECT_PATH = "projects/healthcare-operations-platform"
DEFAULT_MODEL = "qwen2.5-coder:0.5b"
DEFAULT_TIMEOUT_SECONDS = 300


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8") if path.exists() else ""


def read_yaml(path: Path) -> dict:
    if not path.exists():
        return {}
    data = yaml.safe_load(path.read_text(encoding="utf-8"))
    return data if isinstance(data, dict) else {}


def nested_get(data: object, *keys: str) -> object:
    current = data
    for key in keys:
        if not isinstance(current, dict):
            return None
        current = current.get(key)
    return current


def parse_task_id_from_prompt(prompt: str) -> str | None:
    match = re.search(r"^# TASK:\s+([A-Z0-9-]+)\s+-", prompt, re.MULTILINE)
    return match.group(1) if match else None


def project_file(root: Path, relative_path: str) -> Path:
    return root / PROJECT_PATH / relative_path


def git_status(root: Path) -> str:
    result = subprocess.run(
        ["git", "status", "--short"],
        cwd=root,
        text=True,
        capture_output=True,
        check=False,
    )
    return result.stdout.strip()


def git_head(root: Path) -> str:
    result = subprocess.run(
        ["git", "rev-parse", "--short", "HEAD"],
        cwd=root,
        text=True,
        capture_output=True,
        check=False,
    )
    return result.stdout.strip()


def ollama_models() -> list[str]:
    try:
        with urllib.request.urlopen("http://127.0.0.1:11434/api/tags", timeout=10) as response:
            body = json.loads(response.read().decode("utf-8"))
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError):
        return []
    models = body.get("models", [])
    if not isinstance(models, list):
        return []
    return sorted(model["name"] for model in models if isinstance(model, dict) and isinstance(model.get("name"), str))


def require_ollama_model(model: str) -> None:
    models = ollama_models()
    if model in models:
        return
    installed = ", ".join(models) if models else "none"
    raise SystemExit(
        "Ollama model prerequisite not satisfied. "
        f"Required model: {model}. Installed models: {installed}. "
        f"Install with: ollama pull {model}"
    )


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


def ollama_review(context: dict, model: str) -> dict:
    require_ollama_model(model)
    payload = {
        "model": model,
        "stream": False,
        "format": "json",
        "prompt": (
            "Devuelve solo JSON compacto con esta forma: "
            "{\"decision\":\"closed|incomplete\",\"summary\":\"...\",\"top_risks\":[],\"required_actions\":[]}. "
            "Usa las reglas deterministicas como fuente de verdad. Si hard_findings no esta vacio, "
            "decision debe ser incomplete. No inventes archivos ni ejecuciones.\n\n"
            + json.dumps(context, ensure_ascii=True, sort_keys=True, separators=(",", ":"))
        ),
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
        with urllib.request.urlopen(request, timeout=DEFAULT_TIMEOUT_SECONDS) as response:
            body = json.loads(response.read().decode("utf-8"))
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as exc:
        raise SystemExit(f"Ollama closure validation failed: {type(exc).__name__}") from exc
    parsed = parse_json_object(str(body.get("response", "")))
    return parsed if parsed else {"decision": "incomplete", "summary": "Ollama returned invalid JSON.", "top_risks": [], "required_actions": []}


def find_backlog_item_status(data: object, task_id: str) -> str | None:
    stack: list[object] = [data]
    while stack:
        current = stack.pop()
        if isinstance(current, dict):
            if current.get("id") == task_id:
                status = current.get("status")
                return str(status) if status is not None else None
            stack.extend(current.values())
        elif isinstance(current, list):
            stack.extend(current)
    return None


def build_context(root: Path, task_id: str, prompt_path: Path, require_clean_git: bool) -> dict:
    project_state = read_yaml(root / PROJECT_PATH / "PROJECT_STATE.yaml")
    product_backlog = read_yaml(root / PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml")
    execution_prompts = read_yaml(root / PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml")
    source_of_truth = read_yaml(root / PROJECT_PATH / "SOURCE_OF_TRUTH.yaml")

    qa_path = project_file(root, f"08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.yaml")
    security_path = project_file(root, f"08-qa/security-quality/{task_id}/security-quality-evidence.yaml")
    handoff_path = project_file(root, f"08-qa/handoffs/{task_id}-summary.md")
    prompt_text = read_text(prompt_path)
    qa = read_yaml(qa_path)
    security = read_yaml(security_path)

    current_state_active = nested_get(project_state, "commercial_product_progress", "active_backlog_item")
    project_state_next = nested_get(project_state, "delivery_readiness", "next_backlog_item")
    product_baseline_active = nested_get(product_backlog, "product", "current_baseline", "active_backlog_item")
    product_backlog_status = find_backlog_item_status(product_backlog, task_id)
    execution_previous = nested_get(execution_prompts, "validation_commands", "previous_backlog_item", "backlog_item_id")
    execution_previous_status = nested_get(execution_prompts, "validation_commands", "previous_backlog_item", "status")

    hard_findings: list[dict] = []
    expected_files = {
        "prompt": prompt_path,
        "qa_evidence": qa_path,
        "security_evidence": security_path,
        "handoff": handoff_path,
    }
    for name, path in expected_files.items():
        if not path.exists():
            hard_findings.append({"id": f"missing_{name}", "severity": "P0", "detail": str(path)})

    if parse_task_id_from_prompt(prompt_text) != task_id:
        hard_findings.append({"id": "prompt_task_mismatch", "severity": "P0", "detail": "Prompt TASK header does not match requested task."})
    qa_status = qa.get("status") or nested_get(qa, "artifact", "status")
    qa_backlog_item = qa.get("backlog_item") or nested_get(qa, "artifact", "backlog_item")
    if qa_status != "validated" or qa_backlog_item != task_id:
        hard_findings.append({"id": "qa_evidence_not_validated", "severity": "P0", "detail": "QA evidence must be status validated and match backlog_item."})
    if nested_get(security, "artifact", "status") != "validated" or nested_get(security, "artifact", "backlog_item") != task_id:
        hard_findings.append({"id": "security_evidence_not_validated", "severity": "P0", "detail": "Security evidence must be status validated and match backlog_item."})
    if product_backlog_status != "closed":
        hard_findings.append({"id": "product_backlog_item_not_closed", "severity": "P0", "detail": f"Expected closed, found {product_backlog_status}."})
    if product_baseline_active == task_id:
        hard_findings.append({"id": "product_baseline_stale_active_item", "severity": "P0", "detail": "HOP commercial product backlog baseline still points to the closed task."})
    if current_state_active == task_id:
        hard_findings.append({"id": "project_state_stale_active_item", "severity": "P0", "detail": "PROJECT_STATE commercial_product_delivery still points to the closed task."})
    if execution_previous != task_id or execution_previous_status != "closed":
        hard_findings.append({"id": "execution_prompt_previous_not_closed", "severity": "P0", "detail": "Execution prompt must carry the validated task as previous_backlog_item closed."})

    source_values = set((source_of_truth.get("sources") or {}).values()) if isinstance(source_of_truth.get("sources"), dict) else set()
    for relative in (
        f"08-qa/qa/product-marketplace-and-extension-packaging/{task_id}-validation.yaml",
        f"08-qa/security-quality/{task_id}/security-quality-evidence.yaml",
        f"08-qa/handoffs/{task_id}-summary.md",
    ):
        if relative not in source_values:
            hard_findings.append({"id": "source_of_truth_missing_reference", "severity": "P1", "detail": relative})

    git_dirty = git_status(root)
    git_clean_value: object = not bool(git_dirty)
    if not require_clean_git:
        git_clean_value = "not_required_diagnostic_mode"
    if require_clean_git and git_dirty:
        hard_findings.append({"id": "git_worktree_not_clean", "severity": "P0", "detail": git_dirty[:800]})

    return {
        "task_id": task_id,
        "project": PROJECT_PATH,
        "prompt_path": str(prompt_path.relative_to(root)).replace("\\", "/"),
        "qa_evidence_exists": qa_path.exists(),
        "qa_status": qa_status,
        "security_evidence_exists": security_path.exists(),
        "security_status": nested_get(security, "artifact", "status"),
        "handoff_exists": handoff_path.exists(),
        "project_state_active_backlog_item": current_state_active,
        "project_state_next_backlog_item": project_state_next,
        "product_backlog_current_baseline_active": product_baseline_active,
        "product_backlog_item_status": product_backlog_status,
        "execution_prompt_previous_backlog_item": execution_previous,
        "execution_prompt_previous_status": execution_previous_status,
        "source_of_truth_checked": True,
        "git_head": git_head(root),
        "git_clean": git_clean_value,
        "hard_findings": hard_findings,
    }


def correction_prompt(task_id: str, context: dict, review: dict) -> str:
    findings = context["hard_findings"]
    finding_lines = "\n".join(f"- {item['id']}: {item['detail']}" for item in findings)
    if not finding_lines:
        finding_lines = "- Revalidar cierre; no hay hallazgos duros registrados."
    return f"""# TASK: {task_id} - Cierre Correctivo de Backlog
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: {PROJECT_PATH}

## 1. Objetivo
- Corregir únicamente los hallazgos que impiden cerrar `{task_id}`.
- No implementar funcionalidad nueva fuera del cierre.
- Mantener ejecución agent-agnostic y no avanzar punteros hasta que todo quede consistente.

## 2. Hallazgos a Cerrar
{finding_lines}

## 3. Acciones Obligatorias
- Sincronizar `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, `06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml` y `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml`.
- Confirmar que QA Evidence, Security Evidence y handoff de `{task_id}` existan, estén validados y estén referenciados en `SOURCE_OF_TRUTH.yaml`.
- Ejecutar parseo YAML del proyecto, sweep de punteros obsoletos y `git diff --check`.
- Si no hay bloqueantes, hacer commit y dejar `git status --short` limpio.

## 4. Criterios de Cierre
- El validador local reporta `decision: closed`.
- No existen hallazgos P0/P1 sin registrar o corregir.
- El siguiente backlog activo queda alineado en todos los registros.

<!-- local_ollama_review: {str(review.get('decision', 'incomplete'))} -->
"""


def write_outputs(root: Path, task_id: str, context: dict, review: dict) -> tuple[Path, Path | None]:
    out_dir = root / PROJECT_PATH / "08-qa/backlog-validations"
    out_dir.mkdir(parents=True, exist_ok=True)
    report_yaml = out_dir / f"{task_id}-closure-validation.yaml"
    report_md = out_dir / f"{task_id}-closure-validation.md"
    decision = "closed" if not context["hard_findings"] and review.get("decision") == "closed" else "incomplete"
    report = {
        "artifact": {
            "id": f"{task_id}-closure-validation",
            "type": "backlog-closure-validation",
            "status": decision,
            "model": DEFAULT_MODEL,
        },
        "context": context,
        "ollama_review": review,
    }
    report_yaml.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    report_md.write_text(
        f"# {task_id} Closure Validation\n\n"
        f"Status: `{decision}`\n\n"
        f"Hard findings: `{len(context['hard_findings'])}`\n\n"
        f"Ollama summary: {review.get('summary', '')}\n",
        encoding="utf-8",
        newline="\n",
    )
    prompt_path = None
    if decision != "closed":
        prompt_path = root / PROJECT_PATH / "08-qa/generated-prompts" / f"{task_id}-closure-fix-prompt.md"
        prompt_path.write_text(correction_prompt(task_id, context, review), encoding="utf-8", newline="\n")
    return report_yaml, prompt_path


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate closure for a generated Nexora backlog prompt.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root path.")
    parser.add_argument("--task-id", default=None, help="Backlog task id. Inferred from prompt when omitted.")
    parser.add_argument("--prompt", default=None, help="Generated prompt path. Defaults to active COM-MOD prompt if task id is inferred.")
    parser.add_argument("--ollama-model", default=DEFAULT_MODEL, help="Required local Ollama model.")
    parser.add_argument("--no-require-clean-git", action="store_true", help="Diagnostic mode only: do not fail closure because the worktree is dirty.")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    prompt_path = Path(args.prompt).resolve() if args.prompt else root / PROJECT_PATH / "08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md"
    prompt_text = read_text(prompt_path)
    task_id = args.task_id or parse_task_id_from_prompt(prompt_text)
    if not task_id:
        raise SystemExit("Cannot infer task id. Provide --task-id or a generated prompt with '# TASK: <ID> - ...'.")

    context = build_context(root, task_id, prompt_path, require_clean_git=not args.no_require_clean_git)
    review = ollama_review(context, args.ollama_model)
    if context["hard_findings"]:
        review["decision"] = "incomplete"
    report_path, prompt_fix_path = write_outputs(root, task_id, context, review)
    print(report_path.resolve())
    if prompt_fix_path:
        print(prompt_fix_path.resolve())
    return 0 if review.get("decision") == "closed" and not context["hard_findings"] else 2


if __name__ == "__main__":
    raise SystemExit(main())
