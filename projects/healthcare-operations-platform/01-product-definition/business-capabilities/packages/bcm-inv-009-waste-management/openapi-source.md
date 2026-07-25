---
id: HOP-API-SRC-BCM-INV-009
format: markdown_structured_payload
type: openapi-source
name: Waste Management API Source Model
version: 0.1.0
status: modeled
---

# Waste Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-INV-009
  type: openapi-source
  name: Waste Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
api:
  base_path: /api/inventory/waste
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - inventory.waste.manage
    - inventory.waste.read
resources:
- name: WasteRecord
  operations:
  - id: applyWasteDisposal
    method: POST
    path: /
    scopes:
    - inventory.waste.manage
    generatable: false
    custom_reason: Real-time quantity guard and conditional cross-entity status transition.
  - id: listWasteRecords
    method: GET
    path: /
    scopes:
    - inventory.waste.read
    generatable: true
schemas_source:
- WasteRecord
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: WASTE_QUANTITY_EXCEEDS_LOT
    maps_to_rule: RN-001
  - code: WASTE_REASON_CODE_REQUIRED
    maps_to_rule: RN-002
  - code: WASTE_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
