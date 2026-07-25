---
id: CAP-008
format: markdown_structured_payload
name: Cash, Sales & Cash Closing Management
version: 0.30.0
status: Draft
---

# Cash, Sales & Cash Closing Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-008
name: Cash, Sales & Cash Closing Management
status: Draft
version: 0.30.0
owner: Finance Operations
dependsOn:
- CAP-001
- CAP-002
- CAP-003
- CAP-005
- CAP-006
- CAP-007
businessRules:
- BR-CASH-001
- BR-CASH-002
- BR-CASH-003
- BR-CASH-004
- BR-CASH-005
- BR-CASH-006
- BR-CASH-007
- BR-CASH-008
- BR-CASH-009
- BR-CASH-010
- BR-CASH-011
- BR-CASH-012
entities:
- Sale
- SaleItem
- Payment
- PaymentAllocation
- Discount
- Refund
- CashDrawerSession
- CashMovement
- CashClosing
- CashDifference
- Receipt
- FinancialAuditEntry
events:
- SaleCreated
- PaymentRegistered
- SalePaid
- DiscountApprovalRequested
- SaleCancelled
- RefundProcessed
- CashDrawerOpened
- CashMovementRegistered
- CashClosingRequested
- CashDifferenceDetected
- CashClosingApproved
apis:
- 05-contracts/contracts/openapi/cash/cash-api.md
ui:
- Sales Workbench
- Payment Capture
- Cash Closing Wizard
- Refund Management
mobile:
- Supervisor Approval
- Daily Sales Snapshot
aiUseCases:
- AI-CASH-001
- AI-CASH-002
- AI-CASH-003
- AI-CASH-004
```
