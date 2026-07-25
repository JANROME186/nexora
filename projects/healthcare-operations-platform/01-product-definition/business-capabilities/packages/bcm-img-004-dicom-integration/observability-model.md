---
id: HOP-OBS-BCM-IMG-004
format: markdown_structured_payload
type: observability-model
name: DICOM Integration Observability Model
version: 1.0.0
status: modeled
---

# DICOM Integration Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-004
  type: observability-model
  name: DICOM Integration Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
observability:
  metrics:
    - name: hop_imaging_bcm_img_004_operations_total
      type: counter
  traces:
    - span: bcm-img-004_execution
```
