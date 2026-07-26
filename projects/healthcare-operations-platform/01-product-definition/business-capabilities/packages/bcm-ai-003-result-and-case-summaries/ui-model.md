---
id: HOP-UI-BCM-AI-003
format: markdown_structured_payload
type: ui-model
name: Result and Case Summaries UI Model
version: 1.0.0
status: modeled
---

# Result and Case Summaries UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-003
  type: ui-model
  status: modeled
capability_id: BCM-AI-003
screens:
  - id: SUMMARY_REQUEST_PANEL
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: SUMMARY_HUMAN_REVIEW
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
