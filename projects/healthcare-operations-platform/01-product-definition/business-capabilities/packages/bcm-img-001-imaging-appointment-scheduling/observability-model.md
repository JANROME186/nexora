---
id: HOP-OBS-BCM-IMG-001
format: markdown_structured_payload
type: observability-model
name: Imaging Appointment Scheduling Observability Model
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-001
  type: observability-model
  name: Imaging Appointment Scheduling Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
observability:
  metrics:
    - name: hop_imaging_bcm_img_001_operations_total
      type: counter
  traces:
    - span: bcm-img-001_execution
```
