---
id: HOP-TRACE-BCM-IMG-006
format: markdown_structured_payload
type: traceability
name: Medical Dictation Traceability
version: 1.0.0
status: modeled
---

# Medical Dictation Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-006
  type: traceability
  name: Medical Dictation Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
traces:
  capability_map:
    bcm_001: BCM-IMG-006
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-IMG-003
    - BCM-IMG-005
    - BCM-PER-003
    downstream_capabilities:
    - BCM-IMG-007
  domain_foundation:
    bounded_context: radiology-reporting
    primary_aggregate: RadiologyDictation (AGG-036)
```
