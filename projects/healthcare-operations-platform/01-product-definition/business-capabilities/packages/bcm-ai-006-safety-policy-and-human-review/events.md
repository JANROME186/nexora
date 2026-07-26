---
id: HOP-EVT-BCM-AI-006
format: markdown_structured_payload
type: event-model
name: Safety Policy and Human Review Events
version: 1.0.0
status: modeled
---

# Safety Policy and Human Review Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-006
  type: event-model
  status: modeled
capability_id: BCM-AI-006
events:
  - name: AiSafetyDecisionRecordedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: AiOutputBlockedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: HumanReviewEscalatedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
