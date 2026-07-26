---
id: HOP-UI-BCM-AI-008
format: markdown_structured_payload
type: ui-model
name: AI Audit and Evaluation UI Model
version: 1.0.0
status: modeled
---

# AI Audit and Evaluation UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-008
  type: ui-model
  status: modeled
capability_id: BCM-AI-008
screens:
  - id: AI_EVALUATION_DASHBOARD
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: AI_AUDIT_EVIDENCE_EXPORT
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
