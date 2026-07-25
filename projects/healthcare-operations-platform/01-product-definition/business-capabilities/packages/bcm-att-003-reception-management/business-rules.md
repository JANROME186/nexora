---
id: HOP-BR-BCM-ATT-003
format: markdown_structured_payload
type: business-rules
name: Reception Management Business Rules
version: 0.1.0
status: modeled
---

# Reception Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-ATT-003
  type: business-rules
  name: Reception Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-003
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A reception visit cannot advance to in_admission status without an explicit
    identity confirmation against BCM-PER-002.
  applies_to: ReceptionVisit
  enforcement_point: command:ConfirmReceptionIdentity, command:AdvanceToAdmission
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Requires a read-only cross-capability identity check against Patient
    master data.
  test_refs:
  - TST-REC-003-01
- id: RN-002
  statement: A reception visit linked to an appointment must reference an appointment
    in checked_in status.
  applies_to: ReceptionVisit
  enforcement_point: command:StartReceptionVisit
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires cross-capability state check against BCM-ATT-001 AppointmentSlot.
  test_refs:
  - TST-REC-003-02
- id: RN-003
  statement: Reception must not mutate Patient aggregate state; identity confirmation
    is read-only.
  applies_to: ReceptionVisit
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Boundary enforcement mirrors BRM-001-R003 patient master data ownership.
  test_refs:
  - TST-REC-003-03
- id: RN-004
  statement: Reception commands must execute within the actor's tenant, laboratory
    and branch scope.
  applies_to: ReceptionVisit
  enforcement_point: authorization:reception.manage
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-REC-003-04
- id: RN-005
  statement: Queue prioritization must apply tenant-configured priority rules (for
    example urgent clinical flags) ahead of first-in-first-out ordering.
  applies_to: ReceptionVisit
  enforcement_point: query:GetReceptionQueue
  severity: medium
  audit_required: false
  generatable: false
  custom_reason: Priority computation depends on tenant-configurable rules and clinical
    flags.
  test_refs:
  - TST-REC-003-05
- id: RN-006
  statement: Handing off a reception visit to Admission Management must not create
    or mutate a DiagnosticOrder directly.
  applies_to: ReceptionVisit
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Order mutation remains exclusive to BCM-LAB-001, orchestrated through
    BCM-ATT-004.
  test_refs:
  - TST-REC-003-06
- id: RN-007
  statement: Reception audit events must include actor identity, branch and intake
    channel.
  applies_to: ReceptionVisit
  enforcement_point: event:ReceptionVisitReadyForAdmission
  severity: high
  audit_required: true
  generatable: true
  test_refs:
  - TST-REC-003-07
enforcement_summary:
  generatable_rules:
  - RN-004
  - RN-007
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-005
  - RN-006
```
