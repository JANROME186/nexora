---
id: HOP-GEN-BCM-ATT-004
format: markdown_structured_payload
type: generation-plan
name: Admission Management Generation Plan
version: 0.1.0
status: modeled
---

# Admission Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-ATT-004
  type: generation-plan
  name: Admission Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-004
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for AdmissionRequest
  - DTOs for AdmissionRequest and AdmissionCatalogSelection
  - Controllers for generatable operations (list, get, reject)
  - Repository interfaces and persistence adapters
  - Event consumers for ReceptionVisitReadyForAdmission, TestDefinitionPublished,
    PriceListPublished and QuotationAccepted
  - API adapters
  frontend:
  - Employee portal admission request list and detail (SCR-ADM-004-02, SCR-ADM-004-03)
  - Routes and Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-005, RN-006 and RN-007
  - Contract tests for authorization
  - Acceptance test skeletons for orchestration rules
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-ADM-004-01
  description: Reception visit identity-confirmed precondition check (RN-001).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-ADM-004-02
  description: Published-catalog completeness validation (RN-002).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-ADM-004-03
  description: Tenant-configurable consent and sample-requirement acknowledgement
    gate (RN-003).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-ADM-004-04
  description: Cross-capability commit delegating to BCM-LAB-001 order commands (RN-004).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-ADM-004-05
  description: Admission intake wizard UI with catalog, consent and clinical-notes
    capture.
  maps_to_backlog: MVP-MOD-004-FE-001
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
