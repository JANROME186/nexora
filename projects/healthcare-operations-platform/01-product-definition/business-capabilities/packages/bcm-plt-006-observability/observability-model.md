---
id: HOP-OBS-BCM-PLT-006
format: markdown_structured_payload
type: observability-model
name: Observability Telemetry Self-Model
version: 1.0.0
---

# Observability Telemetry Self Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-PLT-006
  type: observability-model
  name: Observability Telemetry Self-Model
  version: 1.0.0
metrics:
- name: hop_telemetry_spans_emitted_total
  type: counter
  description: Count of OpenTelemetry spans exported.
- name: hop_health_check_status
  type: gauge
  labels:
  - probe_type
  description: Health probe status (1=UP, 0=DOWN).
```
