#!/usr/bin/env python3
"""Migrate heavy YAML/Markdown artifacts to Markdown with compact YAML frontmatter.

The tool is intentionally conservative:
- Inventory and deterministic conversion do not use a commercial model.
- Ollama is used only for free-form narrative files when explicitly enabled.
- YAML source files are never deleted unless --apply and --archive-source are provided.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
from collections import Counter
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import yaml


DEFAULT_MODEL = "qwen2.5-coder:0.5b"
PROJECT_PATH = "projects/healthcare-operations-platform"
EXCLUDED_PARTS = {".git", "node_modules", "target", "dist", "build", "__pycache__"}
METADATA_KEYS = (
    "id",
    "task_id",
    "backlog_item",
    "status",
    "type",
    "name",
    "version",
    "created_date",
    "updated_date",
    "next",
    "next_backlog_item",
    "priority",
    "owner",
)


@dataclass(frozen=True)
class Candidate:
    path: Path
    kind: str
    strategy: str
    reason: str
    target: Path


def should_skip(path: Path) -> bool:
    if any(part in EXCLUDED_PARTS for part in path.parts):
        return True
    if path.name.endswith(".bak"):
        return True
    if path.name in {"SOURCE_OF_TRUTH.yaml", "PROJECT_STATE.yaml"}:
        return True
    return False


def relative(path: Path, root: Path) -> str:
    return path.relative_to(root).as_posix()


def classify(path: Path, raw: str, data: Any | None) -> tuple[str, str, str]:
    normalized = path.as_posix().lower()
    if path.suffix.lower() == ".md":
        has_frontmatter = raw.startswith("---\n")
        if has_frontmatter and len(raw) < 4000:
            return "markdown", "skip", "already compact frontmatter markdown"
        return "markdown", "ollama", "free-form markdown narrative"
    if "backlog" in normalized:
        return "backlog", "deterministic", "structured backlog yaml"
    if "security-quality" in normalized or "qa/" in normalized or "validation" in normalized:
        return "qa_evidence", "deterministic", "structured QA/security evidence yaml"
    if "runbook" in normalized:
        return "runbook", "deterministic", "structured runbook yaml"
    if "technical-debt" in normalized:
        return "technical_debt", "deterministic", "structured technical debt yaml"
    if isinstance(data, dict) and len(raw) <= 6000:
        return "structured_yaml", "deterministic", "small structured yaml"
    return "narrative_or_large_yaml", "ollama", "large or narrative yaml"


def frontmatter_metadata(data: dict[str, Any]) -> dict[str, Any]:
    metadata: dict[str, Any] = {}
    artifact = data.get("artifact") if isinstance(data.get("artifact"), dict) else {}
    for key in METADATA_KEYS:
        if key in data and isinstance(data[key], (str, int, float, bool)):
            metadata[key] = data[key]
        elif key in artifact and isinstance(artifact[key], (str, int, float, bool)):
            metadata[key] = artifact[key]
    if "id" not in metadata and "task_id" in metadata:
        metadata["id"] = metadata["task_id"]
    return metadata


def heading(text: str) -> str:
    return str(text).replace("_", " ").replace("-", " ").title()


def render_value(value: Any, depth: int = 0) -> list[str]:
    lines: list[str] = []
    if isinstance(value, dict):
        for key, item in value.items():
            if isinstance(item, (dict, list)):
                lines.append(f"{'  ' * depth}- **{key}**:")
                lines.extend(render_value(item, depth + 1))
            else:
                lines.append(f"{'  ' * depth}- **{key}**: {item}")
    elif isinstance(value, list):
        for item in value:
            if isinstance(item, (dict, list)):
                lines.append(f"{'  ' * depth}-")
                lines.extend(render_value(item, depth + 1))
            else:
                lines.append(f"{'  ' * depth}- {item}")
    elif value is not None:
        lines.append(str(value))
    return lines


def deterministic_yaml_to_markdown(data: dict[str, Any]) -> str:
    metadata = frontmatter_metadata(data)
    body: list[str] = []
    for key, value in data.items():
        if key in METADATA_KEYS and isinstance(value, (str, int, float, bool)):
            continue
        if key == "artifact" and isinstance(value, dict):
            remaining = {k: v for k, v in value.items() if k not in METADATA_KEYS}
            if not remaining:
                continue
            value = remaining
        body.append(f"## {heading(key)}")
        body.extend(render_value(value))
        body.append("")
    frontmatter = yaml.safe_dump(metadata, sort_keys=False, allow_unicode=True).strip()
    return "---\n" + frontmatter + "\n---\n\n" + "\n".join(body).rstrip() + "\n"


def ollama_markdown(raw: str, model: str) -> str:
    try:
        import ollama  # type: ignore
    except ImportError as exc:
        raise RuntimeError(
            "Python package 'ollama' is required for SLLM migration. "
            "Install locally with: python -m pip install ollama"
        ) from exc
    prompt = (
        "Transforma la siguiente informacion a Markdown con YAML frontmatter compacto.\n"
        "Reglas: conserva IDs, estados, fechas, rutas, numeros y nombres exactos; no inventes claves; "
        "usa frontmatter solo para metadatos minimos; usa tablas o bullets compactos en el cuerpo; "
        "responde solo con el Markdown final.\n\n"
        f"ENTRADA:\n{raw}"
    )
    response = ollama.chat(
        model=model,
        messages=[{"role": "user", "content": prompt}],
        options={"temperature": 0, "top_p": 0, "seed": 42},
    )
    return str(response["message"]["content"])


def sha256(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def discover(root: Path, scope: Path, limit: int | None) -> list[Candidate]:
    candidates: list[Candidate] = []
    files = sorted(p for p in scope.rglob("*") if p.suffix.lower() in {".yaml", ".yml", ".md"} and not should_skip(p))
    for path in files:
        raw = path.read_text(encoding="utf-8", errors="replace")
        data = None
        if path.suffix.lower() in {".yaml", ".yml"}:
            try:
                data = yaml.safe_load(raw)
            except yaml.YAMLError:
                data = None
        kind, strategy, reason = classify(path, raw, data)
        target = path.with_suffix(".md")
        candidates.append(Candidate(path=path, kind=kind, strategy=strategy, reason=reason, target=target))
        if limit and len(candidates) >= limit:
            break
    return candidates


def convert_candidate(candidate: Candidate, model: str, use_ollama: bool) -> tuple[str, str]:
    raw = candidate.path.read_text(encoding="utf-8", errors="replace")
    if candidate.strategy == "skip":
        return raw, "skipped"
    if candidate.strategy == "deterministic":
        data = yaml.safe_load(raw)
        if not isinstance(data, dict):
            raise ValueError("deterministic conversion requires a YAML mapping")
        return deterministic_yaml_to_markdown(data), "deterministic"
    if not use_ollama:
        return raw, "planned_ollama_not_executed"
    return ollama_markdown(raw, model), "ollama"


def combine_with_existing(converted: str, existing: str) -> str:
    if not existing.strip():
        return converted
    if existing.startswith("---\n") and converted.startswith("---\n"):
        return converted.rstrip() + "\n\n## Existing Markdown\n\n" + existing.rstrip() + "\n"
    return converted.rstrip() + "\n\n## Existing Markdown\n\n" + existing.rstrip() + "\n"


def update_reference_text(text: str) -> str:
    return re.sub(r"(?<!SOURCE_OF_TRUTH)(?P<path>[\w./\\-]+)\.ya?ml\b", lambda m: m.group("path") + ".md", text)


def validate_references(root: Path, migrated: list[dict[str, str]]) -> list[str]:
    issues: list[str] = []
    migrated_sources = {item["source"] for item in migrated}
    for path in root.rglob("*"):
        if path.suffix.lower() not in {".md", ".yaml", ".yml"} or should_skip(path):
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        for source in migrated_sources:
            if source in text:
                issues.append(f"{relative(path, root)} still references migrated source {source}")
    return issues


def slugify(value: str) -> str:
    return re.sub(r"[^a-zA-Z0-9]+", "-", value.strip("/\\")).strip("-").lower() or "root"


def compact_items(items: list[dict[str, str]], limit: int = 20) -> list[dict[str, str]]:
    return items[:limit]


def count_by(items: list[dict[str, str]], key: str) -> dict[str, int]:
    return dict(sorted(Counter(item.get(key, "unknown") for item in items).items()))


def markdown_table(headers: list[str], rows: list[list[Any]]) -> list[str]:
    lines = ["| " + " | ".join(headers) + " |", "| " + " | ".join("---" for _ in headers) + " |"]
    for row in rows:
        lines.append("| " + " | ".join(str(value) for value in row) + " |")
    return lines


def write_report(root: Path, report: dict[str, Any]) -> Path:
    report_dir = root / PROJECT_PATH / "08-qa/format-migration"
    report_dir.mkdir(parents=True, exist_ok=True)
    scope_slug = slugify(str(report.get("mode", {}).get("scope", "root")))
    report_path = report_dir / f"frontmatter-migration-report-{scope_slug}.md"
    artifact = report["artifact"]
    mode = report["mode"]
    counts = report["counts"]
    inventory = report["inventory"]
    planned = report["planned"]
    written = report["written"]
    collisions = report["collisions"]
    errors = report["errors"]
    reference_issues = report["reference_issues"]

    frontmatter = {
        "id": artifact["id"],
        "type": artifact["type"],
        "status": artifact["status"],
        "scope": mode["scope"],
        "apply": mode["apply"],
        "use_ollama": mode["use_ollama"],
        "archive_source": mode["archive_source"],
        "update_references": mode["update_references"],
        "model": mode["model"],
        "limit": mode["limit"],
        "candidates": counts["candidates"],
        "planned": counts["planned"],
        "written": counts["written"],
        "collisions": counts["collisions"],
        "errors": counts["errors"],
        "reference_issues": counts["reference_issues"],
    }

    lines: list[str] = [
        "---",
        yaml.safe_dump(frontmatter, sort_keys=False, allow_unicode=True).strip(),
        "---",
        "",
        "# Frontmatter Migration Report",
        "",
        "## Summary",
        "",
    ]
    lines.extend(
        markdown_table(
            ["Scope", "Candidates", "Planned", "Written", "Collisions", "Errors", "Reference Issues"],
            [
                [
                    mode["scope"],
                    counts["candidates"],
                    counts["planned"],
                    counts["written"],
                    counts["collisions"],
                    counts["errors"],
                    counts["reference_issues"],
                ]
            ],
        )
    )
    lines.extend(["", "## Strategy Breakdown", ""])
    lines.extend(markdown_table(["Strategy", "Count"], [[key, value] for key, value in count_by(inventory, "strategy").items()]))
    lines.extend(["", "## Artifact Type Breakdown", ""])
    lines.extend(markdown_table(["Kind", "Count"], [[key, value] for key, value in count_by(inventory, "kind").items()]))
    lines.extend(["", "## Samples", ""])
    lines.extend(markdown_table(["Source", "Target", "Strategy"], [[item["source"], item["target"], item["strategy"]] for item in compact_items(inventory, 15)]))

    if written:
        lines.extend(["", "## Written Sample", ""])
        lines.extend(markdown_table(["Source", "Target", "Mode"], [[item["source"], item["target"], item["mode"]] for item in compact_items(written, 15)]))
    if collisions:
        lines.extend(["", "## Collisions", ""])
        lines.extend(markdown_table(["Source", "Target", "Error"], [[item["source"], item["target"], item["error"]] for item in compact_items(collisions, 20)]))
    if errors:
        lines.extend(["", "## Errors", ""])
        lines.extend(markdown_table(["Source", "Error"], [[item["source"], item["error"]] for item in compact_items(errors, 20)]))
    if reference_issues:
        lines.extend(["", "## Reference Issues", ""])
        lines.extend(f"- {issue}" for issue in reference_issues[:50])
    lines.extend(
        [
            "",
            "## Context Policy",
            "",
            "This report is intentionally compact. Detailed per-file inventories are not emitted by default because they increase downstream model context without improving backlog execution.",
            "",
        ]
    )
    report_path.write_text("\n".join(lines), encoding="utf-8", newline="\n")
    return report_path


def main() -> int:
    parser = argparse.ArgumentParser(description="Inventory and migrate heavy YAML/MD artifacts to compact frontmatter Markdown.")
    parser.add_argument("--root", default=os.getcwd(), help="Repository root.")
    parser.add_argument("--scope", default=PROJECT_PATH, help="Relative scope to scan.")
    parser.add_argument("--model", default=DEFAULT_MODEL, help="Ollama model for narrative files.")
    parser.add_argument("--limit", type=int, default=None, help="Limit candidate count for pilot runs.")
    parser.add_argument("--apply", action="store_true", help="Write converted Markdown files.")
    parser.add_argument("--use-ollama", action="store_true", help="Allow local Ollama processing for narrative files.")
    parser.add_argument("--archive-source", action="store_true", help="Rename migrated YAML sources to .bak after successful conversion.")
    parser.add_argument("--combine-existing", action="store_true", help="Combine converted output with an existing Markdown target instead of reporting a collision.")
    parser.add_argument("--update-references", action="store_true", help="Update .yaml/.yml references to .md for migrated files.")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    scope = (root / args.scope).resolve()
    candidates = discover(root, scope, args.limit)
    planned: list[dict[str, str]] = []
    written: list[dict[str, str]] = []
    collisions: list[dict[str, str]] = []
    inventory: list[dict[str, str]] = []
    errors: list[dict[str, str]] = []

    for candidate in candidates:
        source_rel = relative(candidate.path, root)
        target_rel = relative(candidate.target, root)
        inventory.append({"source": source_rel, "target": target_rel, "kind": candidate.kind, "strategy": candidate.strategy, "reason": candidate.reason})
        if candidate.strategy == "skip":
            continue
        try:
            output, mode = convert_candidate(candidate, args.model, args.use_ollama)
            if mode == "planned_ollama_not_executed":
                planned.append({"source": source_rel, "target": target_rel, "mode": mode, "sha256": sha256(output)})
                continue
            planned.append({"source": source_rel, "target": target_rel, "mode": mode, "sha256": sha256(output)})
            if args.apply:
                if candidate.target.exists() and candidate.target != candidate.path and not args.combine_existing:
                    collisions.append({"source": source_rel, "target": target_rel, "error": "target markdown already exists; rerun with --combine-existing after review"})
                    continue
                if candidate.target.exists() and candidate.target != candidate.path and args.combine_existing:
                    output = combine_with_existing(output, candidate.target.read_text(encoding="utf-8", errors="replace"))
                candidate.target.write_text(output, encoding="utf-8", newline="\n")
                if args.archive_source and candidate.path.suffix.lower() in {".yaml", ".yml"}:
                    candidate.path.rename(candidate.path.with_suffix(candidate.path.suffix + ".bak"))
                written.append({"source": source_rel, "target": target_rel, "mode": mode, "sha256": sha256(output)})
        except Exception as exc:
            errors.append({"source": source_rel, "error": str(exc)})

    if args.apply and args.update_references and written:
        for path in root.rglob("*"):
            if path.suffix.lower() not in {".md", ".yaml", ".yml"} or should_skip(path):
                continue
            original = path.read_text(encoding="utf-8", errors="replace")
            updated = update_reference_text(original)
            if updated != original:
                path.write_text(updated, encoding="utf-8", newline="\n")

    reference_issues = validate_references(root, written) if args.apply and written else []
    report = {
        "artifact": {
            "id": "NXF-FMT-MIGRATION-REPORT",
            "type": "frontmatter-migration-report",
            "status": "completed_with_issues" if errors or collisions or reference_issues else "completed",
        },
        "mode": {
            "apply": args.apply,
            "use_ollama": args.use_ollama,
            "archive_source": args.archive_source,
            "update_references": args.update_references,
            "model": args.model,
            "scope": args.scope,
            "limit": args.limit,
        },
        "counts": {
            "candidates": len(candidates),
            "planned": len(planned),
            "written": len(written),
            "collisions": len(collisions),
            "errors": len(errors),
            "reference_issues": len(reference_issues),
        },
        "inventory": inventory,
        "planned": planned,
        "written": written,
        "collisions": collisions,
        "errors": errors,
        "reference_issues": reference_issues,
    }
    report_path = write_report(root, report)
    print(report_path.resolve())
    return 1 if errors or reference_issues else 0


if __name__ == "__main__":
    raise SystemExit(main())
