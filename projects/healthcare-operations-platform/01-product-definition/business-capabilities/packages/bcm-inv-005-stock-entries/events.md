---
id: HOP-EVT-BCM-INV-005
format: markdown_structured_payload
type: events
name: Stock Entries Events
version: 0.1.0
status: modeled
---

# Stock Entries Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-005
  type: events
  name: Stock Entries Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-005
domain_events:
- name: StockReceived
  description: A stock entry increased on-hand quantity for an InventoryItem/StockLot.
  payload:
  - stockEntryId
  - inventoryItemId
  - stockLotId
  - quantity
  - sourceType
  audit: true
integration_events:
  published:
  - name: StockReceived
    description: Signals BCM-INV-004 that a purchase order line's goods were received.
    consumers:
    - inventory-procurement
  consumed: []
published_language: []
```
