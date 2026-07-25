---
id: HOP-EVT-BCM-PLT-006
format: markdown_structured_payload
type: events
name: Observability Domain Events
version: 1.0.0
---

# Observability Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-006
  type: events
  name: Observability Domain Events
  version: 1.0.0
events:
- name: HealthProbeFailedEvent
  type: domain_event
  publisher: platform-operations
  payload:
    service_id: String
    probe_type: String
    reason: String
    timestamp: Instant
- name: SloBreachedEvent
  type: domain_event
  publisher: platform-operations
  payload:
    slo_name: String
    metric_value: Double
    threshold: Double
    timestamp: Instant
```
