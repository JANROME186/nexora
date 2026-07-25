---
id: HOP-TRACE-BCM-IMG-007
format: markdown_structured_payload
type: traceability
name: Radiology Signature Traceability
version: 1.0.0
status: modeled
---

# Radiology Signature Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-007
  type: traceability
  name: Radiology Signature Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
traces:
  capability_map:
    bcm_001: BCM-IMG-007
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-IMG-006
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-IMG-008
  domain_foundation:
    bounded_context: radiology-reporting
    primary_aggregate: RadiologyReport (AGG-037)
```
