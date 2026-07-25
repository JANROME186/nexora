---
id: HOP-GEN-BCM-PER-003
format: markdown_structured_payload
type: generation-plan
name: Doctor Management Generation Plan
version: 0.1.0
status: modeled
---

# Doctor Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-PER-003
  type: generation-plan
  name: Doctor Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for Doctor, ProfessionalCredential, SpecialtyAssignment and DoctorPortalAccessBaseline
  - DTOs for Doctor, DoctorSnapshot, ProfessionalCredential, SpecialtyAssignment
  - Controllers for generatable operations
  - Repository interfaces and baseline persistence adapters
  - Domain skeletons for Doctor aggregate persistence
  - API adapters
  frontend:
  - Employee portal list and editor components (SCR-DOC-003-01, SCR-DOC-003-02)
  - Credential and specialty panels (SCR-DOC-003-03, SCR-DOC-003-04)
  - Routes, forms and Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-001, RN-008, RN-009
  - Contract tests
  - Acceptance test skeletons for custom rules
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-DOC-003-01
  description: Duplicate detection integration at registration (RN-002).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-DOC-003-02
  description: Aggregate boundary enforcement policy (RN-003).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-DOC-003-03
  description: Doctor activation cascade upon credential verification (RN-004).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-DOC-003-04
  description: Credential expiration watcher and doctor re-verification flag (RN-005).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-DOC-003-05
  description: Doctor eligibility filter for suspended and retired doctors (RN-006).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-DOC-003-06
  description: Doctor portal access baseline preparation without provisioning (RN-007).
  maps_to_backlog: MVP-MOD-003-BE-002
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
