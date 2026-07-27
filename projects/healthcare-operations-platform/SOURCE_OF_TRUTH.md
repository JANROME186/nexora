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
  agent_cli_preflight_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py
  source_registry_index: 08-qa/project-tracking/source-registry/SOURCE_REGISTRY_INDEX.md
  com_mod_014_closeout_qa_evidence: 08-qa/qa/imaging-operations/COM-MOD-014-CLOSEOUT-validation.md
  com_mod_014_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-014-CLOSEOUT/security-quality-evidence.md
  com_mod_014_closeout_handoff: 08-qa/handoffs/COM-MOD-014-CLOSEOUT-summary.md
  com_mod_015_def_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md
  com_mod_015_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md
  com_mod_015_def_handoff: 08-qa/handoffs/COM-MOD-015-DEF-summary.md
  com_mod_015_be_001_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-BE-001-validation.md
  com_mod_015_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-BE-001/security-quality-evidence.md
  com_mod_015_be_001_handoff: 08-qa/handoffs/COM-MOD-015-BE-001-summary.md
  com_mod_015_be_002_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-BE-002-validation.md
  com_mod_015_be_002_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-BE-002/security-quality-evidence.md
  com_mod_015_be_002_handoff: 08-qa/handoffs/COM-MOD-015-BE-002-summary.md
  com_mod_015_fe_001_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-FE-001-validation.md
  com_mod_015_fe_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-FE-001/security-quality-evidence.md
  com_mod_015_fe_001_handoff: 08-qa/handoffs/COM-MOD-015-FE-001-summary.md
  com_mod_015_qa_001_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-QA-001-validation.md
  com_mod_015_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-QA-001/security-quality-evidence.md
  com_mod_015_qa_001_handoff: 08-qa/handoffs/COM-MOD-015-QA-001-summary.md
  com_mod_015_closeout_qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-CLOSEOUT-validation.md
  com_mod_015_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-015-CLOSEOUT/security-quality-evidence.md
  com_mod_015_closeout_handoff: 08-qa/handoffs/COM-MOD-015-CLOSEOUT-summary.md
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
    entries: 434
    payload_hash: d98f15eb46b7
  definition:
    path: 08-qa/project-tracking/source-registry/source-registry-definition.md
    entries: 84
    payload_hash: 9c6c695c092c
  implementation:
    path: 08-qa/project-tracking/source-registry/source-registry-implementation.md
    entries: 56
    payload_hash: 89629ee0980f
  delivery:
    path: 08-qa/project-tracking/source-registry/source-registry-delivery.md
    entries: 33
    payload_hash: 4894d720d311
rules_summary:
  full_rules_count: 55
  rules_are_loaded_from_framework_standards: true
  load_shards_on_demand: true
```
