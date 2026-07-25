---
id: HOP-BR-BCM-INV-001
format: markdown_structured_payload
type: business-rules
name: Product Catalog Business Rules
version: 0.1.0
status: modeled
---

# Product Catalog Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-INV-001
  type: business-rules
  name: Product Catalog Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-001
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: itemCode must be unique within a tenant/laboratory/branch scope.
  applies_to: InventoryItem
  enforcement_point: command:CreateInventoryItem
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-CAT-001-01
- id: RN-002
  statement: An InventoryItem's itemType and classification must be internally consistent
    (for example classification capital_equipment requires itemType equipment).
  applies_to: InventoryItem
  enforcement_point: command:CreateInventoryItem
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Cross-field consistency validation between itemType and classification
    enumerations.
  test_refs:
  - TST-CAT-001-02
- id: RN-003
  statement: Only this capability may create an InventoryItem or write its core identity
    fields (itemCode, itemName, itemType, classification, unitOfMeasure, status);
    every other capability is restricted to its own delegated field set.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated-ownership boundary shared by all 13 COM-MOD-010
    capabilities.
  test_refs:
  - TST-CAT-001-03
- id: RN-004
  statement: A discontinued InventoryItem must reject new stock-entry, purchase-order-line
    and reagent-assignment commands while remaining readable for historical traceability.
  applies_to: InventoryItem
  enforcement_point: command:DiscontinueInventoryItem
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Cross-capability lifecycle gate consulted by delegated Apply* commands.
  test_refs:
  - TST-CAT-001-04
- id: RN-005
  statement: Product catalog commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: InventoryItem
  enforcement_point: authorization:inventory.catalog.manage, authorization:inventory.catalog.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-CAT-001-05
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-005
  custom_implementation_rules:
  - RN-002
  - RN-003
  - RN-004
```
