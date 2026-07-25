---
id: HOP-PERM-BCM-INV-006
format: markdown_structured_payload
type: permissions
name: Stock Exits Permissions
version: 0.1.0
status: modeled
---

# Stock Exits Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-INV-006
  type: permissions
  name: Stock Exits Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-006
  depends_on_capability: BCM-PLT-001
scopes:
- code: inventory.exits.manage
  description: Record a stock exit (ApplyStockExit).
- code: inventory.exits.read
  description: Read stock exit history.
roles:
- role: tenant-administrator
  grants:
  - inventory.exits.manage
  - inventory.exits.read
- role: branch-administrator
  grants:
  - inventory.exits.manage
  - inventory.exits.read
- role: laboratory-technician
  grants:
  - inventory.exits.manage
  - inventory.exits.read
access_policies:
- id: POL-SXT-006-01
  statement: Stock exit commands are scoped to the calling actor's tenant, laboratory
    and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-SXT-006-02
  statement: stockSummary.onHandQuantity may only be decreased for exit purposes through
    ApplyStockExit.
  enforcement: field_level_delegation_boundary
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: StockExited
    fields:
    - stockExitId
    - inventoryItemId
    - stockLotId
    - quantity
    - exitReason
```
