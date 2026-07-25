---
id: HOP-PERM-BCM-INV-002
format: markdown_structured_payload
type: permissions
name: Reagent Management Permissions
version: 0.1.0
status: modeled
---

# Reagent Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-INV-002
  type: permissions
  name: Reagent Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-002
  depends_on_capability: BCM-PLT-001
scopes:
- code: inventory.reagent.manage
  description: Assign or update a reagent profile on an eligible InventoryItem.
- code: inventory.reagent.read
  description: Read reagent profile data.
roles:
- role: tenant-administrator
  grants:
  - inventory.reagent.manage
  - inventory.reagent.read
- role: catalog-manager
  grants:
  - inventory.reagent.manage
  - inventory.reagent.read
- role: laboratory-technician
  grants:
  - inventory.reagent.read
access_policies:
- id: POL-RGT-002-01
  statement: Reagent management commands are scoped to the calling actor's tenant,
    laboratory and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-RGT-002-02
  statement: reagentProfile may only be written through AssignReagentProfile; no direct
    persistence access.
  enforcement: field_level_delegation_boundary
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ReagentProfileAssigned
    fields:
    - inventoryItemId
    - reagentCategory
    - linkedTestDefinitionId
```
