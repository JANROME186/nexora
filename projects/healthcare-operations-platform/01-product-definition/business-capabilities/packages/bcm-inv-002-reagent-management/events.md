---
id: HOP-EVT-BCM-INV-002
format: markdown_structured_payload
type: events
name: Reagent Management Events
version: 0.1.0
status: modeled
---

# Reagent Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-002
  type: events
  name: Reagent Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-002
domain_events:
- name: ReagentProfileAssigned
  description: An InventoryItem was classified as a reagent/consumable with a test-consumption
    ratio.
  payload:
  - inventoryItemId
  - reagentCategory
  - linkedTestDefinitionId
  audit: true
integration_events:
  published:
  - name: ReagentProfileAssigned
    description: Signals BCM-INV-007 that a consumption ratio is available for automatic
      stock decrement.
    consumers:
    - inventory-procurement
  consumed: []
published_language: []
```
