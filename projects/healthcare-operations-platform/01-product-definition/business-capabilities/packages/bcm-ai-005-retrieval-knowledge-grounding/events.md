---
id: HOP-EVT-BCM-AI-005
format: markdown_structured_payload
type: event-model
name: Retrieval Knowledge Grounding Events
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-005
  type: event-model
  status: modeled
capability_id: BCM-AI-005
events:
  - name: GroundingPackageCreatedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: GroundingSourceLinkedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: GroundingPackageRetiredEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
