---
id: HOP-EVT-BCM-AI-004
format: markdown_structured_payload
type: event-model
name: Semantic Search Events
version: 1.0.0
status: modeled
---

# Semantic Search Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-004
  type: event-model
  status: modeled
capability_id: BCM-AI-004
events:
  - name: SemanticQuerySubmittedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: SemanticResultReturnedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: SemanticIndexRefreshedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
