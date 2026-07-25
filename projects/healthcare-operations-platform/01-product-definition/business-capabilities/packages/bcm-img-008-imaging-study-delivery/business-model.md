---
id: HOP-MODEL-BCM-IMG-008
format: markdown_structured_payload
type: business-model
name: Imaging Study Delivery Business Model
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-008
  type: business-model
  name: Imaging Study Delivery Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-008
domain_model:
  bounded_context: radiology-delivery
  aggregates:
    - id: AGG-038
      name: ImagingDeliveryPackage
      type: root_aggregate
      description: Primary aggregate for Imaging Study Delivery.
```

## Domain Model Description
The aggregate `ImagingDeliveryPackage` (`AGG-038`) manages state transitions and business invariants within the `radiology-delivery` context.
