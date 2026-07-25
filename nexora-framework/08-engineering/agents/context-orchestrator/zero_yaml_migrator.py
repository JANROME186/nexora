#!/usr/bin/env python3
"""Convert remaining YAML/YML files to non-YAML repository artifacts.

Documentation, models, registries and evidence are migrated to Markdown with compact frontmatter
plus a structured payload block. Spring Boot configuration is migrated to .properties. Local
runtime compose/collector configuration is migrated to JSON.
"""

from __future__ import annotations

import json
import re
from collections.abc import Iterable
from pathlib import Path
from typing import Any

import yaml


EXCLUDED_PARTS = {".git", "node_modules", "target", "dist", "build", "__pycache__"}
TEXT_SUFFIXES = {
    ".md",
    ".txt",
    ".py",
    ".java",
    ".ts",
    ".tsx",
    ".js",
    ".jsx",
    ".json",
    ".xml",
    ".properties",
    ".sql",
    ".csv",
    ".ps1",
    ".bat",
    ".sh",
    ".html",
    ".css",
    ".scss",
    ".toml",
}
METADATA_KEYS = ("id", "type", "name", "version", "status", "backlog_item", "task_id", "created_date", "updated_date")
STRUCTURED_MARKER = "<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->"


def should_skip(path: Path) -> bool:
    return any(part in EXCLUDED_PARTS for part in path.parts)


def relative(path: Path, root: Path) -> str:
    return path.relative_to(root).as_posix()


def metadata_from(data: Any, fallback_id: str) -> dict[str, Any]:
    metadata: dict[str, Any] = {"id": fallback_id, "format": "markdown_structured_payload"}
    if isinstance(data, dict):
        artifact = data.get("artifact") if isinstance(data.get("artifact"), dict) else {}
        for key in METADATA_KEYS:
            value = data.get(key)
            if value is None:
                value = artifact.get(key)
            if isinstance(value, (str, int, float, bool)):
                metadata[key] = value
    return metadata


def yaml_to_markdown(source: Path, target: Path, data: Any, raw: str) -> str:
    metadata = metadata_from(data, source.stem)
    title = str(metadata.get("name") or metadata.get("id") or source.stem).replace("_", " ").replace("-", " ").title()
    frontmatter = yaml.safe_dump(metadata, sort_keys=False, allow_unicode=True).strip()
    payload = yaml.safe_dump(data, sort_keys=False, allow_unicode=True) if data is not None else raw
    generated = (
        f"---\n{frontmatter}\n---\n\n"
        f"# {title}\n\n"
        f"{STRUCTURED_MARKER}\n\n"
        "## Structured Payload\n\n"
        "```yaml\n"
        f"{payload.rstrip()}\n"
        "```\n"
    )
    if target.exists():
        existing = target.read_text(encoding="utf-8", errors="replace")
        if STRUCTURED_MARKER in existing:
            return re.sub(
                rf"{re.escape(STRUCTURED_MARKER)}[\s\S]*$",
                generated.split("\n\n", 2)[2],
                existing.rstrip(),
            ) + "\n"
        return existing.rstrip() + "\n\n" + generated.split("\n\n", 2)[2]
    return generated


def flatten_properties(prefix: str, value: Any) -> Iterable[tuple[str, str]]:
    if isinstance(value, dict):
        for key, item in value.items():
            next_prefix = f"{prefix}.{key}" if prefix else str(key)
            yield from flatten_properties(next_prefix, item)
    elif isinstance(value, list):
        for index, item in enumerate(value):
            yield from flatten_properties(f"{prefix}[{index}]", item)
    elif value is None:
        yield prefix, ""
    else:
        yield prefix, str(value)


def yaml_to_properties(data: Any) -> str:
    lines = [
        "# Generated from YAML by zero_yaml_migrator.py.",
        "# Do not recreate application.properties/application-local.properties in this repository.",
    ]
    for key, value in flatten_properties("", data):
        lines.append(f"{key}={value}")
    return "\n".join(lines) + "\n"


def target_for(source: Path) -> Path:
    if source.name in {"application.properties", "application-local.properties"}:
        return source.with_suffix(".properties")
    if source.name == "compose.local.json":
        return source.with_suffix(".json")
    if source.name == "otel-collector-config.json":
        return source.with_suffix(".json")
    return source.with_suffix(".md")


def convert_file(source: Path) -> tuple[Path, str]:
    raw = source.read_text(encoding="utf-8", errors="replace")
    data = yaml.safe_load(raw)
    target = target_for(source)
    if target.suffix == ".properties":
        target.write_text(yaml_to_properties(data), encoding="utf-8", newline="\n")
        mode = "properties"
    elif target.suffix == ".json":
        target.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8", newline="\n")
        mode = "json"
    else:
        target.write_text(yaml_to_markdown(source, target, data, raw), encoding="utf-8", newline="\n")
        mode = "markdown_structured_payload"
    source.unlink()
    return target, mode


def build_replacements(mapping: dict[str, str]) -> list[tuple[str, str]]:
    replacements: dict[str, str] = {}
    for old, new in mapping.items():
        replacements[old] = new
        replacements[old.replace("/", "\\")] = new.replace("/", "\\")
        replacements[Path(old).name] = Path(new).name
    return sorted(replacements.items(), key=lambda item: len(item[0]), reverse=True)


def update_references(root: Path, replacements: list[tuple[str, str]]) -> int:
    changed = 0
    for path in root.rglob("*"):
        if not path.is_file() or should_skip(path):
            continue
        if path.suffix.lower() not in TEXT_SUFFIXES:
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        updated = text
        for old, new in replacements:
            updated = updated.replace(old, new)
        if updated != text:
            path.write_text(updated, encoding="utf-8", newline="\n")
            changed += 1
    return changed


def main() -> int:
    root = Path.cwd()
    sources = sorted(
        path
        for path in root.rglob("*")
        if path.is_file() and path.suffix.lower() in {".yaml", ".yml"} and not should_skip(path)
    )
    mapping: dict[str, str] = {}
    modes: dict[str, int] = {}
    for source in sources:
        target, mode = convert_file(source)
        mapping[relative(source, root)] = relative(target, root)
        modes[mode] = modes.get(mode, 0) + 1
    changed_refs = update_references(root, build_replacements(mapping))
    report = root / "projects/healthcare-operations-platform/08-qa/format-migration/zero-yaml-migration-report.md"
    report.parent.mkdir(parents=True, exist_ok=True)
    report.write_text(
        "---\n"
        "id: NXF-FMT-ZERO-YAML-MIGRATION\n"
        "status: completed\n"
        "format: markdown_frontmatter\n"
        "---\n\n"
        "# Zero YAML Migration Report\n\n"
        f"- Converted files: {len(mapping)}\n"
        f"- Reference-updated files: {changed_refs}\n"
        f"- Modes: {json.dumps(modes, sort_keys=True)}\n"
        "- Policy: repository must not contain `.yaml` or `.yml` files after this migration.\n",
        encoding="utf-8",
        newline="\n",
    )
    print(report.resolve())
    print(f"converted={len(mapping)} reference_files={changed_refs} modes={modes}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
