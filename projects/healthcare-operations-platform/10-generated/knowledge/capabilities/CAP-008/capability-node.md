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
kind: BusinessCapability
name: Cash, Sales & Cash Closing Management
version: 0.30.0
status: Draft
relations:
  dependsOn:
  - CAP-001
  - CAP-002
  - CAP-003
  - CAP-005
  - CAP-006
  - CAP-007
  owns:
  - BR-CASH-001
  - API-CASH-001
  - Sale
  - Payment
  - CashDrawerSession
  - CashClosing
  - Refund
  producesEvents:
  - SaleCreated
  - PaymentRegistered
  - CashClosingApproved
sourceFiles:
  capability: capability-library/CAP-008-cash-sales-cash-closing-management/capability.md
  openapi: 05-contracts/contracts/openapi/cash/cash-api.md
```
