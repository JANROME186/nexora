---
id: HOP-OBS-BCM-INV-008
format: markdown_structured_payload
type: observability-model
name: Inventory Adjustments Observability Model
version: 0.1.0
status: modeled
---

# Inventory Adjustments Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-008
  type: observability-model
  name: Inventory Adjustments Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
  depends_on_capability: BCM-PLT-006
logs:
- event: stock_adjusted
  level: warn
  fields:
  - adjustmentId
  - inventoryItemId
  - stockLotId
  - quantityDelta
  - reasonCode
  correlation_id: adjustmentId
metrics:
- name: stock_adjustments_recorded_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reasonCode
traces:
- span: ApplyAdjustment
  child_spans:
  - ValidateNegativeQuantityGuard
  - ValidateApprovalSeparationOfDuties
audit_events:
- StockAdjusted
alerts:
- name: InventoryAdjustmentVolumeSpike
  condition: stock_adjustments_recorded_total rate exceeds threshold
  severity: medium
```
