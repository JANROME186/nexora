---
id: HOP-UI-BCM-AI-001
format: markdown_structured_payload
type: ui-model
name: Assistant Orchestration UI Model
version: 1.0.0
status: modeled
---

# Assistant Orchestration UI Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-AI-001
  type: ui-model
  status: modeled
capability_id: BCM-AI-001
screens:
  - id: AI_ASSISTANT_WORKBENCH
    surface: employee_portal
    generated: true
    human_review_visible: true
  - id: AI_SESSION_REVIEW_QUEUE
    surface: employee_portal
    generated: true
    human_review_visible: true
interaction_rules:
  ai_outputs_are_visually_labeled: true
  reviewer_can_accept_edit_reject: true
  source_citations_visible: true
```
