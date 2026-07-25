---
id: HOP-EVENTS-BCM-IMG-007
format: markdown_structured_payload
type: events
name: Radiology Signature Domain Events
version: 1.0.0
status: modeled
---

# Radiology Signature Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-007
  type: events
  name: Radiology Signature Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
events:
  - id: EVT-BCM-IMG-007-001
    name: BCMIMG007StateChanged
    description: Triggered when Radiology Signature state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
