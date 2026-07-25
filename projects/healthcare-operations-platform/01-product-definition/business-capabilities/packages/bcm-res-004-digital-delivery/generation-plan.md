---
id: HOP-GEN-BCM-RES-004
format: markdown_structured_payload
type: generation-plan
name: Digital Delivery Generation Plan
version: 0.1.0
status: modeled
---

# Digital Delivery Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-RES-004
  type: generation-plan
  name: Digital Delivery Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for ResultDeliveryTicket and DeliveryAuthorizationCheck
  - Repository interfaces and persistence adapters
  - Event consumers for ResultReleased, ResultAmended and ReportGenerated
  - API adapters
  frontend:
  - Patient/doctor portal released-results list and detail (SCR-DLV-004-01, SCR-DLV-004-02)
  - Routes and Client SDK usage
  mobile:
  - Mobile released-results list and detail flows (MOB-DLV-004-01, MOB-DLV-004-02)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-008
  - Contract tests for authorization
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-DLV-004-01
  description: Release-state gate before any external visibility (RN-001).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-02
  description: Patient self-ownership authorization check (RN-002).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-03
  description: Patient representative authorization verification (RN-003).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-04
  description: Referring/treating doctor relationship verification (RN-004).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-05
  description: Amendment-triggered withhold-and-reauthorize workflow (RN-005).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-06
  description: View-state recording distinct from generic access audit (RN-007).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-DLV-004-07
  description: Patient/doctor portal and mobile released-result detail UI with view-state
    trigger.
  maps_to_backlog: MVP-MOD-007-PORTAL-001
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
