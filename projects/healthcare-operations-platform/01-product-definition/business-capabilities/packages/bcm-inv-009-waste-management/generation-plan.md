---
id: HOP-GEN-BCM-INV-009
format: markdown_structured_payload
type: generation-plan
name: Waste Management Generation Plan
version: 0.1.0
status: modeled
---

# Waste Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-INV-009
  type: generation-plan
  name: Waste Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for WasteRecord
  - API adapter for listWasteRecords
  frontend:
  - Waste and Disposal screen shell (custom disposal-confirmation action wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-002, RN-005
  operations:
  - Metric and log wiring from observability-model.md
  - Waste-rate alert definitions
custom_implementation_points:
- id: CUS-WST-009-01
  description: Real-time remainingQuantity guard before disposal (RN-001).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-WST-009-02
  description: Delegated multi-field ApplyWasteDisposal mutation with conditional
    StockLot status transition (RN-003, RN-004).
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
