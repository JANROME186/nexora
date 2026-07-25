---
id: HOP-GEN-BCM-INV-006
format: markdown_structured_payload
type: generation-plan
name: Stock Exits Generation Plan
version: 0.1.0
status: modeled
---

# Stock Exits Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-INV-006
  type: generation-plan
  name: Stock Exits Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-006
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for StockExitRecord
  - API adapter for listStockExits
  frontend:
  - Stock Exits screen shell (custom exit-confirmation action wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-002, RN-005
  operations:
  - Metric and log wiring from observability-model.md
custom_implementation_points:
- id: CUS-SXT-006-01
  description: Real-time remainingQuantity guard before decrementing (RN-001).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-SXT-006-02
  description: Delegated multi-field ApplyStockExit mutation across InventoryItem
    and StockLot (RN-003).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-SXT-006-03
  description: Expired/disposed lot eligibility guard (RN-004).
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
