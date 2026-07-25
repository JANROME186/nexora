---
id: HOP-API-SRC-BCM-QLT-003
format: markdown_structured_payload
type: openapi-source
name: Calibration Management API Source Model
version: 0.1.0
status: modeled
---

# Calibration Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-QLT-003
  type: openapi-source
  name: Calibration Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
api:
  base_path: /api/quality/calibrations
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - quality.calibration.manage
    - quality.calibration.read
resources:
- name: CalibrationEvent
  operations:
  - id: recordCalibration
    method: POST
    path: /items/{inventoryItemId}/calibrations
    scopes:
    - quality.calibration.manage
    generatable: false
    custom_reason: Delegated append mutation with cross-capability itemType validation
      and conditional event publication.
  - id: listCalibrations
    method: GET
    path: /items/{inventoryItemId}/calibrations
    scopes:
    - quality.calibration.read
    generatable: true
schemas_source:
- CalibrationEvent
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: CALIBRATION_ITEM_TYPE_NOT_ELIGIBLE
    maps_to_rule: RN-001
  - code: CALIBRATION_NEXT_DUE_DATE_INVALID
    maps_to_rule: RN-004
  - code: CALIBRATION_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
