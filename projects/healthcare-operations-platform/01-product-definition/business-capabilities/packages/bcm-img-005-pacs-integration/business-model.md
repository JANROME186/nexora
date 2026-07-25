---
id: HOP-MODEL-BCM-IMG-005
format: markdown_structured_payload
type: business-model
name: PACS Integration Business Model
version: 1.0.0
status: modeled
---

# PACS Integration Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MODEL-BCM-IMG-005
  type: business-model
  name: PACS Integration Business Model
  version: 1.0.0
  status: modeled
  capability: BCM-IMG-005
domain_model:
  bounded_context: imaging-adapters
  aggregates:
    - id: AGG-035
      name: PacsIntegrationEndpoint
      type: root_aggregate
      description: Primary aggregate for PACS Integration.
```

## Domain Model Description
The aggregate `PacsIntegrationEndpoint` (`AGG-035`) manages state transitions and business invariants within the `imaging-adapters` context.
