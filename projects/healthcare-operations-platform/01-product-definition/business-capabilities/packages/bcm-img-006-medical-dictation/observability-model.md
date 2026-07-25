---
id: HOP-OBS-BCM-IMG-006
format: markdown_structured_payload
type: observability-model
name: Medical Dictation Observability Model
version: 1.0.0
status: modeled
---

# Medical Dictation Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-006
  type: observability-model
  name: Medical Dictation Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
observability:
  metrics:
    - name: hop_imaging_bcm_img_006_operations_total
      type: counter
  traces:
    - span: bcm-img-006_execution
```
