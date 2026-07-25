---
id: HOP-EVENTS-BCM-IMG-002
format: markdown_structured_payload
type: events
name: Imaging Reception Domain Events
version: 1.0.0
status: modeled
---

# Imaging Reception Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-002
  type: events
  name: Imaging Reception Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
events:
  - id: EVT-BCM-IMG-002-001
    name: BCMIMG002StateChanged
    description: Triggered when Imaging Reception state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
