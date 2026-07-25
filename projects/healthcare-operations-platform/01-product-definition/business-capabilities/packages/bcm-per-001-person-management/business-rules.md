---
id: HOP-BR-BCM-PER-001
format: markdown_structured_payload
type: business-rules
name: Person Management Business Rules
version: 0.1.0
status: modeled
---

# Person Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-PER-001
  type: business-rules
  name: Person Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: Person natural keys must be normalized using tenant locale rules before
    duplicate comparison.
  applies_to: PersonNaturalKey
  enforcement_point: service:PersonDuplicateDetector
  severity: high
  audit_required: false
  generatable: true
  test_refs:
  - TST-PER-001-01
- id: RN-002
  statement: A primary personal document number must be unique within a tenant across
    Patient and Doctor scopes.
  applies_to: PersonDocument
  enforcement_point: query:PersonSearchIndex, command:PatientRegister, command:DoctorRegister
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-context uniqueness requires read-model projection and locking
    policy.
  test_refs:
  - TST-PER-001-02
  - TST-PER-001-03
- id: RN-003
  statement: Duplicate detection must return candidate matches with a confidence score
    before any register command is confirmed.
  applies_to: PersonDuplicateDetector
  enforcement_point: service:PersonDuplicateDetector
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Confidence scoring requires configurable natural-key weights per
    tenant.
  test_refs:
  - TST-PER-001-04
- id: RN-004
  statement: Person read model may only be updated by projecting published domain
    events from patient-management and medical-staff.
  applies_to: PersonSearchIndex
  enforcement_point: projection:PersonSearchIndex
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Projection ordering and idempotence policy require custom rules.
  test_refs:
  - TST-PER-001-05
- id: RN-005
  statement: Personal data access, search and export must be tenant scoped and require
    an authorized actor.
  applies_to: PersonSearchIndex, PersonDocument
  enforcement_point: authorization:person.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-PER-001-06
- id: RN-006
  statement: Person duplicate detection results must be logged as an audit event when
    triggered by a registration command.
  applies_to: PersonDuplicateDetector
  enforcement_point: service:PersonDuplicateDetector
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-PER-001-07
- id: RN-007
  statement: National identifier values must be stored using a one-way hash when used
    for duplicate detection.
  applies_to: PersonNaturalKey
  enforcement_point: service:PersonDuplicateDetector
  severity: critical
  audit_required: false
  generatable: false
  custom_reason: Hash function selection is a security configuration decision.
  test_refs:
  - TST-PER-001-08
enforcement_summary:
  generatable_rules:
  - RN-001
  - RN-005
  - RN-006
  custom_implementation_rules:
  - RN-002
  - RN-003
  - RN-004
  - RN-007
```
