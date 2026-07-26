---
id: HOP-EVT-BCM-AI-007
format: markdown_structured_payload
type: event-model
name: Model Provider Integration Events
version: 1.0.0
status: modeled
---

# Model Provider Integration Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-007
  type: event-model
  status: modeled
capability_id: BCM-AI-007
events:
  - name: AiProviderProfileRegisteredEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: AiProviderRoutedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: AiProviderHealthChangedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
