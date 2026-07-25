---
id: HOP-EVENTS-BCM-IMG-005
format: markdown_structured_payload
type: events
name: PACS Integration Domain Events
version: 1.0.0
status: modeled
---

# PACS Integration Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVENTS-BCM-IMG-005
  type: events
  name: PACS Integration Domain Events
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-005
events:
  - id: EVT-BCM-IMG-005-001
    name: BCMIMG005StateChanged
    description: Triggered when PACS Integration state undergoes a successful transition.
    payload_schema:
      tenant_id: UUID
      aggregate_id: UUID
      timestamp: Instant
```
