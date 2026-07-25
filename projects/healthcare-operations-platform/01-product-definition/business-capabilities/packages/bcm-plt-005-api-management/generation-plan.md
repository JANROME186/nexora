---
id: HOP-GEN-BCM-PLT-005
format: markdown_structured_payload
type: generation-plan
name: API Management Generation Plan
version: 0.1.0
status: modeled
---

# Api Management Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-PLT-005
  type: generation-plan
  name: API Management Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-005
  compiler_strategy: model_to_platform
generated_outputs:
  backend:
  - DTOs for ApiSurfaceRegistration, PartnerApiKey and RateLimitPolicy
  - Repository interfaces and persistence adapters for governance metadata
  - API adapters for listApiOperations, revokePartnerApiKey, listPartnerApiKeys, setRateLimitPolicy
  frontend:
  - API Surface Classification screen shell (classification form wired separately)
  - Partner API Keys screen shell (issuance form wired separately)
  contracts:
  - Rendered OpenAPI document from openapi-source.md
  - Typed SDK generated with an OpenAPI-Generator-based TypeScript client pipeline.
    This capability is designated the pilot target for TD-STACK-003's acceptance criterion
    ("a pilot TypeScript client generated from one capability's openapi-source.md
    is evaluated against the equivalent hand-written client"), because BCM-PLT-005
    governs the partner-consumer surface that most concretely needs a generated, versioned
    SDK rather than a hand-written one.
  - Swagger documentation
  tests:
  - Repetitive unit tests for RN-006
  - Contract tests for authorization
  operations:
  - Metric and log wiring from observability-model.md
  - Dashboard skeleton
  - Alert definitions
custom_implementation_points:
- id: CUS-APIM-005-01
  description: Classification defaulting and publish-gating logic (RN-001).
  maps_to_backlog: MVP-MOD-008-BE-001
- id: CUS-APIM-005-02
  description: Partner key validity, scope-coverage and tenant-match authorization
    check (RN-002).
  maps_to_backlog: MVP-MOD-008-BE-001
- id: CUS-APIM-005-03
  description: Deprecation-window and migration-note completeness governance (RN-003).
  maps_to_backlog: MVP-MOD-008-BE-002
- id: CUS-APIM-005-04
  description: Rate-limit enforcement middleware using an open-source rate-limiting
    library (e.g. Bucket4j), evaluated for the specific consumer/classification model
    (RN-004).
  maps_to_backlog: MVP-MOD-008-BE-002
- id: CUS-APIM-005-05
  description: Cross-entity audit-obligation wiring for all governance actions (RN-005).
  maps_to_backlog: MVP-MOD-008-BE-002
- id: CUS-APIM-005-06
  description: OpenAPI-Generator TypeScript client pilot evaluation for this capability's
    employee-portal SDK usage (TD-STACK-003 acceptance criterion).
  maps_to_backlog: MVP-MOD-008-FE-001
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
