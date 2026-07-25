---
id: HOP-PERM-BCM-INV-004
format: markdown_structured_payload
type: permissions
name: Procurement Management Permissions
version: 0.1.0
status: modeled
---

# Procurement Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-INV-004
  type: permissions
  name: Procurement Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-004
  depends_on_capability: BCM-PLT-001
scopes:
- code: inventory.procurement.manage
  description: Create, submit, approve, cancel and receive PurchaseOrder records.
- code: inventory.procurement.read
  description: Read purchase order data.
roles:
- role: tenant-administrator
  grants:
  - inventory.procurement.manage
  - inventory.procurement.read
- role: branch-administrator
  grants:
  - inventory.procurement.manage
  - inventory.procurement.read
access_policies:
- id: POL-PUR-004-01
  statement: Procurement commands are scoped to the calling actor's tenant, laboratory
    and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-PUR-004-02
  statement: This capability never writes InventoryItem or Supplier persistence directly.
  enforcement: adapter_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: PurchaseOrderApproved
    fields:
    - purchaseOrderId
  - event: PurchaseOrderCancelled
    fields:
    - purchaseOrderId
  - event: PurchaseOrderLineReceived
    fields:
    - purchaseOrderId
    - lineId
    - receivedQuantity
```
