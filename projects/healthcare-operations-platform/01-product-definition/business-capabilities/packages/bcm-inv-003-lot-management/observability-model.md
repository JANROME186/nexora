---
id: HOP-OBS-BCM-INV-003
format: markdown_structured_payload
type: observability-model
name: Lot Management Observability Model
version: 0.1.0
status: modeled
---

# Lot Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-003
  type: observability-model
  name: Lot Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-003
  depends_on_capability: BCM-PLT-006
logs:
- event: stock_lot_registered
  level: info
  fields:
  - stockLotId
  - inventoryItemId
  - lotNumber
  correlation_id: stockLotId
- event: stock_lot_expired
  level: warn
  fields:
  - stockLotId
  - inventoryItemId
  - expirationDate
  correlation_id: stockLotId
metrics:
- name: stock_lots_registered_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: stock_lots_nearing_expiration_gauge
  type: gauge
  labels:
  - tenantId
  - branchId
traces:
- span: RegisterStockLot
  child_spans: []
- span: ExpireStockLot
  child_spans:
  - ScheduledExpirationSweep
audit_events:
- StockLotQuarantined
- StockLotExpired
alerts:
- name: StockLotNearingExpiration
  condition: stock_lots_nearing_expiration_gauge exceeds configured lead-time threshold
  severity: medium
- name: StockLotExpiredUnaddressed
  condition: StockLotExpired without a subsequent exit or disposal event within a
    configured window
  severity: high
```
