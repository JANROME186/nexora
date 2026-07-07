# User Stories

## Supplier Management

### INV-US-001 Register supplier

As an organization administrator, I want to register suppliers so that the laboratory can purchase approved supplies.

Acceptance criteria:
- Supplier must belong to an organization.
- Supplier tax/commercial data can be captured when required by country pack.
- Supplier is created in Draft or Active according to configuration.

### INV-US-002 Suspend supplier

As a purchasing manager, I want to suspend suppliers so that purchase orders cannot be issued to non-compliant vendors.

Acceptance criteria:
- Suspended suppliers cannot receive new purchase orders.
- Existing purchase orders remain traceable.
- Suspension reason is mandatory.

## Inventory Item Management

### INV-US-003 Create inventory item

As an inventory manager, I want to create inventory items so that reagents and consumables can be controlled.

Acceptance criteria:
- Item requires name, category, unit of measure and organization.
- Item may be configured as lot-controlled.
- Item may be configured with expiration tracking.

### INV-US-004 Configure minimum stock

As a branch administrator, I want to configure minimum stock levels per branch so that the system can alert shortages.

Acceptance criteria:
- Minimum and safety stock can be configured by warehouse.
- Reorder alerts are generated when thresholds are reached.

## Procurement

### INV-US-005 Create purchase request

As a warehouse operator, I want to create purchase requests when supplies are needed.

Acceptance criteria:
- Request includes items, quantities, branch and expected date.
- Approval is determined by decision table.

### INV-US-006 Receive goods

As a warehouse operator, I want to receive goods against a purchase order so that stock is updated.

Acceptance criteria:
- Received quantity updates stock only after posting.
- Lot and expiration are mandatory for lot-controlled items.
- Variance beyond tolerance requires authorization.

## Stock Operations

### INV-US-007 Consume stock by test/order

As a laboratory technician, I want to consume supplies linked to diagnostic work so that costs and traceability are maintained.

Acceptance criteria:
- Expired or recalled lots are blocked.
- Consumption creates immutable stock movement.
- Optional link to order/sample/test is stored.

### INV-US-008 Adjust stock

As an inventory manager, I want to adjust stock with authorization so that physical and system balances remain aligned.

Acceptance criteria:
- Adjustment reason is mandatory.
- Adjustment generates immutable movement.
- Restricted items require elevated permission.

## Audit

### INV-US-009 Perform inventory audit

As an auditor, I want to perform inventory counts so that variances can be detected and resolved.

Acceptance criteria:
- Audit can be performed by warehouse and category.
- Variances are recorded separately.
- Closing audit requires approval when variance exceeds threshold.
