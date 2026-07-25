---
id: HOP-FWK-FEEDBACK-IDX-001
format: markdown_structured_payload
type: framework-feedback-index
name: HOP Framework Feedback Index
version: 1.0.0
status: active
---

# Hop Framework Feedback Index

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-FWK-FEEDBACK-IDX-001
  type: framework-feedback-index
  name: HOP Framework Feedback Index
  version: 1.0.0
  status: active
  human_readable: README.md
  machine_readable: framework-feedback-index.md
standard:
  framework_feedback_standard: ../../../../nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
  prompt_playbook: ../../../../nexora-framework/05-prompts/prompts/framework-feedback-prompts.md
purpose: Track HOP execution feedback that may become Nexora framework improvement
  proposals.
policy:
  agents_may_create_feedback: true
  agents_may_implement_framework_changes: false_unless_explicitly_assigned_by_nexora
  product_backlog_must_not_be_blocked_by_noncritical_framework_feedback: true
feedback_item_path_pattern: 08-qa/framework-feedback/<feedback-id>.yaml
entries:
- feedback_id: FWF-HOP-001
  source_backlog_item_or_phase: framework_continuous_improvement_request
  feedback_type: governance_gap
  affected_framework_area: framework_feedback_and_governance
  priority_recommendation: P1_high
  central_backlog_item: FWI-GOV-001
  status: implemented_in_framework
- feedback_id: FWF-HOP-002
  source_backlog_item_or_phase: MVP-MOD-003-BE-002
  feedback_type: modeling_gap
  affected_framework_area: capability_package_standard
  priority_recommendation: P2_medium
  central_backlog_item: null
  status: proposed
- feedback_id: FWF-HOP-003
  source_backlog_item_or_phase: MVP-MOD-004-BE-001
  feedback_type: implementation_guidance_gap
  affected_framework_area: agent_to_mvp_recipe_and_backend_scaffolding_conventions
  priority_recommendation: P2_medium
  central_backlog_item: null
  status: proposed
```
