---
id: HOP-API-SRC-BCM-RES-002
format: markdown_structured_payload
type: openapi-source
name: PDF Report Generation API Source Model
version: 0.1.0
status: modeled
---

# Pdf Report Generation Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-RES-002
  type: openapi-source
  name: PDF Report Generation API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-002
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/results/{resultId}/reports
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - report.generate
    - report.read
resources:
- name: GeneratedResultReport
  operations:
  - id: generateResultReport
    method: POST
    path: /
    scopes:
    - report.generate
    generatable: false
    custom_reason: PDF rendering, hash computation and delegated document storage.
  - id: getResultReport
    method: GET
    path: /{reportId}
    scopes:
    - report.read
    generatable: false
    custom_reason: Runtime integrity re-verification before serving.
  - id: listResultReports
    method: GET
    path: /
    scopes:
    - report.read
    generatable: true
schemas_source:
- GeneratedResultReport
- DocumentReference
error_model:
  standard: rfc7807
  domain_errors:
  - code: REPORT_RESULT_NOT_RELEASED
    maps_to_rule: RN-001
  - code: REPORT_INTEGRITY_METADATA_INCOMPLETE
    maps_to_rule: RN-002
  - code: REPORT_AMENDMENT_REGENERATION_REQUIRED
    maps_to_rule: RN-003
  - code: REPORT_INTEGRITY_HASH_MISMATCH
    maps_to_rule: RN-004
  - code: REPORT_BOUNDARY_VIOLATION
    maps_to_rule: RN-005
  - code: REPORT_SCOPE_MISMATCH
    maps_to_rule: RN-006
```
