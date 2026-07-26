---
id: HOP-UI-BCM-AI-002
format: markdown_structured_payload
type: ui-model
name: OCR Document Intake UI Model
version: 1.0.0
status: modeled
---

# OCR Document Intake UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-002
  type: ui-model
  status: modeled
capability_id: BCM-AI-002
screens:
  - id: OCR_INTAKE_QUEUE
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: OCR_CORRECTION_REVIEW
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
