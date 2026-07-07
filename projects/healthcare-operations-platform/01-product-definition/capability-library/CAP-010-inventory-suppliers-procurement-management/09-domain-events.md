# Domain Events

| Event ID | Event | Payload |
|---|---|---|
| INV-EVT-001 | SupplierRegistered | supplierId, organizationId |
| INV-EVT-002 | InventoryItemCreated | itemId, organizationId |
| INV-EVT-003 | WarehouseCreated | warehouseId, branchId |
| INV-EVT-004 | PurchaseRequestCreated | requestId, branchId, totalAmount |
| INV-EVT-005 | PurchaseRequestApproved | requestId, approvedBy |
| INV-EVT-006 | PurchaseOrderCreated | purchaseOrderId, supplierId |
| INV-EVT-007 | StockReceived | receiptId, warehouseId |
| INV-EVT-008 | StockMovementPosted | movementId, movementType, itemId, quantity |
| INV-EVT-009 | StockConsumed | movementId, orderId, itemId, lotId |
| INV-EVT-010 | ReorderAlertCreated | alertId, itemId, branchId |
| INV-EVT-011 | LotExpired | lotId, itemId |
| INV-EVT-012 | InventoryVarianceDetected | auditId, itemId, expected, counted |
