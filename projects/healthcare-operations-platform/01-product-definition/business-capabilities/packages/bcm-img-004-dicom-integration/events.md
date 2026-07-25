---
id: HOP-EVENTS-BCM-IMG-004
format: markdown_structured_payload
type: events
name: DICOM Integration Domain Events
version: 1.0.0
status: modeled
---

# DICOM Integration Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-004
  type: events
  name: DICOM Integration Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
events:
  - id: EVT-BCM-IMG-004-001
    name: BCMIMG004StateChanged
    description: Triggered when DICOM Integration state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
