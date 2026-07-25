---
id: HOP-API-SRC-BCM-SVC-009
format: markdown_structured_payload
type: openapi-source
name: Price List Management API Source Model
version: 0.1.0
status: modeled
---

# Price List Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-SVC-009
  type: openapi-source
  name: Price List Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-009
  note: 'Source contract model. Rendered OpenAPI, controllers, DTOs and SDKs are generated
    outputs declared in generation-plan.md.

    '
api:
  base_path: /api/catalog/price-lists
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - catalog.price.read
resources:
- name: PriceList
  operations:
  - id: listPriceLists
    method: GET
    path: /
    scopes:
    - catalog.price.read
    generatable: true
  - id: getPriceList
    method: GET
    path: /{priceListId}
    scopes:
    - catalog.price.read
    generatable: true
  - id: createPriceList
    method: POST
    path: /
    scopes:
    - catalog.price.write
    generatable: true
  - id: addPriceEntry
    method: POST
    path: /{priceListId}/entries
    scopes:
    - catalog.price.write
    generatable: true
  - id: updatePriceList
    method: PUT
    path: /{priceListId}
    scopes:
    - catalog.price.write
    generatable: false
    custom_reason: Effective-dated versioning and snapshot freeze.
  - id: publishPriceList
    method: POST
    path: /{priceListId}/publish
    scopes:
    - catalog.price.publish
    generatable: false
    custom_reason: Item validation and effective-date overlap detection are custom
      rules.
  - id: deprecatePriceList
    method: POST
    path: /{priceListId}/deprecate
    scopes:
    - catalog.price.publish
    generatable: true
  - id: getEffectivePriceSnapshot
    method: GET
    path: /effective
    query:
    - itemType
    - itemRefId
    - currency
    - agreementRefId
    - saleDate
    scopes:
    - catalog.price.read
    generatable: false
    custom_reason: Effective-date and scope resolution for downstream pricing.
schemas_source:
- PriceList
- PriceEntry
- EffectivePriceSnapshot
error_model:
  standard: rfc7807
  domain_errors:
  - code: CATALOG_PRICE_LIST_CODE_CONFLICT
    maps_to_rule: RN-001
  - code: CATALOG_PRICE_AMOUNT_INVALID
    maps_to_rule: RN-002
  - code: CATALOG_PRICE_ITEM_NOT_PUBLISHED
    maps_to_rule: RN-003
  - code: CATALOG_PRICE_EFFECTIVE_OVERLAP
    maps_to_rule: RN-005
```
