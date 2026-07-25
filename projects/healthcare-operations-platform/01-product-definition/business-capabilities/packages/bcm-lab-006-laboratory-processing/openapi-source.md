---
id: HOP-API-SRC-BCM-LAB-006
format: markdown_structured_payload
type: openapi-source
name: Laboratory Processing API Source Model
version: 0.1.0
status: modeled
---

# Laboratory Processing Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-LAB-006
  type: openapi-source
  name: Laboratory Processing API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/clinical-operations/laboratory-results
  surface_classification: internal
  future_surfaces:
  - classification: system
    status: device_message_ingestion_via_bcm_plt_004
    note: Device messages arrive normalized through BCM-PLT-004's integration boundary,
      targeted for MVP-MOD-008.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - result.capture
    - result.manage
resources:
- name: LaboratoryResult
  operations:
  - id: listProcessingWorklist
    method: GET
    path: /processing-worklist
    scopes:
    - result.capture
    generatable: true
  - id: getLaboratoryResult
    method: GET
    path: /{resultId}
    scopes:
    - result.read
    generatable: true
  - id: captureResultValue
    method: POST
    path: /
    scopes:
    - result.capture
    generatable: false
    custom_reason: Multi-source snapshot capture and device-message boundary enforcement.
  - id: recordProcessingIncident
    method: POST
    path: /{resultId}/incidents
    scopes:
    - result.capture
    generatable: true
  - id: submitResultForValidation
    method: POST
    path: /{resultId}/submit
    scopes:
    - result.capture
    generatable: false
    custom_reason: Incident-reliability judgment before allowing submission.
schemas_source:
- LaboratoryResult
- AnalyteSnapshot
- ReferenceRangeSnapshot
- ResultValue
- ProcessingIncident
error_model:
  standard: rfc7807
  domain_errors:
  - code: RESULT_SAMPLE_NOT_RECEIVED
    maps_to_rule: RN-001
  - code: RESULT_SNAPSHOT_INCOMPLETE
    maps_to_rule: RN-002
  - code: RESULT_VALUE_IMPLAUSIBLE
    maps_to_rule: RN-003
  - code: RESULT_RAW_DEVICE_PROTOCOL_REJECTED
    maps_to_rule: RN-004
  - code: RESULT_UNRESOLVED_INCIDENT
    maps_to_rule: RN-005
  - code: RESULT_BOUNDARY_VIOLATION
    maps_to_rule: RN-006
  - code: RESULT_SCOPE_MISMATCH
    maps_to_rule: RN-007
```
