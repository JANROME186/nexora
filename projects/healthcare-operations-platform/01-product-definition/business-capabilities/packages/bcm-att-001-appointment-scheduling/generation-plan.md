---
id: HOP-GEN-BCM-ATT-001
format: markdown_structured_payload
type: generation-plan
name: Appointment Scheduling Generation Plan
version: 0.1.0
status: modeled
---

# Appointment Scheduling Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-ATT-001
  type: generation-plan
  name: Appointment Scheduling Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for AppointmentSlot
  - DTOs for AppointmentSlot, RequestedCatalogItem and PreparationSummary
  - Controllers for generatable operations (list, get, cancel)
  - Repository interfaces and persistence adapters
  - Event consumers for PatientRegistrationCommitted, TestDefinitionPublished, BranchUpdated
    and DiagnosticOrderCreated
  - API adapters
  frontend:
  - Employee portal appointment calendar and detail (SCR-APT-001-02, SCR-APT-001-03)
  - Routes and Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-004 and RN-007
  - Contract tests for authorization
  - Acceptance test skeletons for orchestration rules
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-APT-001-01
  description: Branch operational-status validation at confirmation (RN-001).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-APT-001-02
  description: Overlap detection across confirmed appointments for the same patient
    and branch (RN-002).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-APT-001-03
  description: Published-catalog validation for requested items (RN-003).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-APT-001-04
  description: Check-in handoff to BCM-LAB-001 CreateDiagnosticOrder (RN-005).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-APT-001-05
  description: Tenant-configurable no-show grace-period policy (RN-006).
  maps_to_backlog: MVP-MOD-004-BE-002
- id: CUS-APT-001-06
  description: Appointment scheduler wizard UI with calendar and preparation-instruction
    surfacing.
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
