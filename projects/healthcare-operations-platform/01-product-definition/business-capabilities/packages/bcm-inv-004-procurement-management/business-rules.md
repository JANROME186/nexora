---
id: HOP-BR-BCM-INV-004
format: markdown_structured_payload
type: business-rules
name: Procurement Management Business Rules
version: 0.1.0
status: modeled
---

# Procurement Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-004
  type: business-rules
  name: Procurement Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-004
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A PurchaseOrder line's inventoryItemId must reference an existing, non-discontinued
    InventoryItem at submission time.
  applies_to: PurchaseOrderLine
  enforcement_point: command:SubmitPurchaseOrder
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires a cross-capability read of InventoryItem.status owned by
    BCM-INV-001.
  test_refs:
  - TST-PUR-004-01
- id: RN-002
  statement: This capability never mutates InventoryItem or Supplier persistence directly;
    goods receipt is delegated entirely to BCM-INV-005's own command.
  applies_to: PurchaseOrder
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement.
  test_refs:
  - TST-PUR-004-02
- id: RN-003
  statement: A cancelled or fully received PurchaseOrder is terminal and cannot accept
    new lines or status transitions.
  applies_to: PurchaseOrder
  enforcement_point: command:ApprovePurchaseOrder, command:CancelPurchaseOrder
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PUR-004-03
- id: RN-004
  statement: requestedQuantity and unitCost must be strictly greater than zero for
    every line.
  applies_to: PurchaseOrderLine
  enforcement_point: command:CreatePurchaseOrder
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PUR-004-04
- id: RN-005
  statement: Procurement commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: PurchaseOrder
  enforcement_point: authorization:inventory.procurement.manage, authorization:inventory.procurement.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-PUR-004-05
enforcement_summary:
  generatable_rules:
  - RN-003
  - RN-004
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-002
```
