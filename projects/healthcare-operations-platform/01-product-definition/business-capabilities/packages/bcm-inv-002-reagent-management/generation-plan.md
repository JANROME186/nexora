---
id: HOP-GEN-BCM-INV-002
format: markdown_structured_payload
type: generation-plan
name: Reagent Management Generation Plan
version: 0.1.0
status: modeled
---

# Reagent Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-INV-002
  type: generation-plan
  name: Reagent Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-002
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for ReagentProfileAssignment
  - API adapter for getReagentProfile
  frontend:
  - Reagent Profile Panel shell (custom assignment action wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-002, RN-004
  operations:
  - Metric and log wiring from observability-model.md
custom_implementation_points:
- id: CUS-RGT-002-01
  description: itemType eligibility check before assignment (RN-001).
  maps_to_backlog: COM-MOD-010-BE-001
- id: CUS-RGT-002-02
  description: Delegated single-field mutation boundary over InventoryItem.reagentProfile
    (RN-003).
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
