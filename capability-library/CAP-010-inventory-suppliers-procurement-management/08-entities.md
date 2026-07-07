# Entities

| Entity | Type | Description |
|---|---|---|
| Supplier | Master Data | Supplier/vendor record. |
| SupplierContact | Master Data | Supplier contact details. |
| InventoryItem | Master Data | Stockable item. |
| InventoryCategory | Reference Data | Category for supplies and reagents. |
| Warehouse | Master Data | Inventory location. |
| WarehouseBin | Master Data | Optional warehouse subdivision. |
| StockBalance | Transactional Snapshot | Current balance by item/warehouse/lot. |
| StockMovement | Transaction Data | Immutable movement record. |
| Lot | Master/Transaction Data | Lot-level tracking. |
| PurchaseRequest | Transaction Data | Internal request to procure. |
| PurchaseOrder | Transaction Data | Supplier order. |
| PurchaseOrderLine | Transaction Data | Item lines in purchase order. |
| GoodsReceipt | Transaction Data | Receipt against purchase order. |
| GoodsReceiptLine | Transaction Data | Received item and lot details. |
| InventoryAdjustment | Transaction Data | Adjustment header. |
| InventoryAudit | Transaction Data | Physical inventory event. |
| InventoryVariance | Transaction Data | Difference detected in audit. |
| ReorderAlert | Operational Event | Alert for low stock. |
| ConsumptionLink | Transaction Data | Link between consumed supply and order/test/result. |
