---
id: HOP-MODEL-BCM-IMG-003
format: markdown_structured_payload
type: business-model
name: Imaging Study Management Business Model
version: 1.0.0
status: modeled
---

# Imaging Study Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-003
  type: business-model
  name: Imaging Study Management Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-003
domain_model:
  bounded_context: imaging-operations
  aggregates:
    - id: AGG-033
      name: ImagingStudy
      type: root_aggregate
      description: Primary aggregate for Imaging Study Management.
```

## Domain Model Description
The aggregate `ImagingStudy` (`AGG-033`) manages state transitions and business invariants within the `imaging-operations` context.
