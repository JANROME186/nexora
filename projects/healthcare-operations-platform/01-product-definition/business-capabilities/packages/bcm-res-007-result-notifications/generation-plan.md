---
id: HOP-GEN-BCM-RES-007
format: markdown_structured_payload
type: generation-plan
name: Result Notifications Generation Plan
version: 0.1.0
status: modeled
---

# Result Notifications Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-RES-007
  type: generation-plan
  name: Result Notifications Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for ResultNotificationRequest read paths
  - DTOs for ResultNotificationRequest
  - Controllers for generatable operations (list, get)
  - Repository interfaces and persistence adapters
  - Event consumers for ResultDeliveryAuthorized, ResultFlaggedCritical, ResultAmended,
    NotificationDispatched, NotificationDeliveryFailed
  - API adapters
  frontend:
  - Employee portal result notification history (SCR-RNT-007-01)
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
- id: CUS-RNT-007-01
  description: Delivery-authorization precondition before composing a delivered notification
    (RN-001).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-RNT-007-02
  description: Mandatory, unconditional composition for every critical-result event
    (RN-002).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-RNT-007-03
  description: Content/dispatch boundary enforcement delegating physical dispatch
    to BCM-PLT-003 (RN-003).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-RNT-007-04
  description: Read-only architecture-boundary enforcement against LaboratoryResult,
    Patient and Doctor (RN-004).
  maps_to_backlog: MVP-MOD-007-BE-002
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
