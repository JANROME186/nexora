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
current_phase: HOP-HARD-QA-001 closed. Commercial hardening complete. All 116 backlog items closed.
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
  status: closed
  active_module: null
  active_backlog_item: null
  completed_backlog_items_count: 116
  project_state_ref: projects/healthcare-operations-platform/PROJECT_STATE.md
atomic_references:
  repository_progress_ledger: nexora-framework/08-engineering/repository-tracking/repository-progress-ledger.md
  repository_source_registry_index: nexora-framework/08-engineering/repository-tracking/source-registry/REPOSITORY_SOURCE_REGISTRY_INDEX.md
```
