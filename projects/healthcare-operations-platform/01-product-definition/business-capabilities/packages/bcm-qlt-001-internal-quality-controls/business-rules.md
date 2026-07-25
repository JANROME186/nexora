---
id: HOP-BR-BCM-QLT-001
format: markdown_structured_payload
type: business-rules
name: Internal Quality Controls Business Rules
version: 0.1.0
status: modeled
---

# Internal Quality Controls Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-QLT-001
  type: business-rules
  name: Internal Quality Controls Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-001
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: This capability never mutates InventoryItem, StockLot or AGG-009 LaboratoryResult;
    all such references are read-only.
  applies_to: QualityControlRun
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-bounded-context read-only reference boundary.
  test_refs:
  - TST-IQC-001-01
- id: RN-002
  statement: ruleEvaluation must be computed from measuredValue against expectedRange
    using Westgard-style multi-rule logic before acceptanceDecision can be set.
  applies_to: QualityControlRun
  enforcement_point: command:RecordQualityControlRun
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Statistical multi-rule evaluation logic, not expressible as a generic
    CRUD validation.
  test_refs:
  - TST-IQC-001-02
- id: RN-003
  statement: An out_of_control ruleEvaluation must never resolve to acceptanceDecision
    accepted without an explicit, audited OverrideAcceptanceDecision command from
    a supervisor-scoped actor.
  applies_to: QualityControlRun
  enforcement_point: command:OverrideAcceptanceDecision
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a supervisor-scoped authorization check and mandatory override
    reason.
  test_refs:
  - TST-IQC-001-03
- id: RN-004
  statement: controlMaterialStockLotId must reference a StockLot whose classification
    is calibrator_control_material and whose status is active at run time.
  applies_to: QualityControlRun
  enforcement_point: command:RecordQualityControlRun
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires a cross-capability read of StockLot classification and status
    owned by BCM-INV-003.
  test_refs:
  - TST-IQC-001-04
- id: RN-005
  statement: Internal quality control commands must execute within the actor's tenant,
    laboratory and branch scope.
  applies_to: QualityControlRun
  enforcement_point: authorization:quality.internalcontrol.manage, authorization:quality.internalcontrol.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-IQC-001-05
enforcement_summary:
  generatable_rules:
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
```
