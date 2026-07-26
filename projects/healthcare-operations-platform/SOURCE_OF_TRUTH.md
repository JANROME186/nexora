---
artifact:
  id: HOP-SOT-001
  type: source-of-truth-index
  status: active
  optimization: atomic_context
---

# HOP Source Of Truth

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SOT-001
  type: source-of-truth-registry
  version: 1.0.0
  status: approved
sources:
  business_requirement: BUSINESS_REQUIREMENT.md
  project_brief: PROJECT_BRIEF.md
  project_state: PROJECT_STATE.md
  ordered_development_guide: ORDERED_DEVELOPMENT_GUIDE.md
  context_orchestrator_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
  agent_runtime_router_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
  backlog_closure_validator_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  context_orchestrator_tool_registry: ../../nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md
  hop_agent_orchestration_runtime_runbook: 09-operations/runbooks/agent-orchestration-runtime-runbook.md
  capability_package_index: 01-product-definition/business-capabilities/packages/capability-package-index.md
  framework_managed_artifact_optimizer_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py
  context_atomic_artifact_standard: ../../nexora-framework/02-standards/standards/context-atomic-artifact-standard.md
  source_registry_index: 08-qa/project-tracking/source-registry/SOURCE_REGISTRY_INDEX.md
source_registry:
  core:
    path: 08-qa/project-tracking/source-registry/source-registry-core.md
    entries: 87
    payload_hash: fbe76b97d8ca
  framework:
    path: 08-qa/project-tracking/source-registry/source-registry-framework.md
    entries: 32
    payload_hash: 3a0201cf1027
  qa:
    path: 08-qa/project-tracking/source-registry/source-registry-qa.md
    entries: 426
    payload_hash: 92cd33ef1159
  definition:
    path: 08-qa/project-tracking/source-registry/source-registry-definition.md
    entries: 84
    payload_hash: 9c6c695c092c
  implementation:
    path: 08-qa/project-tracking/source-registry/source-registry-implementation.md
    entries: 54
    payload_hash: 437d8d8dcaca
  delivery:
    path: 08-qa/project-tracking/source-registry/source-registry-delivery.md
    entries: 33
    payload_hash: 4894d720d311
rules_summary:
  full_rules_count: 55
  rules_are_loaded_from_framework_standards: true
  load_shards_on_demand: true
```
