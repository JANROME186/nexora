---
id: HOP-TRACE-BCM-IMG-002
format: markdown_structured_payload
type: traceability
name: Imaging Reception Traceability
version: 1.0.0
status: modeled
---

# Imaging Reception Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-002
  type: traceability
  name: Imaging Reception Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
traces:
  capability_map:
    bcm_001: BCM-IMG-002
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-IMG-001
    - BCM-PER-002
    - BCM-ORG-003
    - BCM-PLT-001
    downstream_capabilities:
    - BCM-IMG-003
  domain_foundation:
    bounded_context: imaging-operations
    primary_aggregate: ImagingReceptionIntake (AGG-032)
```
