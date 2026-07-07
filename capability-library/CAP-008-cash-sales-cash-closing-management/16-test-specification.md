# Test Specification

## Test Categories

- Unit tests for pricing, discounts, refunds and reconciliation.
- Contract tests for Cash API.
- Authorization tests for cancellations, discounts and closing approval.
- Integration tests with Orders API and Billing API.
- Audit tests for immutable financial events.
- Performance tests for cashier payment flow.

## Critical Scenarios

- Register full payment.
- Register partial payment.
- Apply discount within limit.
- Request discount approval.
- Cancel paid sale.
- Refund partial and full payment.
- Detect cash difference.
- Approve cash closing.
