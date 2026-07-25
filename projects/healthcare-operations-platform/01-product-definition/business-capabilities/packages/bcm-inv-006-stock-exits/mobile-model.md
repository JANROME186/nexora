---
id: HOP-MOB-BCM-INV-006
format: markdown_structured_payload
type: mobile-model
name: Stock Exits Mobile Model
version: 0.1.0
status: deferred
---

# Stock Exits Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-INV-006
  type: mobile-model
  name: Stock Exits Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-INV-006
mobile_scope:
  status: optional_for_stock_actions
  rationale: 'COM-MOD-010''s module-level product_surfaces classifies mobile_app as
    optional_for_stock_actions; capability-dependency-map.md''s inventory_quality
    profile declares affected_mobile_apps: [staff_app]. No staff_app mobile module
    is yet scheduled; deferred to a future staff/operations mobile backlog item.

    '
  deferred_to: []
  flows:
  - Scan-to-transfer between branches (future).
  offline_expectations: not_yet_defined
```
