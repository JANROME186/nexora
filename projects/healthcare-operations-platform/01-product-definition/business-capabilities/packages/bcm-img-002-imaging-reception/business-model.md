---
id: HOP-MODEL-BCM-IMG-002
format: markdown_structured_payload
type: business-model
name: Imaging Reception Business Model
version: 1.0.0
status: modeled
---

# Imaging Reception Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-002
  type: business-model
  name: Imaging Reception Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-002
domain_model:
  bounded_context: imaging-operations
  aggregates:
    - id: AGG-032
      name: ImagingReceptionIntake
      type: root_aggregate
      description: Primary aggregate for Imaging Reception.
```

## Domain Model Description
The aggregate `ImagingReceptionIntake` (`AGG-032`) manages state transitions and business invariants within the `imaging-operations` context.
