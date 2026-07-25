---
id: HOP-GEN-BCM-QLT-005
format: markdown_structured_payload
type: generation-plan
name: Maintenance Management Generation Plan
version: 0.1.0
status: modeled
---

# Maintenance Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-QLT-005
  type: generation-plan
  name: Maintenance Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-005
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for MaintenanceEvent
  - API adapter for listMaintenanceEvents
  frontend:
  - Maintenance Log screen shell (custom start/complete actions wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-004, RN-005
  operations:
  - Metric and log wiring from observability-model.md
  - Downtime alert definitions
custom_implementation_points:
- id: CUS-MNT-005-01
  description: itemType eligibility check before recording (RN-001).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-MNT-005-02
  description: MaintenanceScheduled/MaintenanceCompleted event publication without
    direct equipmentProfile writes (RN-002).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-MNT-005-03
  description: Delegated append-only mutation boundary over InventoryItem.maintenanceRecord
    (RN-003).
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
