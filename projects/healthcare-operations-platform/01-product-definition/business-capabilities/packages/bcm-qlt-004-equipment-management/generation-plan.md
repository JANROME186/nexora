---
id: HOP-GEN-BCM-QLT-004
format: markdown_structured_payload
type: generation-plan
name: Equipment Management Generation Plan
version: 0.1.0
status: modeled
---

# Equipment Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-QLT-004
  type: generation-plan
  name: Equipment Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for EquipmentAvailabilityChange
  - API adapter for getEquipmentProfile
  frontend:
  - Equipment Registry screen shell (custom profile/availability actions wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-002, RN-005
  operations:
  - Metric and log wiring from observability-model.md
  - Prolonged-out-of-service alert definitions
custom_implementation_points:
- id: CUS-EQP-004-01
  description: itemType eligibility check before profile assignment (RN-001).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-EQP-004-02
  description: Delegated single-field mutation boundary over InventoryItem.equipmentProfile
    (RN-003).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-EQP-004-03
  description: Event-driven availability transition from BCM-QLT-003/005 signals (RN-004).
  maps_to_backlog: COM-MOD-010-BE-002
do_not_write_manually:
- CRUD scaffolding
- DTOs
- Controllers
- Repositories
- Swagger documentation
- SDKs
- Repetitive documentation
- Repetitive test cases
provenance:
  source_models:
  - business-model.md
  - business-rules.md
  - processes.md
  - events.md
  - openapi-source.md
  - ui-model.md
  - permissions.md
  - observability-model.md
  generation_metadata_required: true
```
