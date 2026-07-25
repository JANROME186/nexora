---
id: HOP-TRACE-BCM-IMG-008
format: markdown_structured_payload
type: traceability
name: Imaging Study Delivery Traceability
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-008
  type: traceability
  name: Imaging Study Delivery Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
traces:
  capability_map:
    bcm_001: BCM-IMG-008
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-IMG-007
    - BCM-IMG-005
    - BCM-PLT-003
    - BCM-RES-004
    downstream_capabilities:
  domain_foundation:
    bounded_context: radiology-delivery
    primary_aggregate: ImagingDeliveryPackage (AGG-038)
```
