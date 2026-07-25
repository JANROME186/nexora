---
id: HOP-BR-BCM-LAB-003
format: markdown_structured_payload
type: business-rules
name: Sample Labeling Business Rules
version: 0.1.0
status: modeled
---

# Sample Labeling Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-LAB-003
  type: business-rules
  name: Sample Labeling Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-003
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A label can be printed only for a Sample in collected status; printing
    against a rejected or disposed sample is refused.
  applies_to: LabelPrintJob
  enforcement_point: command:PrintSpecimenLabel
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability status check against the Sample
    aggregate owned by BCM-LAB-002.
  test_refs:
  - TST-LBL-003-01
- id: RN-002
  statement: A label cannot be confirmed without a matched LabelMismatchCheck comparing
    the printed barcode against the sample's order and patient reference.
  applies_to: LabelPrintJob
  enforcement_point: command:ConfirmSpecimenLabel
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Mismatch detection requires comparing printed barcode content against
    Sample fields at confirmation time.
  test_refs:
  - TST-LBL-003-02
- id: RN-003
  statement: Confirming a label is the only action that may invoke AssignSpecimenLabel
    on the Sample aggregate; this capability must not write any other Sample field.
  applies_to: Sample
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement mirrors the aggregate ownership
    rule declared by BCM-LAB-002 (RN-006).
  test_refs:
  - TST-LBL-003-03
- id: RN-004
  statement: Labeling commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: LabelPrintJob
  enforcement_point: authorization:sample.label
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-LBL-003-04
- id: RN-005
  statement: A relabeling attempt on a sample already received, rejected or in_process
    requires an explicit override reason and is audited distinctly from first-time
    labeling.
  applies_to: LabelPrintJob
  enforcement_point: command:PrintSpecimenLabel
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires an explicit override justification and distinct audit classification
    beyond a generated field check.
  test_refs:
  - TST-LBL-003-05
- id: RN-006
  statement: Label print job events must include actor identity, branch, sample reference
    and print attempt count.
  applies_to: LabelPrintJob
  enforcement_point: event:SpecimenLabelPrinted, event:SpecimenLabelAssigned
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-LBL-003-06
enforcement_summary:
  generatable_rules:
  - RN-004
  - RN-006
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-005
```
