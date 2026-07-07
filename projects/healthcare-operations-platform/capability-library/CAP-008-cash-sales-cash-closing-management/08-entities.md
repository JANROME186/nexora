# Entities

| Entity | Type | Description |
|---|---|---|
| Sale | Aggregate Root | Financial sale linked to diagnostic order. |
| SaleItem | Entity | Billable study/service item. |
| Payment | Aggregate Root | Payment transaction. |
| PaymentAllocation | Entity | Allocation of a payment to sale items/order balance. |
| Discount | Entity | Applied discount or promotion. |
| Refund | Aggregate Root | Refund operation linked to original payment. |
| CashDrawerSession | Aggregate Root | Cashier working session for cash operations. |
| CashMovement | Entity | Manual cash in/out movement. |
| CashClosing | Aggregate Root | Closing statement for a session/branch/day. |
| CashDifference | Entity | Expected vs counted discrepancy. |
| Receipt | Entity | Non-fiscal receipt. |
| FinancialAuditEntry | Entity | Immutable audit trail. |
