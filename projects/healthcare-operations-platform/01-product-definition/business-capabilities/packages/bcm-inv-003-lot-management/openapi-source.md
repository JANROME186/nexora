---
id: HOP-API-SRC-BCM-INV-003
format: markdown_structured_payload
type: openapi-source
name: Lot Management API Source Model
version: 0.1.0
status: modeled
---

# Lot Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-INV-003
  type: openapi-source
  name: Lot Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-003
api:
  base_path: /api/inventory/lots
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - inventory.lot.manage
    - inventory.lot.read
resources:
- name: StockLot
  operations:
  - id: registerStockLot
    method: POST
    path: /items/{inventoryItemId}/lots
    scopes:
    - inventory.lot.manage
    generatable: true
  - id: listStockLots
    method: GET
    path: /items/{inventoryItemId}/lots
    scopes:
    - inventory.lot.read
    generatable: true
  - id: quarantineStockLot
    method: POST
    path: /lots/{stockLotId}/quarantine
    scopes:
    - inventory.lot.manage
    generatable: true
  - id: expireStockLot
    method: POST
    path: /lots/{stockLotId}/expire
    scopes:
    - inventory.lot.manage
    generatable: false
    custom_reason: Driven by a scheduled expiration sweep, not only a manual request.
schemas_source:
- StockLot
- SupplierSnapshot
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: LOT_QUANTITY_INVARIANT_VIOLATION
    maps_to_rule: RN-001
  - code: LOT_DISPOSED_TRANSITION_FORBIDDEN
    maps_to_rule: RN-004
  - code: LOT_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
