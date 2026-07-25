---
id: HOP-BR-BCM-INV-003
format: markdown_structured_payload
type: business-rules
name: Lot Management Business Rules
version: 0.1.0
status: modeled
---

# Lot Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-003
  type: business-rules
  name: Lot Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-003
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A StockLot's remainingQuantity must never exceed its receivedQuantity
    and must never go negative.
  applies_to: StockLot
  enforcement_point: command:RegisterStockLot, command:QuarantineStockLot
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-LOT-003-01
- id: RN-002
  statement: A StockLot past its expirationDate must automatically transition to expired
    status and be excluded from new consumption or exit selection.
  applies_to: StockLot
  enforcement_point: scheduled_expiration_sweep
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a scheduled evaluation against the current date, not a request-time-only
    check.
  test_refs:
  - TST-LOT-003-02
- id: RN-003
  statement: Only this capability may write lotNumber, expirationDate, storageCondition
    and status; quantity fields are exclusively delegated to BCM-INV-005/006/007/008/009.
  applies_to: StockLot
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the shared-entity delegation boundary.
  test_refs:
  - TST-LOT-003-03
- id: RN-004
  statement: A disposed StockLot is terminal and can never transition back to active
    or quarantined.
  applies_to: StockLot
  enforcement_point: command:QuarantineStockLot
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-LOT-003-04
- id: RN-005
  statement: Lot management commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: StockLot
  enforcement_point: authorization:inventory.lot.manage, authorization:inventory.lot.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-LOT-003-05
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-004
  - RN-005
  custom_implementation_rules:
  - RN-002
  - RN-003
```
