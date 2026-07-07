# Event Storming

## Domain Events

- SaleCreated
- SaleItemsCalculated
- DiscountApplied
- DiscountApprovalRequested
- DiscountApproved
- PaymentRegistered
- SalePartiallyPaid
- SalePaid
- ReceiptGenerated
- SaleCancellationRequested
- SaleCancelled
- RefundRequested
- RefundApproved
- RefundProcessed
- CashDrawerOpened
- CashMovementRegistered
- CashClosingRequested
- CashDifferenceDetected
- CashClosingApproved
- CashClosingRejected

## External Events Consumed

- OrderCreated
- OrderCancelled
- ResultReleased
- UserPermissionChanged
- BranchConfigurationChanged

## Downstream Events Produced

- PaymentCompleted
- FinancialAdjustmentRecorded
- CashClosingCompleted
- InvoiceEligibilityChanged
