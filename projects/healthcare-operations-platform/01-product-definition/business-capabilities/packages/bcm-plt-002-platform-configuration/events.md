---
id: HOP-EVT-BCM-PLT-002
format: markdown_structured_payload
type: events
name: Platform Configuration Domain Events
version: 1.0.0
---

# Platform Configuration Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-002
  type: events
  name: Platform Configuration Domain Events
  version: 1.0.0
events:
- name: ConfigUpdatedEvent
  type: domain_event
  publisher: platform-operations
  payload:
    key: String
    tenant_id: UUID
    timestamp: Instant
- name: FeatureFlagToggledEvent
  type: domain_event
  publisher: platform-operations
  payload:
    flag_key: String
    new_status: Boolean
    rollout_percentage: Integer
    timestamp: Instant
```
