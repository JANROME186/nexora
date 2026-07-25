---
id: HOP-MOB-BCM-INV-001
format: markdown_structured_payload
type: mobile-model
name: Product Catalog Mobile Model
version: 0.1.0
status: deferred
---

# Product Catalog Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-INV-001
  type: mobile-model
  name: Product Catalog Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-INV-001
mobile_scope:
  status: not_required
  rationale: 'Item-master governance is an administrative, low-frequency capability.
    COM-MOD-010''s module product_surfaces classifies mobile_app as optional_for_stock_actions,
    which applies to the higher-frequency stock-movement capabilities (BCM-INV-005/006/007/008/009),
    not to catalog administration.

    '
  deferred_to: []
  flows: []
  offline_expectations: none
```
