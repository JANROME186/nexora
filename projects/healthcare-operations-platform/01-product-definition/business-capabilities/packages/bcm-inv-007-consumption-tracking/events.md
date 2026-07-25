---
id: HOP-EVT-BCM-INV-007
format: markdown_structured_payload
type: events
name: Consumption Tracking Events
version: 0.1.0
status: modeled
---

# Consumption Tracking Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-007
  type: events
  name: Consumption Tracking Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-007
domain_events:
- name: ConsumptionRecorded
  description: A test-performance-driven consumption decreased on-hand quantity for
    an InventoryItem/StockLot.
  payload:
  - consumptionId
  - inventoryItemId
  - stockLotId
  - quantity
  - linkedLaboratoryResultId
  audit: true
integration_events:
  published:
  - name: ConsumptionRecorded
    description: Signals BCM-QLT-001 of the control-material lot consumed for an internal
      quality control run.
    consumers:
    - inventory-procurement
  consumed:
  - name: LaboratoryResultCaptured
    description: Read-only signal from laboratory-results indicating a linked test
      was performed (published_language consumption only, no LaboratoryResult mutation).
    source: laboratory-results
published_language: []
```
