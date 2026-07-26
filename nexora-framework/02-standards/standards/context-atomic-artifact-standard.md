---
artifact:
  id: NXF-STD-015
  type: execution-standard
  name: Context Atomic Artifact Standard
  version: 1.0.0
  status: approved
  owner: Nexora Engineering
---

# Context Atomic Artifact Standard

Framework-managed project tracking files must stay compact. Agents must not append long histories,
full evidence copies, repeated prompt bodies or complete backlog trees into root state files.

The required pattern is:

```text
Compact root index -> master plan -> atomic record loaded on demand
```

Root files such as `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`,
`HOP_COMMERCIAL_PRODUCT_BACKLOG.md` and `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md` must contain
only current pointers, short summaries, counters and paths to atomic records.

Detailed content must live in small domain-specific files:

- backlog master plan;
- backlog item records;
- module records;
- source registry shards;
- progress ledgers;
- prompt-library records;
- QA/security evidence and handoffs.

## Loading Rule

An agent must load only:

- root project state;
- source-of-truth index;
- backlog master plan;
- active backlog item record;
- previous handoff;
- directly impacted capability/package/code/evidence files.

It must not preload full source registries, complete historical deliverable lists, all backlog item
records, all auxiliary prompts or all evidence files.

## Maintenance Rule

When closing a backlog item, update compact indexes and only the atomic records that changed. If a
root file grows because history or repeated content was appended, run:

```text
tool: framework_managed_artifact_optimizer
```

The optimizer is a framework control, not product feature code. Product backlog agents may run it
when artifacts grow, but they must not weaken its compaction policy.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-STD-015
  type: execution-standard
  name: Context Atomic Artifact Standard
  version: 1.0.0
  status: approved
  owner: Nexora Engineering
policy:
  compact_root_indexes_required: true
  atomic_records_required: true
  lazy_loading_required: true
  append_long_history_to_root_files: prohibited
  preload_all_backlog_items: prohibited
  preload_all_source_registry_entries: prohibited
  preload_all_auxiliary_prompts: prohibited
managed_root_files:
- PROJECT_STATE.md
- SOURCE_OF_TRUTH.md
- 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
- 06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
atomic_record_types:
- backlog_master_plan
- backlog_item_record
- backlog_module_record
- source_registry_shard
- progress_ledger
- prompt_library_record
- qa_security_evidence
- handoff_summary
tool_reference: framework_managed_artifact_optimizer
```
