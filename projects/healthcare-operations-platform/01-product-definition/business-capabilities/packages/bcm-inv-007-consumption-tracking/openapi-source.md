---
id: HOP-API-SRC-BCM-INV-007
format: markdown_structured_payload
type: openapi-source
name: Consumption Tracking API Source Model
version: 0.1.0
status: modeled
---

# Consumption Tracking Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-INV-007
  type: openapi-source
  name: Consumption Tracking API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-007
api:
  base_path: /api/inventory/consumption
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - inventory.consumption.manage
    - inventory.consumption.read
resources:
- name: ConsumptionRecord
  operations:
  - id: applyConsumption
    method: POST
    path: /
    scopes:
    - inventory.consumption.manage
    generatable: false
    custom_reason: Cross-capability ratio resolution and real-time lot-eligibility
      guard.
  - id: listConsumptionRecords
    method: GET
    path: /
    scopes:
    - inventory.consumption.read
    generatable: true
schemas_source:
- ConsumptionRecord
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: CONSUMPTION_REAGENT_PROFILE_MISSING
    maps_to_rule: RN-001
  - code: CONSUMPTION_LOT_NOT_ELIGIBLE
    maps_to_rule: RN-004
  - code: CONSUMPTION_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
