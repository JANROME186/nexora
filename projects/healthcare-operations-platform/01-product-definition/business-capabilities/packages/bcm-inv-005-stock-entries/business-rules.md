---
id: HOP-BR-BCM-INV-005
format: markdown_structured_payload
type: business-rules
name: Stock Entries Business Rules
version: 0.1.0
status: modeled
---

# Stock Entries Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-005
  type: business-rules
  name: Stock Entries Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-005
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A stock entry's quantity must be strictly greater than zero.
  applies_to: StockEntryRecord
  enforcement_point: command:ApplyStockReceipt
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-SEN-005-01
- id: RN-002
  statement: A purchase_order_receipt entry must reference a valid line on an approved
    or partially_received PurchaseOrder.
  applies_to: StockEntryRecord
  enforcement_point: command:ApplyStockReceipt
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires a cross-capability read of PurchaseOrder status owned by
    BCM-INV-004.
  test_refs:
  - TST-SEN-005-02
- id: RN-003
  statement: Only this capability may invoke ApplyStockReceipt, the sole path that
    increases InventoryItem.stockSummary and the referenced StockLot.remainingQuantity.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated field-mutation boundary shared across COM-MOD-010.
  test_refs:
  - TST-SEN-005-03
- id: RN-004
  statement: A receipt against a discontinued InventoryItem is rejected.
  applies_to: StockEntryRecord
  enforcement_point: command:ApplyStockReceipt
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires a cross-capability read of InventoryItem.status.
  test_refs:
  - TST-SEN-005-04
- id: RN-005
  statement: Stock entry commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: StockEntryRecord
  enforcement_point: authorization:inventory.entries.manage, authorization:inventory.entries.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-SEN-005-05
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-005
  custom_implementation_rules:
  - RN-002
  - RN-003
  - RN-004
```
