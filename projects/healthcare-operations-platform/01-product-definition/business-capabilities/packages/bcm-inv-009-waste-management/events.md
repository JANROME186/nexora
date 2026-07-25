---
id: HOP-EVT-BCM-INV-009
format: markdown_structured_payload
type: events
name: Waste Management Events
version: 0.1.0
status: modeled
---

# Waste Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-009
  type: events
  name: Waste Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
domain_events:
- name: WasteRecorded
  description: A disposal decreased on-hand quantity for an InventoryItem/StockLot.
  payload:
  - wasteRecordId
  - inventoryItemId
  - stockLotId
  - quantity
  - wasteReasonCode
  audit: true
integration_events:
  published:
  - name: WasteRecorded
    description: Available for future waste-trend audit reporting (BCM-QLT-007, not
      part of COM-MOD-010).
    consumers:
    - inventory-procurement
  consumed: []
published_language: []
```
