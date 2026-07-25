---
id: HOP-GEN-BCM-RES-006
format: markdown_structured_payload
type: generation-plan
name: Critical Results Generation Plan
version: 0.1.0
status: modeled
---

# Critical Results Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-RES-006
  type: generation-plan
  name: Critical Results Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for CriticalResultEscalation read paths
  - DTOs for CriticalResultEscalation
  - Controllers for generatable operations (list)
  - Repository interfaces and persistence adapters
  - Event consumers for ResultFlaggedCritical
  - API adapters
  frontend:
  - Employee portal escalation worklist (SCR-CRR-006-01)
  - Routes and Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-005 and RN-006
  - Contract tests for authorization
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-CRR-006-01
  description: Mandatory, unconditional escalation-record creation on every critical
    flag (RN-001).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-CRR-006-02
  description: Deadline-driven tier progression and re-notification trigger (RN-002).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-CRR-006-03
  description: Terminal-state guard for closing an escalation (RN-003).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-CRR-006-04
  description: Read-only architecture-boundary enforcement against LaboratoryResult
    (RN-004).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-CRR-006-05
  description: Escalation worklist and acknowledgement UI.
  maps_to_backlog: MVP-MOD-007-FE-001
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
