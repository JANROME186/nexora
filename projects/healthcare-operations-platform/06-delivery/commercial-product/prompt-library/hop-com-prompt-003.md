---
artifact:
  id: HOP-PROMPT-HOP-COM-PROMPT-003
  type: auxiliary-prompt
  status: active
  optimization: atomic_context
---

# HOP-COM-PROMPT-003 Auxiliary Prompt

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-COM-PROMPT-003
name: Validate capability package models
intent: Confirm that generated capability packages satisfy MDPE, traceability and agent-agnostic requirements.
input:
- selected_capability_package_folders
- ../../SOURCE_OF_TRUTH.md
- HOP_COMMERCIAL_PRODUCT_BACKLOG.md
expected_output:
- validation_report
- missing_artifacts
- traceability_gaps
- agent_agnostic_findings
- ready_for_compilation_decision
prompt: 'Validate the selected Capability Package model set.

  Confirm that all required artifacts exist, YAML is parseable, Markdown companion files exist where required, capabilities
  trace to BCM-001, dependencies trace to BCM-002, API surfaces are classified, and security/audit/test expectations are present.

  Confirm that generation-plan.md separates generated outputs from custom implementation points.

  Scan for named-agent dependencies or vendor-specific execution requirements.

  If any blocking gap exists, update the package before allowing compilation or implementation.

  '
```
