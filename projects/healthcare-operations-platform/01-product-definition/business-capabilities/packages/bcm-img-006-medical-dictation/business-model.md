---
id: HOP-MODEL-BCM-IMG-006
format: markdown_structured_payload
type: business-model
name: Medical Dictation Business Model
version: 1.0.0
status: modeled
---

# Medical Dictation Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-006
  type: business-model
  name: Medical Dictation Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-006
domain_model:
  bounded_context: radiology-reporting
  aggregates:
    - id: AGG-036
      name: RadiologyDictation
      type: root_aggregate
      description: Primary aggregate for Medical Dictation.
```

## Domain Model Description
The aggregate `RadiologyDictation` (`AGG-036`) manages state transitions and business invariants within the `radiology-reporting` context.
