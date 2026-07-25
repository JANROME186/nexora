---
id: HOP-OBS-BCM-IMG-007
format: markdown_structured_payload
type: observability-model
name: Radiology Signature Observability Model
version: 1.0.0
status: modeled
---

# Radiology Signature Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-007
  type: observability-model
  name: Radiology Signature Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
observability:
  metrics:
    - name: hop_imaging_bcm_img_007_operations_total
      type: counter
  traces:
    - span: bcm-img-007_execution
```
