---
id: HOP-GEN-BCM-SVC-002
format: markdown_structured_payload
type: generation-plan
name: Test Catalog Generation Plan
version: 0.1.0
status: modeled
---

# Test Catalog Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-SVC-002
  type: generation-plan
  name: Test Catalog Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for TestDefinition, TestAnalyteLink, TestSampleRequirementLink
  - DTOs
  - Controllers for generatable operations
  - Repository interfaces and baseline persistence adapters
  - Domain skeleton for TestDefinition aggregate
  - API adapters
  frontend:
  - Employee portal list and editor components (SCR-SVC-002-01, SCR-SVC-002-02)
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
  - Alert definition for HighTestPublishFailureRate
custom_implementation_points:
- id: CUS-SVC-002-01
  description: Publish validation across sample requirement publication state (RN-003).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-002-02
  description: Immutable versioning and snapshot freeze on edit (RN-004).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-002-03
  description: Analyte publication validation at publish time (RN-005).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-002-04
  description: Published test snapshot projection endpoint for downstream consumers.
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
