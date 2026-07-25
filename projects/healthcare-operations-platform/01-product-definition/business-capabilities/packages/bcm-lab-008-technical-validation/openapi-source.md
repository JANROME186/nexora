---
id: HOP-API-SRC-BCM-LAB-008
format: markdown_structured_payload
type: openapi-source
name: Technical Validation API Source Model
version: 0.1.0
status: modeled
---

# Technical Validation Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-LAB-008
  type: openapi-source
  name: Technical Validation API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-008
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/clinical-operations/laboratory-results/{resultId}/technical-validation
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - result.manage
resources:
- name: TechnicalValidation
  operations:
  - id: listTechnicalValidationWorklist
    method: GET
    path: /technical-validation-worklist
    scopes:
    - result.manage
    generatable: true
  - id: performTechnicalValidation
    method: POST
    path: /validate
    scopes:
    - result.manage
    generatable: false
    custom_reason: Multi-criterion acceptance check and segregation-of-duties enforcement.
  - id: flagCriticalResult
    method: POST
    path: /flag-critical
    scopes:
    - result.manage
    generatable: false
    custom_reason: Critical-threshold comparison and mandatory notification hook.
schemas_source:
- TechnicalValidationWorklistEntry
- TechnicalAcceptanceCheck
- CriticalThresholdCheck
error_model:
  standard: rfc7807
  domain_errors:
  - code: RESULT_UNRESOLVED_INCIDENT_BLOCKS_VALIDATION
    maps_to_rule: RN-001
  - code: RESULT_SEGREGATION_OF_DUTIES_VIOLATION
    maps_to_rule: RN-002
  - code: RESULT_CRITICAL_FLAG_REQUIRED
    maps_to_rule: RN-003
  - code: RESULT_CRITICAL_NOTIFICATION_TRACE_MISSING
    maps_to_rule: RN-004
  - code: RESULT_BOUNDARY_VIOLATION
    maps_to_rule: RN-005
  - code: RESULT_SCOPE_MISMATCH
    maps_to_rule: RN-006
```
