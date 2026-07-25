---
id: HOP-OBS-BCM-IMG-002
format: markdown_structured_payload
type: observability-model
name: Imaging Reception Observability Model
version: 1.0.0
status: modeled
---

# Imaging Reception Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-002
  type: observability-model
  name: Imaging Reception Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
observability:
  metrics:
    - name: hop_imaging_bcm_img_002_operations_total
      type: counter
  traces:
    - span: bcm-img-002_execution
```
