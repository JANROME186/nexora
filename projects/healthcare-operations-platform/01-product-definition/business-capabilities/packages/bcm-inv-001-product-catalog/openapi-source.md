---
id: HOP-API-SRC-BCM-INV-001
format: markdown_structured_payload
type: openapi-source
name: Product Catalog API Source Model
version: 0.1.0
status: modeled
---

# Product Catalog Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-INV-001
  type: openapi-source
  name: Product Catalog API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-001
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/inventory/catalog
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - inventory.catalog.manage
    - inventory.catalog.read
resources:
- name: InventoryItem
  operations:
  - id: createInventoryItem
    method: POST
    path: /items
    scopes:
    - inventory.catalog.manage
    generatable: true
  - id: listInventoryItems
    method: GET
    path: /items
    scopes:
    - inventory.catalog.read
    generatable: true
  - id: getInventoryItem
    method: GET
    path: /items/{inventoryItemId}
    scopes:
    - inventory.catalog.read
    generatable: true
  - id: updateInventoryItem
    method: PUT
    path: /items/{inventoryItemId}
    scopes:
    - inventory.catalog.manage
    generatable: true
  - id: discontinueInventoryItem
    method: POST
    path: /items/{inventoryItemId}/discontinue
    scopes:
    - inventory.catalog.manage
    generatable: false
    custom_reason: Must consult open stock/lot/purchase-order state across delegated
      capabilities.
schemas_source:
- InventoryItem
- StockSummary
- ReagentProfile
- EquipmentProfile
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class, independently parseable
    `code` string field, following the convention established by BCM-PLT-004/BCM-PLT-005
    (MVP-MOD-008) and continued consistently across all COM-MOD-010 capabilities.

    '
  domain_errors:
  - code: INVENTORY_ITEM_CODE_NOT_UNIQUE
    maps_to_rule: RN-001
  - code: INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH
    maps_to_rule: RN-002
  - code: INVENTORY_ITEM_DISCONTINUED
    maps_to_rule: RN-004
  - code: INVENTORY_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
