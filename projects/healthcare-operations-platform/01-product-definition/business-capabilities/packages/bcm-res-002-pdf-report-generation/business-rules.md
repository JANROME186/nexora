---
id: HOP-BR-BCM-RES-002
format: markdown_structured_payload
type: business-rules
name: PDF Report Generation Business Rules
version: 0.1.0
status: modeled
---

# Pdf Report Generation Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-RES-002
  type: business-rules
  name: PDF Report Generation Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-002
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A report can be generated only for a released LaboratoryResult; generation
    against a captured, pending-validation or otherwise non-released result is refused.
  applies_to: GeneratedResultReport
  enforcement_point: command:GenerateResultReport
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability status check against LaboratoryResult
    (via BCM-RES-001's projection or the ResultReleased event) rather than a generic
    field validator.
  brm_alignment: BRM-001-R012
  test_refs:
  - TST-RPT-002-01
- id: RN-002
  statement: Every generated report must carry a unique identifier, a monotonically
    increasing per-result version and a content hash sufficient to verify integrity
    at serve time.
  applies_to: GeneratedResultReport
  enforcement_point: command:GenerateResultReport
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Hash computation and version-sequencing logic are custom, not a generated
    field default.
  test_refs:
  - TST-RPT-002-02
- id: RN-003
  statement: An amendment to a released result must trigger regeneration as a new
    report version; the prior report is marked superseded and never edited or deleted.
  applies_to: GeneratedResultReport
  enforcement_point: event:ResultAmended
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability event handling and an append-only versioning
    strategy.
  test_refs:
  - TST-RPT-002-03
- id: RN-004
  statement: Before serving a stored report, the content hash must be re-verified
    against the physical document from BCM-PLT-008; a mismatch blocks the serve and
    raises a critical integrity alert.
  applies_to: GeneratedResultReport
  enforcement_point: query:GetResultReport
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a runtime integrity re-check against externally stored bytes,
    not a static validation.
  test_refs:
  - TST-RPT-002-04
- id: RN-005
  statement: This capability never mutates LaboratoryResult, Sample, Patient or Doctor
    state; it only reads released/amended result data and writes its own GeneratedResultReport
    entity.
  applies_to: GeneratedResultReport
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement consistent with AGG-009's ownership
    rules.
  test_refs:
  - TST-RPT-002-05
- id: RN-006
  statement: Report generation commands must execute within the actor's tenant and
    laboratory scope.
  applies_to: GeneratedResultReport
  enforcement_point: authorization:report.generate, authorization:report.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-RPT-002-06
- id: RN-007
  statement: Report generation and access events must include actor identity (or system
    trigger), result reference and report version.
  applies_to: GeneratedResultReport
  enforcement_point: event:ReportGenerated
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-RPT-002-07
enforcement_summary:
  generatable_rules:
  - RN-006
  - RN-007
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-005
```
