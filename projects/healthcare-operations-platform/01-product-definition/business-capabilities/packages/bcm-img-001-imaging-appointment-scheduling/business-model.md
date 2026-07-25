---
id: HOP-MODEL-BCM-IMG-001
format: markdown_structured_payload
type: business-model
name: Imaging Appointment Scheduling Business Model
version: 1.0.0
status: modeled
---

# Imaging Appointment Scheduling Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-001
  type: business-model
  name: Imaging Appointment Scheduling Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-001
domain_model:
  bounded_context: imaging-operations
  aggregates:
    - id: AGG-031
      name: ImagingAppointmentSlot
      type: root_aggregate
      description: Primary aggregate for Imaging Appointment Scheduling.
```

## Domain Model Description
The aggregate `ImagingAppointmentSlot` (`AGG-031`) manages state transitions and business invariants within the `imaging-operations` context.
