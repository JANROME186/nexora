---
id: HOP-MODEL-BCM-IMG-004
format: markdown_structured_payload
type: business-model
name: DICOM Integration Business Model
version: 1.0.0
status: modeled
---

# DICOM Integration Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-004
  type: business-model
  name: DICOM Integration Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-004
domain_model:
  bounded_context: imaging-adapters
  aggregates:
    - id: AGG-034
      name: DicomAdapterConfiguration
      type: root_aggregate
      description: Primary aggregate for DICOM Integration.
```

## Domain Model Description
The aggregate `DicomAdapterConfiguration` (`AGG-034`) manages state transitions and business invariants within the `imaging-adapters` context.
