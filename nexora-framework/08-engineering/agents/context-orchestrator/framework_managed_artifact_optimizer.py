#!/usr/bin/env python3
"""Split framework-managed HOP tracking artifacts into compact indexes and atomic records."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import subprocess
from collections import defaultdict
from pathlib import Path
from typing import Any

import yaml


PROJECT_PATH = Path("projects/healthcare-operations-platform")
MARKER = "<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->"
YAML_FENCE = "```yaml\n"


def extract_payload(text: str) -> dict[str, Any]:
    start = text.find(YAML_FENCE)
    if start == -1:
        return {}
    start += len(YAML_FENCE)
    end = text.find("\n```", start)
    if end == -1:
        return {}
    data = yaml.safe_load(text[start:end])
    return data if isinstance(data, dict) else {}


def read_payload(path: Path) -> dict[str, Any]:
    return extract_payload(path.read_text(encoding="utf-8")) if path.exists() else {}


def read_payload_from_git_head(root: Path, relative_path: Path) -> dict[str, Any]:
    result = subprocess.run(
        ["git", "show", f"HEAD:{relative_path.as_posix()}"],
        cwd=root,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        return {}
    return extract_payload(result.stdout)


def payload_md(title: str, frontmatter: dict[str, Any], payload: dict[str, Any], body: str = "") -> str:
    head = yaml.safe_dump(frontmatter, sort_keys=False, allow_unicode=False).strip()
    structured = yaml.safe_dump(payload, sort_keys=False, allow_unicode=False, width=120).strip()
    body_text = f"\n\n{body.strip()}\n" if body.strip() else "\n"
    return f"---\n{head}\n---\n\n# {title}{body_text}\n{MARKER}\n\n## Structured Payload\n\n```yaml\n{structured}\n```\n"


def slug(value: str) -> str:
    value = value.lower()
    value = re.sub(r"[^a-z0-9]+", "-", value).strip("-")
    return value or "item"


def short_hash(data: Any) -> str:
    raw = json.dumps(data, ensure_ascii=True, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()[:12]


def ensure(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)


def write(path: Path, title: str, artifact_id: str, artifact_type: str, payload: dict[str, Any], body: str = "") -> None:
    ensure(path)
    frontmatter = {
        "artifact": {
            "id": artifact_id,
            "type": artifact_type,
            "status": "active",
            "optimization": "atomic_context",
        }
    }
    path.write_text(payload_md(title, frontmatter, payload, body), encoding="utf-8", newline="\n")


def find_module_item(modules: list[dict[str, Any]], item_id: str) -> dict[str, Any] | None:
    for module in modules:
        for item in module.get("backlog_items") or []:
            if isinstance(item, dict) and item.get("id") == item_id:
                enriched = dict(item)
                enriched["module_id"] = module.get("id")
                enriched["module_name"] = module.get("name")
                enriched["release"] = module.get("release")
                return enriched
    return None


def split_sources(sources: dict[str, str]) -> dict[str, dict[str, str]]:
    groups: dict[str, dict[str, str]] = defaultdict(dict)
    for key, value in sorted(sources.items()):
        path = str(value)
        if path.startswith("08-qa/"):
            group = "qa"
        elif path.startswith("07-implementation/"):
            group = "implementation"
        elif path.startswith("01-product-definition/"):
            group = "definition"
        elif path.startswith("06-delivery/"):
            group = "delivery"
        elif path.startswith("../../nexora-framework/"):
            group = "framework"
        else:
            group = "core"
        groups[group][key] = path
    return dict(groups)


def compact_project_state(root: Path, state: dict[str, Any]) -> None:
    completed = state.get("completed_deliverables") or []
    ledger_path = PROJECT_PATH / "08-qa/project-tracking/progress-ledger/completed-deliverables-ledger.md"
    if not completed and (root / ledger_path).exists():
        completed = (read_payload(root / ledger_path).get("completed_deliverables") or [])
    ledger_payload = {
        "project": state.get("project"),
        "total_completed_deliverables": len(completed),
        "payload_hash": short_hash(completed),
        "completed_deliverables": completed,
    }
    write(root / ledger_path, "Completed Deliverables Ledger", "HOP-TRACK-COMPLETED-DELIVERABLES", "project-progress-ledger", ledger_payload)

    recent = []
    if isinstance(completed, list):
        for entry in completed[:12]:
            text = str(entry)
            recent.append({"id": text.split(" ", 1)[0], "ledger_ref": ledger_path.relative_to(PROJECT_PATH).as_posix()})
    completed_backlog_items = (state.get("implementation_progress") or {}).get("completed_backlog_items") or []
    progress = state.get("commercial_product_progress") or {}
    progress_detail_path = PROJECT_PATH / "08-qa/project-tracking/progress-ledger/commercial-product-progress-detail.md"
    write(
        root / progress_detail_path,
        "Commercial Product Progress Detail",
        "HOP-TRACK-COMMERCIAL-PROGRESS-DETAIL",
        "project-progress-detail",
        {"commercial_product_progress": progress},
    )
    module_closeout = state.get("module_closeout") or {}
    module_closeout_path = PROJECT_PATH / "08-qa/project-tracking/progress-ledger/module-closeout-detail.md"
    write(
        root / module_closeout_path,
        "Module Closeout Detail",
        "HOP-TRACK-MODULE-CLOSEOUT-DETAIL",
        "module-closeout-detail",
        {"module_closeout": module_closeout},
    )
    compact = {
        "project": state.get("project"),
        "project_slug": state.get("project_slug"),
        "company": state.get("company"),
        "repository_version": state.get("repository_version"),
        "current_phase": state.get("current_phase"),
        "commercial_product_progress": {
            "status": progress.get("status"),
            "current_iteration": progress.get("current_iteration"),
            "active_module": progress.get("active_module"),
            "active_backlog_item": progress.get("active_backlog_item"),
            "execution_prompts": progress.get("execution_prompts"),
            "backlog": progress.get("backlog"),
            "capability_package_index": progress.get("capability_package_index"),
            "detail_ref": progress_detail_path.relative_to(PROJECT_PATH).as_posix(),
        },
        "architecture_status": state.get("architecture_status"),
        "implementation_progress": {
            "current_backlog_item": (state.get("implementation_progress") or {}).get("current_backlog_item"),
            "active_backlog_item": (state.get("implementation_progress") or {}).get("active_backlog_item"),
            "next_backlog_item": (state.get("implementation_progress") or {}).get("next_backlog_item"),
            "current_module": (state.get("implementation_progress") or {}).get("current_module"),
            "progress_percent": (state.get("implementation_progress") or {}).get("progress_percent"),
            "last_commit": (state.get("implementation_progress") or {}).get("last_commit"),
            "completed_backlog_items_count": len(completed_backlog_items) if isinstance(completed_backlog_items, list) else 0,
            "recent_completed_backlog_items": completed_backlog_items[-20:] if isinstance(completed_backlog_items, list) else [],
        },
        "module_closeout": {
            "active_ref": module_closeout_path.relative_to(PROJECT_PATH).as_posix(),
            "load_on_demand": True,
        },
        "recent_completed_deliverables_index": recent,
        "atomic_references": {
            "completed_deliverables_ledger": ledger_path.relative_to(PROJECT_PATH).as_posix(),
            "commercial_product_progress_detail": progress_detail_path.relative_to(PROJECT_PATH).as_posix(),
            "module_closeout_detail": module_closeout_path.relative_to(PROJECT_PATH).as_posix(),
            "commercial_backlog_master_plan": "06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md",
            "source_registry_index": "08-qa/project-tracking/source-registry/SOURCE_REGISTRY_INDEX.md",
        },
        "context_policy": {
            "load_this_file_first": True,
            "load_atomic_references_only_on_demand": True,
            "do_not_expand_completed_deliverables_by_default": True,
        },
    }
    write(root / PROJECT_PATH / "PROJECT_STATE.md", "Project State", "PROJECT_STATE", "project-state-index", compact)


def compact_source_of_truth(root: Path, sot: dict[str, Any], active_task: str) -> None:
    sources = sot.get("sources") or {}
    grouped = split_sources(sources if isinstance(sources, dict) else {})
    index_entries = {}
    for group, entries in grouped.items():
        rel = PROJECT_PATH / f"08-qa/project-tracking/source-registry/source-registry-{group}.md"
        index_entries[group] = {
            "path": rel.relative_to(PROJECT_PATH).as_posix(),
            "entries": len(entries),
            "payload_hash": short_hash(entries),
        }
        write(
            root / rel,
            f"Source Registry {group.title()}",
            f"HOP-SOT-{group.upper()}",
            "source-registry-shard",
            {"group": group, "entry_count": len(entries), "sources": entries},
        )

    active_sources = {
        key: value
        for key, value in (sources if isinstance(sources, dict) else {}).items()
        if active_task.lower().replace("-", "_") in key.lower() or active_task in str(value)
    }
    compact_sources = {
        key: value
        for key, value in (sources if isinstance(sources, dict) else {}).items()
        if key
        in {
            "business_requirement",
            "project_brief",
            "project_state",
            "ordered_development_guide",
            "capability_package_index",
            "context_orchestrator_python",
            "agent_runtime_router_python",
            "agent_cli_preflight_python",
            "backlog_closure_validator_python",
            "framework_managed_artifact_optimizer_python",
            "context_orchestrator_tool_registry",
            "hop_agent_orchestration_runtime_runbook",
            "context_atomic_artifact_standard",
        }
    }
    compact_sources.setdefault(
        "agent_cli_preflight_python",
        "../../nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py",
    )
    compact_sources.setdefault(
        "framework_managed_artifact_optimizer_python",
        "../../nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py",
    )
    compact_sources.setdefault(
        "context_atomic_artifact_standard",
        "../../nexora-framework/02-standards/standards/context-atomic-artifact-standard.md",
    )
    compact_sources.update(active_sources)
    compact_sources["source_registry_index"] = "08-qa/project-tracking/source-registry/SOURCE_REGISTRY_INDEX.md"

    write(
        root / PROJECT_PATH / "08-qa/project-tracking/source-registry/SOURCE_REGISTRY_INDEX.md",
        "Source Registry Index",
        "HOP-SOT-INDEX",
        "source-registry-index",
        {"artifact": sot.get("artifact") or {}, "groups": index_entries},
        "Load a source shard only when a task explicitly touches that domain.",
    )
    compact = {
        "artifact": sot.get("artifact") or {},
        "sources": compact_sources,
        "source_registry": index_entries,
        "rules_summary": {
            "full_rules_count": len(sot.get("rules") or []),
            "rules_are_loaded_from_framework_standards": True,
            "load_shards_on_demand": True,
        },
    }
    write(root / PROJECT_PATH / "SOURCE_OF_TRUTH.md", "HOP Source Of Truth", "HOP-SOT-001", "source-of-truth-index", compact)


def compact_product_backlog(root: Path, backlog: dict[str, Any]) -> None:
    modules = backlog.get("modules") or []
    if modules and not any((module.get("backlog_items") for module in modules if isinstance(module, dict))):
        existing_items = (read_payload(root / PROJECT_PATH / "06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md").get("items") or [])
        if existing_items:
            by_module: dict[str, list[dict[str, Any]]] = defaultdict(list)
            for item in existing_items:
                if isinstance(item, dict):
                    by_module[str(item.get("module_id"))].append(item)
            modules = [{**module, "backlog_items": by_module.get(str(module.get("id")), [])} for module in modules if isinstance(module, dict)]
    module_refs = []
    item_refs = []
    item_index = []
    for module in modules:
        module_id = str(module.get("id"))
        module_path = PROJECT_PATH / f"06-delivery/commercial-product/backlog-map/modules/{module_id}.md"
        backlog_items = module.get("backlog_items") or []
        module_refs.append(
            {
                "id": module_id,
                "name": module.get("name"),
                "release": module.get("release"),
                "status": module.get("status", "planned"),
                "path": module_path.relative_to(PROJECT_PATH).as_posix(),
                "items": len(backlog_items),
            }
        )
        write(
            root / module_path,
            f"{module_id} Module Backlog",
            f"HOP-BACKLOG-MODULE-{module_id}",
            "backlog-module-record",
            module,
        )
        for item in backlog_items:
            if not isinstance(item, dict) or not item.get("id"):
                continue
            item_id = str(item["id"])
            item_path = PROJECT_PATH / f"06-delivery/commercial-product/backlog-map/items/{item_id}.md"
            enriched = dict(item)
            enriched.update({"module_id": module_id, "module_name": module.get("name"), "release": module.get("release")})
            write(root / item_path, f"{item_id} Backlog Item", f"HOP-BACKLOG-ITEM-{item_id}", "backlog-item-record", enriched)
            item_refs.append(item_path.relative_to(PROJECT_PATH).as_posix())
            item_index.append(
                {
                    "id": item_id,
                    "name": item.get("name"),
                    "status": item.get("status", "planned"),
                    "module_id": module_id,
                    "path": item_path.relative_to(PROJECT_PATH).as_posix(),
                }
            )

    master = {
        "artifact": {"id": "HOP-COM-BACKLOG-MASTER", "type": "atomic-backlog-master-plan", "status": "active"},
        "current_baseline": (backlog.get("product") or {}).get("current_baseline") or {},
        "release_plan": backlog.get("release_plan") or [],
        "module_index": module_refs,
        "item_count": len(item_index),
        "item_index_path": "06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md",
        "load_policy": {
            "load_master_first": True,
            "load_module_record_when_selecting_dependency": True,
            "load_item_record_only_for_active_or_impacted_item": True,
        },
    }
    write(root / PROJECT_PATH / "06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md", "HOP Master Backlog Plan", "HOP-COM-BACKLOG-MASTER", "atomic-backlog-master-plan", master)
    write(root / PROJECT_PATH / "06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md", "HOP Backlog Item Index", "HOP-COM-BACKLOG-ITEM-INDEX", "backlog-item-index", {"items": item_index})
    definition_payload = {
        "global_definition_of_ready": backlog.get("global_definition_of_ready") or [],
        "global_definition_of_done": backlog.get("global_definition_of_done") or [],
        "capability_package_execution_contract": backlog.get("capability_package_execution_contract") or {},
        "commercial_ga_gates": backlog.get("commercial_ga_gates") or [],
    }
    write(
        root / PROJECT_PATH / "06-delivery/commercial-product/backlog-map/DEFINITION_OF_READY_DONE.md",
        "HOP Definition Of Ready And Done",
        "HOP-COM-BACKLOG-DOR-DOD",
        "backlog-definition-gates",
        definition_payload,
    )
    baseline = (backlog.get("product") or {}).get("current_baseline") or {}
    active_item = find_module_item(modules, str(baseline.get("active_backlog_item") or ""))

    compact = {
        "artifact": backlog.get("artifact") or {},
        "product": {
            **{k: v for k, v in (backlog.get("product") or {}).items() if k != "current_baseline"},
            "current_baseline": baseline,
        },
        "mdpe_policy": backlog.get("mdpe_policy") or {},
        "backlog_master_plan": "06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md",
        "backlog_item_index": "06-delivery/commercial-product/backlog-map/BACKLOG_ITEM_INDEX.md",
        "definition_of_ready_done": "06-delivery/commercial-product/backlog-map/DEFINITION_OF_READY_DONE.md",
        "active_item": active_item,
        "modules": module_refs,
        "context_policy": {
            "do_not_load_module_records_by_default": True,
            "load_active_item_record_only": True,
            "validator_loads_item_index_for_historical_status": True,
        },
    }
    write(root / PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md", "HOP Commercial Product Backlog", "HOP-COM-BACKLOG-001", "commercial-product-backlog-index", compact)


def compact_execution_prompts(root: Path, prompts: dict[str, Any]) -> None:
    base_dir = PROJECT_PATH / "06-delivery/commercial-product/prompt-library"
    sequence_refs = []
    for item in prompts.get("prompt_sequence") or []:
        if not isinstance(item, dict):
            continue
        prompt_id = str(item.get("id") or f"prompt-{len(sequence_refs)+1}")
        path = base_dir / f"{slug(prompt_id)}.md"
        write(root / path, f"{prompt_id} Auxiliary Prompt", f"HOP-PROMPT-{slug(prompt_id).upper()}", "auxiliary-prompt", item)
        sequence_refs.append({"id": prompt_id, "path": path.relative_to(PROJECT_PATH).as_posix(), "purpose": item.get("purpose") or item.get("name")})

    rules = prompts.get("rules") or []
    rules_path = base_dir / "execution-rules.md"
    write(root / rules_path, "Commercial Backlog Execution Rules", "HOP-PROMPT-RULES", "execution-rule-index", {"rules": rules})

    compact = {
        "artifact": prompts.get("artifact") or {},
        "rules_ref": rules_path.relative_to(PROJECT_PATH).as_posix(),
        "rules_summary": {
            "agent_agnostic": True,
            "ollama_prompt_optimization_required": True,
            "manual_execution_flow_default": True,
            "closure_validator_required_after_commit": True,
            "load_full_rules_only_on_demand": True,
        },
        "required_context": {
            "load_first": [
                "PROJECT_STATE.md",
                "SOURCE_OF_TRUTH.md",
                "06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md",
                "06-delivery/commercial-product/backlog-map/MASTER_BACKLOG_PLAN.md",
            ],
            "load_on_demand": [
                "06-delivery/commercial-product/backlog-map/items/<ACTIVE_ITEM>.md",
                "06-delivery/commercial-product/prompt-library/<AUX_PROMPT>.md",
            ],
        },
        "prompt_sequence": sequence_refs,
        "validation_commands": prompts.get("validation_commands") or {},
    }
    write(root / PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md", "HOP Commercial Backlog Execution Prompts", "HOP-COM-PROMPTS-001", "execution-prompt-index", compact)


def compact_repository_indexes(root: Path, seed_from_git_head: bool) -> None:
    root_state_path = Path("PROJECT_STATE.md")
    root_sot_path = Path("SOURCE_OF_TRUTH.md")
    root_state = (
        read_payload_from_git_head(root, root_state_path) if seed_from_git_head else read_payload(root / root_state_path)
    ) or read_payload(root / root_state_path)
    root_sot = (
        read_payload_from_git_head(root, root_sot_path) if seed_from_git_head else read_payload(root / root_sot_path)
    ) or read_payload(root / root_sot_path)

    projects = root_state.get("projects") or []
    compact_projects = []
    for project in projects if isinstance(projects, list) else []:
        if not isinstance(project, dict):
            continue
        completed = project.get("completed_backlog_items") or []
        compact_projects.append(
            {
                "slug": project.get("slug"),
                "name": project.get("name"),
                "path": project.get("path"),
                "status": project.get("status"),
                "active_module": project.get("active_module"),
                "active_backlog_item": project.get("active_backlog_item"),
                "completed_backlog_items_count": len(completed) if isinstance(completed, list) else 0,
                "project_state_ref": f"{str(project.get('path') or '').rstrip('/')}/PROJECT_STATE.md",
            }
        )
    root_progress_path = Path("nexora-framework/08-engineering/repository-tracking/repository-progress-ledger.md")
    write(
        root / root_progress_path,
        "Repository Progress Ledger",
        "NEXORA-REPO-PROGRESS-LEDGER",
        "repository-progress-ledger",
        {"projects": projects},
    )
    compact_state = {
        "repository": root_state.get("repository"),
        "repository_version": root_state.get("repository_version"),
        "current_phase": root_state.get("current_phase"),
        "company": root_state.get("company"),
        "framework": {
            "path": (root_state.get("framework") or {}).get("path"),
            "status": (root_state.get("framework") or {}).get("status"),
            "usage_guide": (root_state.get("framework") or {}).get("usage_guide"),
            "standards_index": "nexora-framework/02-standards/standards/",
            "context_atomic_artifact_standard": "nexora-framework/02-standards/standards/context-atomic-artifact-standard.md",
        },
        "projects": compact_projects,
        "atomic_references": {
            "repository_progress_ledger": root_progress_path.as_posix(),
            "repository_source_registry_index": "nexora-framework/08-engineering/repository-tracking/source-registry/REPOSITORY_SOURCE_REGISTRY_INDEX.md",
        },
    }
    write(root / root_state_path, "Repository Project State", "PROJECT_STATE", "repository-state-index", compact_state)

    sources = root_sot.get("sources") or {}
    grouped = split_sources({k: str(v) for k, v in sources.items()}) if isinstance(sources, dict) else {}
    root_registry_dir = Path("nexora-framework/08-engineering/repository-tracking/source-registry")
    registry_index = {}
    for group, entries in grouped.items():
        path = root_registry_dir / f"repository-source-registry-{group}.md"
        registry_index[group] = {"path": path.as_posix(), "entries": len(entries), "payload_hash": short_hash(entries)}
        write(root / path, f"Repository Source Registry {group.title()}", f"NEXORA-SOT-{group.upper()}", "repository-source-registry-shard", {"sources": entries})
    compact_sources = {
        key: value
        for key, value in (sources if isinstance(sources, dict) else {}).items()
        if key
        in {
            "repository_readme",
            "framework_usage_guide",
            "repository_state",
            "agent_bootstrap",
            "framework_readme",
            "framework_execution_sequence",
            "context_efficient_execution_standard",
            "context_orchestrator_python",
            "agent_cli_preflight_python",
            "backlog_closure_validator_python",
        }
    }
    compact_sources["context_atomic_artifact_standard"] = "nexora-framework/02-standards/standards/context-atomic-artifact-standard.md"
    compact_sources["agent_cli_preflight_python"] = "nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py"
    compact_sources["framework_managed_artifact_optimizer_python"] = "nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py"
    compact_sources["repository_source_registry_index"] = (root_registry_dir / "REPOSITORY_SOURCE_REGISTRY_INDEX.md").as_posix()
    write(
        root / root_registry_dir / "REPOSITORY_SOURCE_REGISTRY_INDEX.md",
        "Repository Source Registry Index",
        "NEXORA-SOT-INDEX",
        "repository-source-registry-index",
        {"groups": registry_index},
    )
    compact_sot = {
        "artifact": root_sot.get("artifact") or {},
        "sources": compact_sources,
        "source_registry": registry_index,
        "context_policy": {"load_shards_on_demand": True, "do_not_preload_full_repository_registry": True},
    }
    write(root / root_sot_path, "Repository Source Of Truth", "NEXORA-SOT-001", "repository-source-of-truth-index", compact_sot)


def optimize(root: Path, seed_from_git_head: bool = False) -> None:
    project = root / PROJECT_PATH
    files = {
        "state": PROJECT_PATH / "PROJECT_STATE.md",
        "sot": PROJECT_PATH / "SOURCE_OF_TRUTH.md",
        "backlog": PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md",
        "prompts": PROJECT_PATH / "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md",
    }
    if seed_from_git_head:
        state = read_payload_from_git_head(root, files["state"]) or read_payload(root / files["state"])
        sot = read_payload_from_git_head(root, files["sot"]) or read_payload(root / files["sot"])
        backlog = read_payload_from_git_head(root, files["backlog"]) or read_payload(root / files["backlog"])
        prompts = read_payload_from_git_head(root, files["prompts"]) or read_payload(root / files["prompts"])
    else:
        state = read_payload(project / "PROJECT_STATE.md")
        sot = read_payload(project / "SOURCE_OF_TRUTH.md")
        backlog = read_payload(project / "06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md")
        prompts = read_payload(project / "06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md")
    active_task = (
        ((backlog.get("product") or {}).get("current_baseline") or {}).get("active_backlog_item")
        or ((state.get("commercial_product_progress") or {}).get("active_backlog_item"))
        or ((prompts.get("validation_commands") or {}).get("backlog_item_id"))
        or "UNKNOWN"
    )
    compact_project_state(root, state)
    compact_source_of_truth(root, sot, str(active_task))
    compact_product_backlog(root, backlog)
    compact_execution_prompts(root, prompts)
    compact_repository_indexes(root, seed_from_git_head)


def main() -> int:
    parser = argparse.ArgumentParser(description="Optimize framework-managed HOP context artifacts into atomic indexes.")
    parser.add_argument("--root", default=".", help="Repository root.")
    parser.add_argument(
        "--seed-from-git-head",
        action="store_true",
        help="Use HEAD versions of large framework-managed files as the migration seed. Intended for the first optimization pass.",
    )
    args = parser.parse_args()
    optimize(Path(args.root).resolve(), args.seed_from_git_head)
    print("framework_managed_artifacts_optimized")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
