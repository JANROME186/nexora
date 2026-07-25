---
id: HOP-GEN-BCM-QLT-003
format: markdown_structured_payload
type: generation-plan
name: Calibration Management Generation Plan
version: 0.1.0
status: modeled
---

# Calibration Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-QLT-003
  type: generation-plan
  name: Calibration Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for CalibrationEvent
  - API adapter for listCalibrations
  frontend:
  - Calibration Log screen shell (custom recording action wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  tests:
  - Repetitive unit tests for RN-004, RN-005
  operations:
  - Metric and log wiring from observability-model.md
  - Overdue-calibration and calibration-failed alert definitions
custom_implementation_points:
- id: CUS-CAL-003-01
  description: itemType eligibility check before recording (RN-001).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-CAL-003-02
  description: Conditional CalibrationFailed event publication on fail result, without
    direct equipmentProfile writes (RN-002).
  maps_to_backlog: COM-MOD-010-BE-002
- id: CUS-CAL-003-03
  description: Delegated append-only mutation boundary over InventoryItem.calibrationRecord
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
