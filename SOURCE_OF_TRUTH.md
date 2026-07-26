---
artifact:
  id: NEXORA-SOT-001
  type: repository-source-of-truth-index
  status: active
  optimization: atomic_context
---

# Repository Source Of Truth

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NEXORA-SOT-001
  type: repository-source-of-truth-registry
  version: 1.0.0
  status: approved
sources:
  repository_readme: README.md
  framework_usage_guide: NEXORA_FRAMEWORK_USAGE_GUIDE.md
  repository_state: PROJECT_STATE.md
  agent_bootstrap: AGENT_BOOTSTRAP.md
  framework_readme: nexora-framework/README.md
  framework_execution_sequence: nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
  context_efficient_execution_standard: nexora-framework/02-standards/standards/context-efficient-execution-standard.md
  context_orchestrator_python: nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
  backlog_closure_validator_python: nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  context_atomic_artifact_standard: nexora-framework/02-standards/standards/context-atomic-artifact-standard.md
  framework_managed_artifact_optimizer_python: nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py
  repository_source_registry_index: nexora-framework/08-engineering/repository-tracking/source-registry/REPOSITORY_SOURCE_REGISTRY_INDEX.md
source_registry:
  core:
    path: nexora-framework/08-engineering/repository-tracking/source-registry/repository-source-registry-core.md
    entries: 197
    payload_hash: c4088f93b1ed
context_policy:
  load_shards_on_demand: true
  do_not_preload_full_repository_registry: true
```
