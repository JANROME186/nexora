# Event Storming

## Domain Events

- SupplierRegistered
- SupplierActivated
- SupplierSuspended
- InventoryItemCreated
- InventoryItemActivated
- WarehouseCreated
- StockReceived
- StockMovementPosted
- StockAdjusted
- LotReceived
- LotExpired
- LotQuarantined
- PurchaseRequestCreated
- PurchaseRequestApproved
- PurchaseOrderCreated
- PurchaseOrderSent
- PurchaseOrderPartiallyReceived
- PurchaseOrderReceived
- StockConsumed
- ReorderAlertCreated
- InventoryAuditStarted
- InventoryVarianceDetected
- InventoryAuditClosed

## Command Flow

CreatePurchaseRequest → ApprovePurchaseRequest → CreatePurchaseOrder → ReceiveGoods → PostStockReceipt → UpdateStockBalance
