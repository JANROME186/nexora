---
id: HOP-TRACE-BCM-IMG-001
format: markdown_structured_payload
type: traceability
name: Imaging Appointment Scheduling Traceability
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-IMG-001
  type: traceability
  name: Imaging Appointment Scheduling Traceability
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
traces:
  capability_map:
    bcm_001: BCM-IMG-001
    domain: DOM-06 Imaging
  dependency_map:
    required_capabilities:
    - BCM-PER-002
    - BCM-ORG-003
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-IMG-002
    - BCM-IMG-003
  domain_foundation:
    bounded_context: imaging-operations
    primary_aggregate: ImagingAppointmentSlot (AGG-031)
```
