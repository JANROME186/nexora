---
id: HOP-UI-BCM-AI-006
format: markdown_structured_payload
type: ui-model
name: Safety Policy and Human Review UI Model
version: 1.0.0
status: modeled
---

# Safety Policy and Human Review UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-006
  type: ui-model
  status: modeled
capability_id: BCM-AI-006
screens:
  - id: AI_SAFETY_POLICY_ADMIN
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: AI_HUMAN_REVIEW_QUEUE
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
