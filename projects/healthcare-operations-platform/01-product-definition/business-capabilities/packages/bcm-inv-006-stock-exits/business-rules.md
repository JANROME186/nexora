---
id: HOP-BR-BCM-INV-006
format: markdown_structured_payload
type: business-rules
name: Stock Exits Business Rules
version: 0.1.0
status: modeled
---

# Stock Exits Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-006
  type: business-rules
  name: Stock Exits Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-006
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: An exit's quantity must be strictly greater than zero and must not exceed
    the referenced StockLot's remainingQuantity.
  applies_to: StockExitRecord
  enforcement_point: command:ApplyStockExit
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires reading current StockLot.remainingQuantity before allowing
    the decrement.
  test_refs:
  - TST-SXT-006-01
- id: RN-002
  statement: A branch_transfer or internal_transfer exit must supply a destinationBranchId.
  applies_to: StockExitRecord
  enforcement_point: command:ApplyStockExit
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-SXT-006-02
- id: RN-003
  statement: Only this capability may invoke ApplyStockExit, the sole non-consumption,
    non-waste path that decreases InventoryItem.stockSummary and the referenced StockLot.remainingQuantity.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated field-mutation boundary shared across COM-MOD-010.
  test_refs:
  - TST-SXT-006-03
- id: RN-004
  statement: An exit against an expired or disposed StockLot is rejected; expired
    lots must route through BCM-INV-009 Waste Management instead.
  applies_to: StockExitRecord
  enforcement_point: command:ApplyStockExit
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires reading StockLot.status owned by BCM-INV-003.
  test_refs:
  - TST-SXT-006-04
- id: RN-005
  statement: Stock exit commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: StockExitRecord
  enforcement_point: authorization:inventory.exits.manage, authorization:inventory.exits.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-SXT-006-05
enforcement_summary:
  generatable_rules:
  - RN-002
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-003
  - RN-004
```
