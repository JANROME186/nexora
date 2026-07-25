---
id: HOP-PERM-BCM-ATT-005
format: markdown_structured_payload
type: permissions
name: Cashier Operations Permissions
version: 0.1.0
status: modeled
---

# Cashier Operations Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-ATT-005
  type: permissions
  name: Cashier Operations Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-005
  depends_on_capability: BCM-PLT-001
scopes:
- cashier.read
- cashier.manage
- sale.read
- sale.manage
- sale.cancel
- payment.manage
- billing.request
roles:
- role: cashier
  grants:
  - cashier.read
  - cashier.manage
  - sale.read
  - sale.manage
  - payment.manage
  - billing.request
- role: branch_manager
  grants:
  - cashier.read
  - sale.read
  - sale.cancel
- role: auditor
  grants:
  - cashier.read
  - sale.read
access_policies:
- id: POL-CASH-001
  statement: Cashiers may manage only sessions and sales within their assigned tenant,
    laboratory and branch.
- id: POL-CASH-002
  statement: Sale cancellation after payment requires branch manager approval.
- id: POL-CASH-003
  statement: Every cash movement and payment registration is audit-required.
audit:
  required_events:
  - CashSessionOpened
  - SaleCreated
  - PaymentRegistered
  - SaleCancelled
  - CashSessionClosed
```
