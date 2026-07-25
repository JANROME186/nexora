---
id: HOP-GEN-BCM-SVC-001
format: markdown_structured_payload
type: generation-plan
name: Diagnostic Service Catalog Generation Plan
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-SVC-001
  type: generation-plan
  name: Diagnostic Service Catalog Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - CRUD scaffolding for DiagnosticService, DiagnosticServiceCategory, ServiceComponentLink
  - DTOs
  - Controllers for generatable operations
  - Repository interfaces and baseline persistence adapters
  - Domain skeletons for TestDefinition catalog projection
  - API adapters
  frontend:
  - Employee portal list and editor components (SCR-SVC-001-01, SCR-SVC-001-02)
  - Routes
  - Forms
  - Client SDK usage
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK
  - Swagger documentation
  tests:
  - Repetitive unit tests for CRUD and validation rules RN-001, RN-005, RN-006
  - Contract tests
  - Acceptance test skeletons
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard definition skeleton
  - Alert definition for HighServicePublishFailureRate
custom_implementation_points:
- id: CUS-SVC-001-01
  description: Publication validation across component publication state (RN-002).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-001-02
  description: Immutable versioning and snapshot freeze on edit of published service
    (RN-003).
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-001-03
  description: Published snapshot projection endpoint for downstream consumers.
  maps_to_backlog: MVP-MOD-002-BE-002
- id: CUS-SVC-001-04
  description: Order eligibility exclusion for deprecated services (RN-004).
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
