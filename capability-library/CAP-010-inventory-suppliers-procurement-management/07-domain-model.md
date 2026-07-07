# Domain Model

## Aggregates

### Supplier Aggregate

Controls supplier identity, commercial status, contact information, compliance status and purchasing eligibility.

### Inventory Item Aggregate

Defines a supply, reagent, consumable, container, imaging material or internal product that can be stocked, consumed or purchased.

### Warehouse Aggregate

Represents a physical or logical stock location assigned to an organization or branch.

### Stock Movement Aggregate

Immutable record of inventory change. Movement types include receipt, consumption, adjustment, transfer, reservation, quarantine and disposal.

### Purchase Order Aggregate

Controls procurement lifecycle from draft to approval, supplier submission, receipt and closure.

### Inventory Audit Aggregate

Controls stock count, variance detection, approval and reconciliation.

## Value Objects

- Money
- Quantity
- UnitOfMeasure
- LotNumber
- ExpirationDate
- StockThreshold
- SupplierContact
- PurchaseOrderNumber
- MovementReason
- WarehouseLocation

## Domain Services

- StockAvailabilityService
- ReorderPolicyService
- LotValidationService
- PurchaseApprovalPolicyService
- InventoryCostingService
