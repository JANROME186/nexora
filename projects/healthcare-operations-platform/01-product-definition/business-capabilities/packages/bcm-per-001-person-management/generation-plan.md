---
id: HOP-GEN-BCM-PER-001
format: markdown_structured_payload
type: generation-plan
name: Person Management Generation Plan
version: 0.1.0
status: modeled
---

# Person Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-PER-001
  type: generation-plan
  name: Person Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - Read-model projection scaffolding for PersonSearchIndex
  - DTOs for PersonNaturalKey, PersonSearchResult, PersonDuplicateDetectionResult
    and PersonMergeCoordination
  - Controllers for generatable operations (searchPersons, getPersonMergeCoordination)
  - Repository interfaces for PersonSearchIndex projection storage
  - Event consumers for PatientRegistered, PatientUpdated, PatientMerged, DoctorRegistered,
    DoctorCredentialVerified and DoctorSuspended
  - API adapters
  frontend:
  - Employee portal global person search screen (SCR-PER-001-01)
  - Routes
  - Forms
  - Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for natural-key normalization (RN-001), authorization (RN-005)
    and audit (RN-006)
  - Contract tests for search operations
  - Acceptance test skeletons for duplicate detection and merge coordination
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard definition skeleton
  - Alert definitions for PersonSearchIndexLagHigh and PersonNaturalKeyNormalizationFailures
custom_implementation_points:
- id: CUS-PER-001-01
  description: Cross-context primary document uniqueness enforcement (RN-002).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PER-001-02
  description: Duplicate detection confidence scoring and audit tracing (RN-003).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PER-001-03
  description: PersonSearchIndex projection ordering and idempotence policy (RN-004).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PER-001-04
  description: One-way hashing of national identifier values for duplicate detection
    (RN-007).
  maps_to_backlog: MVP-MOD-003-BE-002
- id: CUS-PER-001-05
  description: Cross-context merge coordination workflow.
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
