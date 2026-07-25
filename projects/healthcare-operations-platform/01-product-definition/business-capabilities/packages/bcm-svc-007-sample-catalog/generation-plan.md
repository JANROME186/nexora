---
id: HOP-GEN-BCM-SVC-007
format: markdown_structured_payload
type: generation-plan
name: Sample Catalog Generation Plan
version: 0.1.0
status: modeled
---

# Sample Catalog Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-SVC-007
  type: generation-plan
  name: Sample Catalog Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for SampleType, SampleRequirement
  - DTOs
  - Controllers for generatable operations
  - Repository interfaces and baseline persistence adapters
  - Domain skeleton for sample catalog projection on TestDefinition context
  - API adapters
  frontend:
  - Employee portal list and editor components (SCR-SVC-007-01, SCR-SVC-007-02, SCR-SVC-007-03)
  - Routes
  - Forms
  - Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-001, RN-002, RN-006
  - Contract tests
  - Acceptance test skeletons
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard definition skeleton
  - Alert definition for HighSampleRequirementPublishFailureRate
custom_implementation_points:
- id: CUS-SVC-007-01
  description: Sample type publication validation at publish time (RN-003).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-007-02
  description: Immutable versioning and snapshot freeze on edit (RN-004).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-007-03
  description: Matrix-specific handling completeness validation (RN-005).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-007-04
  description: Published sample requirement snapshot projection for collection and
    reception.
  maps_to_backlog: MVP-MOD-002-BE-002
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
