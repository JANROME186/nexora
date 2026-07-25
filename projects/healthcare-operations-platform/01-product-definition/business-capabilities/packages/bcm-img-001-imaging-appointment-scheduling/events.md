---
id: HOP-EVENTS-BCM-IMG-001
format: markdown_structured_payload
type: events
name: Imaging Appointment Scheduling Domain Events
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-001
  type: events
  name: Imaging Appointment Scheduling Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
events:
  - id: EVT-BCM-IMG-001-001
    name: BCMIMG001StateChanged
    description: Triggered when Imaging Appointment Scheduling state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
