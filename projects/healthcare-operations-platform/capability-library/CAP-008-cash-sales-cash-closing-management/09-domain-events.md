# Domain Events

| Event | Trigger |
|---|---|
| SaleCreated | Order is prepared for payment. |
| PaymentRegistered | A payment is captured. |
| SalePaid | Sale balance reaches zero. |
| DiscountApprovalRequested | Discount exceeds user threshold. |
| SaleCancelled | Authorized cancellation is completed. |
| RefundProcessed | Refund operation is completed. |
| CashDrawerOpened | Cashier opens drawer session. |
| CashMovementRegistered | Cash in/out movement is captured. |
| CashClosingRequested | Cashier submits closing. |
| CashDifferenceDetected | Difference is found during closing. |
| CashClosingApproved | Supervisor approves closing. |
