---
id: HOP-BR-BCM-PER-002
format: markdown_structured_payload
type: business-rules
name: Patient Management Business Rules
version: 0.1.0
status: modeled
---

# Patient Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-PER-002
  type: business-rules
  name: Patient Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A patient code must be unique within its tenant scope.
  applies_to: Patient
  enforcement_point: command:RegisterPatient, command:UpdatePatient
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PAT-002-01
- id: RN-002
  statement: Duplicate detection must be invoked and its result recorded before a
    Patient is registered.
  applies_to: Patient
  enforcement_point: command:RegisterPatient
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Registration must consult BCM-PER-001 duplicate detector and route
    confirmation flow.
  test_refs:
  - TST-PAT-002-02
  - TST-PAT-002-03
- id: RN-003
  statement: Only the patient-management bounded context may mutate Patient aggregate
    state; other contexts must reference PatientSnapshot only.
  applies_to: Patient
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforced through repository and adapter policy; verified in contract
    tests.
  test_refs:
  - TST-PAT-002-04
- id: RN-004
  statement: Every patient update must emit PatientUpdated with the delta of changed
    fields.
  applies_to: Patient
  enforcement_point: command:UpdatePatient
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PAT-002-05
- id: RN-005
  statement: A patient merge must nominate a surviving patient id and archive the
    merged patient snapshot for historical references.
  applies_to: Patient
  enforcement_point: command:MergePatient
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Merge rewires downstream snapshot references and requires idempotent
    replay.
  test_refs:
  - TST-PAT-002-06
- id: RN-006
  statement: A patient representative authorization must be active and current for
    the relationship to be honored.
  applies_to: PatientRepresentative
  enforcement_point: query:GetPatient, query:GetPatientSnapshot
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Depends on time-based policy and revocation events.
  test_refs:
  - TST-PAT-002-07
- id: RN-007
  statement: A patient consent may be revoked but existing consent evidence must remain
    immutable.
  applies_to: PatientConsent
  enforcement_point: command:RevokePatientConsent
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires append-only consent history with revocation event.
  test_refs:
  - TST-PAT-002-08
- id: RN-008
  statement: Personal document numbers must be stored with tenant-configured masking
    when displayed in read models.
  applies_to: Patient, PatientDocument
  enforcement_point: projection:PatientSnapshot
  severity: high
  audit_required: false
  generatable: true
  test_refs:
  - TST-PAT-002-09
- id: RN-009
  statement: A deceased patient status is terminal and cannot be reverted through
    update commands.
  applies_to: Patient
  enforcement_point: command:UpdatePatient
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PAT-002-10
- id: RN-010
  statement: Patient search, read and update require an authorized actor with the
    appropriate scope.
  applies_to: Patient
  enforcement_point: authorization:patient.read, authorization:patient.write
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-PAT-002-11
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-004
  - RN-008
  - RN-009
  - RN-010
  custom_implementation_rules:
  - RN-002
  - RN-003
  - RN-005
  - RN-006
  - RN-007
```
