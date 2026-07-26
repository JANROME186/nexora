---
id: HOP-UI-BCM-AI-005
format: markdown_structured_payload
type: ui-model
name: Retrieval Knowledge Grounding UI Model
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-005
  type: ui-model
  status: modeled
capability_id: BCM-AI-005
screens:
  - id: GROUNDING_PACKAGE_ADMIN
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: GROUNDING_SOURCE_REVIEW
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
