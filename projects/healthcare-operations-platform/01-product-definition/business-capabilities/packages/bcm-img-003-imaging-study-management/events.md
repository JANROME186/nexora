---
id: HOP-EVENTS-BCM-IMG-003
format: markdown_structured_payload
type: events
name: Imaging Study Management Domain Events
version: 1.0.0
status: modeled
---

# Imaging Study Management Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-003
  type: events
  name: Imaging Study Management Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-003
events:
  - id: EVT-BCM-IMG-003-001
    name: BCMIMG003StateChanged
    description: Triggered when Imaging Study Management state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
