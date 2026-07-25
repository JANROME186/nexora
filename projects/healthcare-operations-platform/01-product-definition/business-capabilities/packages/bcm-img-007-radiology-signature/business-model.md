---
id: HOP-MODEL-BCM-IMG-007
format: markdown_structured_payload
type: business-model
name: Radiology Signature Business Model
version: 1.0.0
status: modeled
---

# Radiology Signature Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-007
  type: business-model
  name: Radiology Signature Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-007
domain_model:
  bounded_context: radiology-reporting
  aggregates:
    - id: AGG-037
      name: RadiologyReport
      type: root_aggregate
      description: Primary aggregate for Radiology Signature.
```

## Domain Model Description
The aggregate `RadiologyReport` (`AGG-037`) manages state transitions and business invariants within the `radiology-reporting` context.
