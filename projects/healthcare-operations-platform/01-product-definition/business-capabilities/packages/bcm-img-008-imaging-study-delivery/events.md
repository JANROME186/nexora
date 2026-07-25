---
id: HOP-EVENTS-BCM-IMG-008
format: markdown_structured_payload
type: events
name: Imaging Study Delivery Domain Events
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-008
  type: events
  name: Imaging Study Delivery Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
events:
  - id: EVT-BCM-IMG-008-001
    name: BCMIMG008StateChanged
    description: Triggered when Imaging Study Delivery state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
