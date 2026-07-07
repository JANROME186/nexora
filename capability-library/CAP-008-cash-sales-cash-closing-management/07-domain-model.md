# Domain Model

## Aggregate Roots

- Sale
- Payment
- CashDrawerSession
- CashClosing
- Refund

## Value Objects

- Money
- PaymentMethod
- DiscountPolicy
- AuthorizationReference
- CashDifference
- ReceiptNumber

## Domain Services

- SalePricingService
- DiscountAuthorizationService
- PaymentAllocationService
- CashClosingReconciliationService
- RefundPolicyService

## Invariants

- A refund cannot exceed paid amount.
- Cash payment requires open cash drawer session.
- Closed cash drawer movements are immutable.
- Sale total must equal item totals minus discounts plus taxes/fees when applicable.
