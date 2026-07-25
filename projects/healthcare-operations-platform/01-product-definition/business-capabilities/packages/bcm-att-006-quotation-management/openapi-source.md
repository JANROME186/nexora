---
id: HOP-API-SRC-BCM-ATT-006
format: markdown_structured_payload
type: openapi-source
name: Quotation Management API Source Model
version: 0.2.0
status: modeled
---

# Quotation Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-ATT-006
  type: openapi-source
  name: Quotation Management API Source Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-006
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/care-delivery/quotations
  surface_classification: internal
  public_surface:
    status: required
    classification: public
    security: anonymous_rate_limited
    governed_by: BCM-PLT-005 ApiSurfaceRegistration/RateLimitPolicy (classification=public)
    exposed_operations:
    - operation_ref: startQuotation
      scopes:
      - quotation.request.public
      rate_limit_classification: public
      custom_reason: Anonymous public-website requests create a QuotationRequest in
        draft state from a ProspectiveContact only; issuing, accepting or converting
        still requires quotation.manage. See RN-009.
    note: Realizes the future_surfaces placeholder for COM-MOD-011 Public Website
      and Digital Growth. Reuses the existing startQuotation operation and ProspectiveContact
      schema; no new resource or duplicate schema.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - quotation.manage
resources:
- name: QuotationRequest
  operations:
  - id: listQuotations
    method: GET
    path: /
    scopes:
    - quotation.read
    generatable: true
  - id: getQuotation
    method: GET
    path: /{quotationId}
    scopes:
    - quotation.read
    generatable: true
  - id: startQuotation
    method: POST
    path: /
    scopes:
    - quotation.manage
    generatable: false
    custom_reason: Published-catalog validation.
  - id: issueQuotation
    method: POST
    path: /{quotationId}/issue
    scopes:
    - quotation.manage
    generatable: false
    custom_reason: Price-list resolution, pricing snapshot capture and discount policy
      enforcement.
  - id: acceptQuotation
    method: POST
    path: /{quotationId}/accept
    scopes:
    - quotation.manage
    generatable: false
    custom_reason: Validity-window enforcement.
  - id: convertQuotation
    method: POST
    path: /{quotationId}/convert
    scopes:
    - quotation.manage
    generatable: false
    custom_reason: Cross-capability delegation to BCM-LAB-001 CreateDiagnosticOrder.
  - id: cancelQuotation
    method: POST
    path: /{quotationId}/cancel
    scopes:
    - quotation.manage
    generatable: true
  - id: expireQuotation
    method: POST
    path: /{quotationId}/expire
    scopes:
    - quotation.manage
    generatable: true
schemas_source:
- QuotationRequest
- QuotationLine
- QuotationPricingSnapshot
- DiscountApplication
- ProspectiveContact
error_model:
  standard: rfc7807
  domain_errors:
  - code: QUOTATION_CATALOG_ITEM_NOT_PUBLISHED
    maps_to_rule: RN-001
  - code: QUOTATION_PRICING_SNAPSHOT_REQUIRED
    maps_to_rule: RN-002
  - code: QUOTATION_DISCOUNT_POLICY_EXCEEDED
    maps_to_rule: RN-003
  - code: QUOTATION_EXPIRED
    maps_to_rule: RN-004
  - code: QUOTATION_ORDER_BOUNDARY_VIOLATION
    maps_to_rule: RN-005
  - code: QUOTATION_SCOPE_MISMATCH
    maps_to_rule: RN-006
  - code: QUOTATION_TERMINAL_STATE_IMMUTABLE
    maps_to_rule: RN-007
```
