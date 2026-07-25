---
id: HOP-GEN-BCM-RES-001
format: markdown_structured_payload
type: generation-plan
name: Result Management Generation Plan
version: 0.1.0
status: modeled
---

# Result Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-RES-001
  type: generation-plan
  name: Result Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-001
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for ResultSearchIndexEntry and ResultAccessAuditEntry
  - Repository interfaces and persistence adapters for the read projection
  - API adapters
  frontend:
  - Employee portal result search/worklist and detail screens (SCR-RMG-001-01, SCR-RMG-001-02)
  - Routes and Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-005
  - Contract tests for authorization
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-RMG-001-01
  description: Event-sourced ResultSearchIndexEntry projection rebuild (RN-002).
  maps_to_backlog: MVP-MOD-007-BE-001
- id: CUS-RMG-001-02
  description: Row-level role- and laboratory-scoped query filtering (RN-003).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-RMG-001-03
  description: Mandatory access-audit append on every query response (RN-004).
  maps_to_backlog: MVP-MOD-007-BE-002
- id: CUS-RMG-001-04
  description: Read-only architecture-boundary enforcement against LaboratoryResult
    (RN-001).
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
