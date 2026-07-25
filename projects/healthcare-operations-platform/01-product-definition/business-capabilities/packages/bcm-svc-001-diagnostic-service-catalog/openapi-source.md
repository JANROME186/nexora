---
id: HOP-API-SRC-BCM-SVC-001
format: markdown_structured_payload
type: openapi-source
name: Diagnostic Service Catalog API Source Model
version: 0.2.0
status: modeled
---

# Diagnostic Service Catalog Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-SVC-001
  type: openapi-source
  name: Diagnostic Service Catalog API Source Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md and must not be authored
    manually.

    '
api:
  base_path: /api/catalog/diagnostic-services
  surface_classification: internal
  public_surface:
    status: required
    classification: public
    security: anonymous_rate_limited
    governed_by: BCM-PLT-005 ApiSurfaceRegistration/RateLimitPolicy (classification=public)
    exposed_operations:
    - operation_ref: getPublishedServiceSnapshot
      scopes:
      - catalog.service.public_read
      rate_limit_classification: public
    - id: listPublishedServices
      method: GET
      path: /published
      scopes:
      - catalog.service.public_read
      generatable: true
      classification: public
      custom_reason: Published-only projection filter over the existing DiagnosticService
        list; no new resource or schema.
    note: Realizes the future_surfaces placeholder for COM-MOD-011 Public Website
      and Digital Growth. Reuses the existing getPublishedServiceSnapshot operation
      and PublishedServiceSnapshot schema; adds one new published-only list projection.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - catalog.service.read
resources:
- name: DiagnosticService
  operations:
  - id: listDiagnosticServices
    method: GET
    path: /
    scopes:
    - catalog.service.read
    generatable: true
  - id: getDiagnosticService
    method: GET
    path: /{serviceId}
    scopes:
    - catalog.service.read
    generatable: true
  - id: createDiagnosticService
    method: POST
    path: /
    scopes:
    - catalog.service.write
    generatable: true
  - id: updateDiagnosticService
    method: PUT
    path: /{serviceId}
    scopes:
    - catalog.service.write
    generatable: true
  - id: publishDiagnosticService
    method: POST
    path: /{serviceId}/publish
    scopes:
    - catalog.service.publish
    generatable: false
    custom_reason: Publication validation and snapshot freeze are custom rules.
  - id: deprecateDiagnosticService
    method: POST
    path: /{serviceId}/deprecate
    scopes:
    - catalog.service.publish
    generatable: true
  - id: getPublishedServiceSnapshot
    method: GET
    path: /{serviceId}/published-snapshot
    scopes:
    - catalog.service.read
    generatable: false
    custom_reason: Returns frozen version-aware snapshot for downstream consumers.
schemas_source:
- DiagnosticService
- DiagnosticServiceCategory
- ServiceComponentLink
- PublishedServiceSnapshot
error_model:
  standard: rfc7807
  domain_errors:
  - code: CATALOG_SERVICE_CODE_CONFLICT
    maps_to_rule: RN-001
  - code: CATALOG_SERVICE_NOT_PUBLISHABLE
    maps_to_rule: RN-002
```
