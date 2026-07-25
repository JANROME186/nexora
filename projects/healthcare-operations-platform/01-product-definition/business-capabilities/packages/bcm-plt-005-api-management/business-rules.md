---
id: HOP-BR-BCM-PLT-005
format: markdown_structured_payload
type: business-rules
name: API Management Business Rules
version: 1.1.0
status: modeled
---

# Api Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-PLT-005
  type: business-rules
  name: API Management Business Rules
  version: 1.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-005
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: Every API operation exposed by HOP must be classified as public, internal
    or partner before it can be published externally; unclassified operations remain
    internal-only by default.
  applies_to: ApiSurfaceRegistration
  enforcement_point: command:ClassifyApiOperation
  severity: critical
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-01
- id: RN-002
  statement: A partner operation cannot be invoked without a valid, non-revoked, non-expired
    PartnerApiKey whose scopes cover the operation and whose tenant matches the caller.
  applies_to: PartnerApiKey
  enforcement_point: authorization:api.partner.invoke
  severity: critical
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-02
- id: RN-003
  statement: Deprecating or introducing a breaking change to a published public or
    partner operation requires a documented deprecation window and migration note
    before it can be scheduled.
  applies_to: ApiSurfaceRegistration
  enforcement_point: command:ScheduleApiDeprecation
  severity: high
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-03
- id: RN-004
  statement: Public and partner API requests exceeding the configured rate-limit policy
    for their consumer or classification tier must be rejected with a canonical rate-limit
    error, not silently dropped or left unbounded.
  applies_to: RateLimitPolicy
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-04
- id: RN-005
  statement: Every API-management administrative action (classification change, partner
    key issuance/revocation, rate-limit change, deprecation scheduling) must be audited.
  applies_to: ApiSurfaceRegistration
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-05
- id: RN-006
  statement: API-management commands must execute within the actor's tenant scope;
    a tenant administrator cannot classify operations or issue keys for another tenant.
  applies_to: ApiSurfaceRegistration
  enforcement_point: authorization:api.classification.manage, authorization:api.partnerkey.manage
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-APIM-005-06
- id: RN-007
  statement: Public (non-partner-key) API requests must be identified for rate-limiting
    by RateLimitPolicy.consumerIdentificationMethod (IP address or session token)
    and rejected per RN-004 when the limit is exceeded (addressing TD-BE-015).
  applies_to: RateLimitPolicy
  enforcement_point: architecture_boundary
  severity: high
  audit_required: true
  generatable: false
  test_refs:
  - TST-APIM-005-07
- id: RN-008
  statement: 'API Gateway responses must inject production security and cache-control
    headers (HSTS, Content-Security-Policy, X-Frame-Options, X-Content-Type-Options,
    Cache-Control: no-store for sensitive endpoints) (addressing TD-FE-005).'
  applies_to: ApiSurfaceRegistration
  enforcement_point: architecture_boundary
  severity: high
  audit_required: false
  generatable: false
  test_refs:
  - TST-APIM-005-08
enforcement_summary:
  generatable_rules:
  - RN-006
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
  - RN-007
  - RN-008
```
