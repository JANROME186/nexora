---
id: HOP-EVT-BCM-INV-008
format: markdown_structured_payload
type: events
name: Inventory Adjustments Events
version: 0.1.0
status: modeled
---

# Inventory Adjustments Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-008
  type: events
  name: Inventory Adjustments Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
domain_events:
- name: StockAdjusted
  description: A manual correction changed on-hand quantity for an InventoryItem/StockLot.
  payload:
  - adjustmentId
  - inventoryItemId
  - stockLotId
  - quantityDelta
  - reasonCode
  audit: true
integration_events:
  published: []
  consumed: []
published_language: []
```
