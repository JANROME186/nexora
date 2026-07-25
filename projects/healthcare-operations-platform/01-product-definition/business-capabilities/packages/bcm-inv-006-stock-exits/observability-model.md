---
id: HOP-OBS-BCM-INV-006
format: markdown_structured_payload
type: observability-model
name: Stock Exits Observability Model
version: 0.1.0
status: modeled
---

# Stock Exits Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-006
  type: observability-model
  name: Stock Exits Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-006
  depends_on_capability: BCM-PLT-006
logs:
- event: stock_exited
  level: info
  fields:
  - stockExitId
  - inventoryItemId
  - stockLotId
  - quantity
  - exitReason
  correlation_id: stockExitId
metrics:
- name: stock_exits_recorded_total
  type: counter
  labels:
  - tenantId
  - branchId
  - exitReason
traces:
- span: ApplyStockExit
  child_spans:
  - ValidateStockLotEligibilityAndQuantity
audit_events:
- StockExited
alerts: []
```
