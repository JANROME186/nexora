---
id: HOP-BR-BCM-RES-005
format: markdown_structured_payload
type: business-rules
name: Result History Business Rules
version: 0.1.0
status: modeled
---

# Result History Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-RES-005
  type: business-rules
  name: Result History Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-005
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A history entry is created only from an authorized ResultDeliveryAuthorized
    event; a result the recipient is not authorized to view is never added to their
    PatientResultHistoryView.
  applies_to: PatientResultHistoryView
  enforcement_point: event:ResultDeliveryAuthorized
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability authorization-state confirmation from BCM-RES-004
    before projecting.
  brm_alignment: BRM-001-R014
  test_refs:
  - TST-RHS-005-01
- id: RN-002
  statement: This capability never issues a command against LaboratoryResult or Patient;
    it exposes queries only.
  applies_to: PatientResultHistoryView
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability boundary enforcement mirroring BCM-RES-001's read-only
    pattern.
  test_refs:
  - TST-RHS-005-02
- id: RN-003
  statement: Trend computation for a given analyte compares only entries the requesting
    recipient is themselves authorized to view; it must never read or expose another
    patient's value.
  applies_to: PatientResultHistoryView
  enforcement_point: query:GetResultHistory
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-entry authorization scoping is custom logic, not a generic
    query filter.
  test_refs:
  - TST-RHS-005-03
- id: RN-004
  statement: An amendment to a result already present in history updates the corresponding
    entry only after BCM-RES-004 re-authorizes the amended delivery; the pre-amendment
    value is never left displayed as current.
  applies_to: PatientResultHistoryView
  enforcement_point: event:ResultDeliveryWithheld
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability event coordination with BCM-RES-004's withhold/reauthorize
    workflow.
  test_refs:
  - TST-RHS-005-04
- id: RN-005
  statement: History queries must execute within the recipient's own authorization
    scope; a patient may view only their own history, a representative only their
    represented patients', and a referring doctor only their referred patients'.
  applies_to: PatientResultHistoryView
  enforcement_point: authorization:history.view
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-RHS-005-05
- id: RN-006
  statement: Every history query must be auditable, recording recipient, patient scope
    and timestamp.
  applies_to: PatientResultHistoryView
  enforcement_point: query:GetResultHistory
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-RHS-005-06
enforcement_summary:
  generatable_rules:
  - RN-005
  - RN-006
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
```
