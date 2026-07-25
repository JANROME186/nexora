---
id: HOP-API-SRC-BCM-INV-008
format: markdown_structured_payload
type: openapi-source
name: Inventory Adjustments API Source Model
version: 0.1.0
status: modeled
---

# Inventory Adjustments Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-INV-008
  type: openapi-source
  name: Inventory Adjustments API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
api:
  base_path: /api/inventory/adjustments
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - inventory.adjustment.manage
    - inventory.adjustment.read
resources:
- name: AdjustmentRecord
  operations:
  - id: applyAdjustment
    method: POST
    path: /
    scopes:
    - inventory.adjustment.manage
    generatable: false
    custom_reason: Real-time negative-quantity guard and mandatory dual-actor approval.
  - id: listAdjustments
    method: GET
    path: /
    scopes:
    - inventory.adjustment.read
    generatable: true
schemas_source:
- AdjustmentRecord
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: ADJUSTMENT_QUANTITY_INVALID
    maps_to_rule: RN-001
  - code: ADJUSTMENT_APPROVER_SAME_AS_REQUESTER
    maps_to_rule: RN-002
  - code: ADJUSTMENT_REASON_CODE_REQUIRED
    maps_to_rule: RN-003
  - code: ADJUSTMENT_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
