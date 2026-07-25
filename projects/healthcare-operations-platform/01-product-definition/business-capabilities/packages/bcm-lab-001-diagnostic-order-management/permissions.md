---
id: HOP-PERM-BCM-LAB-001
format: markdown_structured_payload
type: permissions
name: Diagnostic Order Management Permissions
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-LAB-001
  type: permissions
  name: Diagnostic Order Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
  depends_on_capability: BCM-PLT-001
scopes:
- code: order.create
  description: Create a diagnostic order.
- code: order.manage
  description: Price, accept, cancel or complete a diagnostic order.
- code: order.read
  description: Read diagnostic order history for audit, review or downstream workflows.
roles:
- role: receptionist
  grants:
  - order.create
  - order.manage
  - order.read
- role: branch-administrator
  grants:
  - order.create
  - order.manage
  - order.read
- role: tenant-administrator
  grants:
  - order.read
- role: laboratory-technician
  grants:
  - order.read
- role: cashier
  grants:
  - order.read
access_policies:
- id: POL-ORD-001-01
  statement: Order commands are scoped to the actor's tenant, laboratory and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-ORD-001-02
  statement: Only this capability's commands may mutate DiagnosticOrder state; cross-capability
    access is command-only, never direct persistence.
  enforcement: cross_capability_delegation_policy
- id: POL-ORD-001-03
  statement: PatientSnapshot and DoctorSnapshot fields exposed through the API must
    apply the same masking policy as their source capability read models.
  enforcement: cross_context_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: DiagnosticOrderCreated
    fields:
    - orderId
    - actorId
    - branchId
    - intakeChannel
  - event: OrderPriced
    fields:
    - orderId
    - priceListId
    - totalAmount
    - actorId
  - event: OrderAccepted
    fields:
    - orderId
    - actorId
    - branchId
  - event: OrderCancelled
    fields:
    - orderId
    - actorId
    - reasonCode
  - event: OrderCompleted
    fields:
    - orderId
    - actorId
```
