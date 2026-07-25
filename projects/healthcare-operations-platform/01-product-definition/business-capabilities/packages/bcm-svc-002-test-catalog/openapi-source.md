---
id: HOP-API-SRC-BCM-SVC-002
format: markdown_structured_payload
type: openapi-source
name: Test Catalog API Source Model
version: 0.2.0
status: modeled
---

# Test Catalog Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-SVC-002
  type: openapi-source
  name: Test Catalog API Source Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
  note: 'Source contract model. Rendered OpenAPI, controllers, DTOs and SDKs are generated
    outputs declared in generation-plan.md.

    '
api:
  base_path: /api/catalog/tests
  surface_classification: internal
  public_surface:
    status: required
    classification: public
    security: anonymous_rate_limited
    governed_by: BCM-PLT-005 ApiSurfaceRegistration/RateLimitPolicy (classification=public)
    exposed_operations:
    - operation_ref: getPublishedTestSnapshot
      scopes:
      - catalog.test.public_read
      rate_limit_classification: public
    - id: listPublishedTests
      method: GET
      path: /published
      scopes:
      - catalog.test.public_read
      generatable: true
      classification: public
      custom_reason: Published-only projection filter over the existing TestDefinition
        list; no new resource or schema.
    note: Realizes the future_surfaces placeholder for COM-MOD-011 Public Website
      and Digital Growth. Reuses the existing getPublishedTestSnapshot operation and
      PublishedTestSnapshot schema; adds one new published-only list projection.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - catalog.test.read
resources:
- name: TestDefinition
  operations:
  - id: listTests
    method: GET
    path: /
    scopes:
    - catalog.test.read
    generatable: true
  - id: getTest
    method: GET
    path: /{testId}
    scopes:
    - catalog.test.read
    generatable: true
  - id: createTest
    method: POST
    path: /
    scopes:
    - catalog.test.write
    generatable: true
  - id: updateTest
    method: PUT
    path: /{testId}
    scopes:
    - catalog.test.write
    generatable: true
  - id: publishTest
    method: POST
    path: /{testId}/publish
    scopes:
    - catalog.test.publish
    generatable: false
    custom_reason: Publication validation and snapshot freeze are custom rules.
  - id: deprecateTest
    method: POST
    path: /{testId}/deprecate
    scopes:
    - catalog.test.publish
    generatable: true
  - id: getPublishedTestSnapshot
    method: GET
    path: /{testId}/published-snapshot
    scopes:
    - catalog.test.read
    generatable: false
    custom_reason: Returns frozen version-aware snapshot for downstream consumers.
schemas_source:
- TestDefinition
- TestAnalyteLink
- TestSampleRequirementLink
- PublishedTestSnapshot
error_model:
  standard: rfc7807
  domain_errors:
  - code: CATALOG_TEST_CODE_CONFLICT
    maps_to_rule: RN-001
  - code: CATALOG_TEST_UNIT_REQUIRED
    maps_to_rule: RN-002
  - code: CATALOG_TEST_NOT_PUBLISHABLE
    maps_to_rule: RN-003
```
