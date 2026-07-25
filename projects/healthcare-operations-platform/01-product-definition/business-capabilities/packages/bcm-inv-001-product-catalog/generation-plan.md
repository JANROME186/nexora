---
id: HOP-GEN-BCM-INV-001
format: markdown_structured_payload
type: generation-plan
name: Product Catalog Generation Plan
version: 0.1.0
status: modeled
---

# Product Catalog Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-INV-001
  type: generation-plan
  name: Product Catalog Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-001
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for InventoryItem, StockSummary, ReagentProfile, EquipmentProfile
  - Repository interfaces and persistence adapters for InventoryItem
  - API adapters for createInventoryItem, listInventoryItems, getInventoryItem, updateInventoryItem
  frontend:
  - Inventory Item Catalog screen (list, register, update)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-001, RN-005
  - Contract tests for authorization
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
custom_implementation_points:
- id: CUS-CAT-001-01
  description: itemType/classification consistency validation (RN-002).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-CAT-001-02
  description: Field-level delegation boundary enforcement across all 13 COM-MOD-010
    capabilities (RN-003).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-CAT-001-03
  description: Discontinuation gate that consults open stock/lot/purchase-order state
    across delegated capabilities (RN-004).
  maps_to_backlog: COM-MOD-010-BE-001
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
