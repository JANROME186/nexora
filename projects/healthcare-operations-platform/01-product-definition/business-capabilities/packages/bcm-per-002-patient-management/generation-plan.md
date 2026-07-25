---
id: HOP-GEN-BCM-PER-002
format: markdown_structured_payload
type: generation-plan
name: Patient Management Generation Plan
version: 0.1.0
status: modeled
---

# Patient Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-PER-002
  type: generation-plan
  name: Patient Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for Patient, PatientEmergencyContact, PatientRepresentative,
    PatientConsent and PatientDocument
  - DTOs for Patient, PatientSnapshot, PatientRepresentative, PatientConsent, PatientDocument
    and PatientEligibilityStatus
  - Controllers for generatable operations
  - Repository interfaces and baseline persistence adapters
  - Domain skeletons for Patient aggregate persistence
  - API adapters
  frontend:
  - Employee portal list and editor components (SCR-PAT-002-01, SCR-PAT-002-02)
  - Consent and representative panels (SCR-PAT-002-03, SCR-PAT-002-04)
  - Documents panel (SCR-PAT-002-06)
  - Routes
  - Forms
  - Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for CRUD, RN-001, RN-004, RN-008, RN-009, RN-010
  - Contract tests
  - Acceptance test skeletons for custom rules
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-PAT-002-01
  description: Duplicate detection integration at registration (RN-002).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PAT-002-02
  description: Aggregate boundary enforcement policy (RN-003).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PAT-002-03
  description: Patient merge with snapshot archival and projection rewiring (RN-005).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PAT-002-04
  description: Representative time-bound authorization enforcement (RN-006).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PAT-002-05
  description: Consent append-only revocation with immutable evidence (RN-007).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PAT-002-06
  description: Merge UI workspace preview and rewiring impact panel.
  maps_to_backlog: MVP-MOD-003-FE-001
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
