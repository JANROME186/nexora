---
id: HOP-BR-BCM-INV-008
format: markdown_structured_payload
type: business-rules
name: Inventory Adjustments Business Rules
version: 0.1.0
status: modeled
---

# Inventory Adjustments Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-008
  type: business-rules
  name: Inventory Adjustments Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: quantityDelta must not be zero and must never drive stock negative.
  applies_to: AdjustmentRecord
  enforcement_point: command:ApplyAdjustment
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires reading current StockLot.remainingQuantity before allowing
    a negative delta.
  test_refs:
  - TST-ADJ-008-01
- id: RN-002
  statement: Every adjustment must carry both a requestedBy and a different approvedBy
    actor.
  applies_to: AdjustmentRecord
  enforcement_point: command:ApplyAdjustment
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADJ-008-02
- id: RN-003
  statement: reasonCode is mandatory for every adjustment.
  applies_to: AdjustmentRecord
  enforcement_point: command:ApplyAdjustment
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADJ-008-03
- id: RN-004
  statement: Only this capability may invoke ApplyAdjustment, the sole correction
    path for InventoryItem.stockSummary and the referenced StockLot.remainingQuantity.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated field-mutation boundary shared across COM-MOD-010.
  test_refs:
  - TST-ADJ-008-04
- id: RN-005
  statement: Inventory adjustment commands must execute within the actor's tenant,
    laboratory and branch scope.
  applies_to: AdjustmentRecord
  enforcement_point: authorization:inventory.adjustment.manage, authorization:inventory.adjustment.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-ADJ-008-05
enforcement_summary:
  generatable_rules:
  - RN-002
  - RN-003
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-004
```
