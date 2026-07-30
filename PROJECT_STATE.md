---
artifact:
  id: PROJECT_STATE
  type: repository-state-index
  status: active
  optimization: atomic_context
---

# Repository Project State

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
repository: Nexora
repository_version: 1.0.0
current_phase: HOP-HARD-INT-001 closed. Final hardening continues through HOP-HARD-QA-001.
company:
  name: Nexora
  type: Software Development and Artificial Intelligence Company
framework:
  path: nexora-framework/
  status: ready
  usage_guide: NEXORA_FRAMEWORK_USAGE_GUIDE.md
  standards_index: nexora-framework/02-standards/standards/
  context_atomic_artifact_standard: nexora-framework/02-standards/standards/context-atomic-artifact-standard.md
projects:
- slug: healthcare-operations-platform
  name: Healthcare Operations Platform
  path: projects/healthcare-operations-platform/
  status: final_hardening_active
  active_module: HOP-FINAL-HARDENING
  active_backlog_item: HOP-HARD-QA-001
  completed_backlog_items_count: 115
  project_state_ref: projects/healthcare-operations-platform/PROJECT_STATE.md
atomic_references:
  repository_progress_ledger: nexora-framework/08-engineering/repository-tracking/repository-progress-ledger.md
  repository_source_registry_index: nexora-framework/08-engineering/repository-tracking/source-registry/REPOSITORY_SOURCE_REGISTRY_INDEX.md
```
