---
id: HOP-OBS-BCM-IMG-005
format: markdown_structured_payload
type: observability-model
name: PACS Integration Observability Model
version: 1.0.0
status: modeled
---

# PACS Integration Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-IMG-005
  type: observability-model
  name: PACS Integration Observability Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-005
observability:
  metrics:
    - name: hop_imaging_bcm_img_005_operations_total
      type: counter
  traces:
    - span: bcm-img-005_execution
```
