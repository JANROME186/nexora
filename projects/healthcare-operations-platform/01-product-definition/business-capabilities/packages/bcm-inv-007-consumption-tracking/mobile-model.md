---
id: HOP-MOB-BCM-INV-007
format: markdown_structured_payload
type: mobile-model
name: Consumption Tracking Mobile Model
version: 0.1.0
status: deferred
---

# Consumption Tracking Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-INV-007
  type: mobile-model
  name: Consumption Tracking Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-INV-007
mobile_scope:
  status: optional_for_stock_actions
  rationale: 'COM-MOD-010''s module-level product_surfaces classifies mobile_app as
    optional_for_stock_actions; capability-dependency-map.md''s inventory_quality
    profile declares affected_mobile_apps: [staff_app] for bench-level manual consumption
    capture. No staff_app mobile module is yet scheduled; deferred to a future staff/operations
    mobile backlog item.

    '
  deferred_to: []
  flows:
  - Manual bench consumption capture when automatic linkage is unavailable (future).
  offline_expectations: not_yet_defined
```
