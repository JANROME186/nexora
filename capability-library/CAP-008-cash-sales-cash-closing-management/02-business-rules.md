# Business Rules

| ID | Rule | Priority |
|---|---|---:|
| BR-CASH-001 | A sale must be linked to an organization, branch, patient and diagnostic order. | High |
| BR-CASH-002 | A payment cannot be registered for an order that is cancelled or clinically closed, unless it is an authorized adjustment. | High |
| BR-CASH-003 | A cashier must have an active cash drawer session before receiving cash payments. | High |
| BR-CASH-004 | Discounts require authorization when they exceed the user's configured threshold. | High |
| BR-CASH-005 | A cancellation must store reason, authorizer, timestamp and affected items. | High |
| BR-CASH-006 | Refunds must be linked to an original payment and cannot exceed the paid amount. | High |
| BR-CASH-007 | Cash closing must compare expected amount vs counted amount by payment method. | High |
| BR-CASH-008 | Cash differences must generate an audit event and require supervisor acknowledgment. | High |
| BR-CASH-009 | Once a cash closing is approved, related financial movements become read-only. | High |
| BR-CASH-010 | Every financial operation must be auditable by branch, user, order and patient. | High |
| BR-CASH-011 | Payment methods must be configurable by organization and branch. | Medium |
| BR-CASH-012 | Financial reports must respect tenant, branch and permission boundaries. | High |
