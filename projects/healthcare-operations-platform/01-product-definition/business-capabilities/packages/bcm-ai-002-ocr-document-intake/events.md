---
id: HOP-EVT-BCM-AI-002
format: markdown_structured_payload
type: event-model
name: OCR Document Intake Events
version: 1.0.0
status: modeled
---

# OCR Document Intake Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-002
  type: event-model
  status: modeled
capability_id: BCM-AI-002
events:
  - name: OcrExtractionRequestedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: OcrExtractionCompletedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: OcrExtractionCorrectedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
