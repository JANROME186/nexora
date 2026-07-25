---
id: HOP-TRACE-BCM-IMG-003
format: markdown_structured_payload
type: traceability
name: Imaging Study Management Traceability
version: 1.0.0
status: modeled
---

# Imaging Study Management Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-003
  type: traceability
  name: Imaging Study Management Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-003
traces:
  capability_map:
    bcm_001: BCM-IMG-003
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-IMG-002
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-IMG-004
    - BCM-IMG-006
  domain_foundation:
    bounded_context: imaging-operations
    primary_aggregate: ImagingStudy (AGG-033)
```
