---
id: HOP-EVT-BCM-AI-003
format: markdown_structured_payload
type: event-model
name: Result and Case Summaries Events
version: 1.0.0
status: modeled
---

# Result and Case Summaries Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-003
  type: event-model
  status: modeled
capability_id: BCM-AI-003
events:
  - name: SummaryRequestedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: SummaryDraftGeneratedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: SummaryAcceptedWithEditsEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
