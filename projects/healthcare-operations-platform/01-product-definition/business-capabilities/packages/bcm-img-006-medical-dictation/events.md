---
id: HOP-EVENTS-BCM-IMG-006
format: markdown_structured_payload
type: events
name: Medical Dictation Domain Events
version: 1.0.0
status: modeled
---

# Medical Dictation Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-006
  type: events
  name: Medical Dictation Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
events:
  - id: EVT-BCM-IMG-006-001
    name: BCMIMG006StateChanged
    description: Triggered when Medical Dictation state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
