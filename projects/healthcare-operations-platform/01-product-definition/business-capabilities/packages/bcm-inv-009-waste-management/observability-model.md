---
id: HOP-OBS-BCM-INV-009
format: markdown_structured_payload
type: observability-model
name: Waste Management Observability Model
version: 0.1.0
status: modeled
---

# Waste Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-009
  type: observability-model
  name: Waste Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
  depends_on_capability: BCM-PLT-006
logs:
- event: waste_recorded
  level: warn
  fields:
  - wasteRecordId
  - inventoryItemId
  - stockLotId
  - quantity
  - wasteReasonCode
  correlation_id: wasteRecordId
metrics:
- name: waste_records_total
  type: counter
  labels:
  - tenantId
  - branchId
  - wasteReasonCode
- name: waste_quantity_total
  type: counter
  labels:
  - tenantId
  - branchId
traces:
- span: ApplyWasteDisposal
  child_spans:
  - ValidateStockLotQuantity
  - TransitionStockLotIfExhausted
audit_events:
- WasteRecorded
alerts:
- name: WasteRateHigh
  condition: waste_quantity_total rate exceeds configured threshold relative to consumption
  severity: medium
```
