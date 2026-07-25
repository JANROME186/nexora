---
id: HOP-TEST-BCM-ATT-001
format: markdown_structured_payload
type: test-model
name: Appointment Scheduling Test Model
version: 0.2.0
status: modeled
---

# Appointment Scheduling Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-ATT-001
  type: test-model
  name: Appointment Scheduling Test Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
test_cases:
- id: TST-APT-001-01
  type: acceptance
  validates_rule: RN-001
  statement: Confirming an appointment for a suspended branch is rejected.
  generatable: false
- id: TST-APT-001-02
  type: acceptance
  validates_rule: RN-002
  statement: Confirming an appointment that overlaps an existing confirmed appointment
    for the same patient is rejected.
  generatable: false
- id: TST-APT-001-03
  type: acceptance
  validates_rule: RN-003
  statement: Requesting an appointment with an unpublished catalog item is rejected.
  generatable: false
- id: TST-APT-001-04
  type: contract
  validates_rule: RN-004
  statement: Unauthorized or out-of-scope actors cannot manage appointments.
  generatable: true
- id: TST-APT-001-05
  type: architecture
  validates_rule: RN-005
  statement: Check-in handoff invokes BCM-LAB-001 CreateDiagnosticOrder rather than
    direct order persistence.
  generatable: false
- id: TST-APT-001-06
  type: acceptance
  validates_rule: RN-006
  statement: An appointment past its grace period without check-in transitions to
    no_show.
  generatable: false
- id: TST-APT-001-07
  type: unit
  validates_rule: RN-007
  statement: AppointmentConfirmed event carries actor, branch and channel.
  generatable: true
- id: TST-APT-001-08
  type: acceptance
  validates_rule: RN-008
  statement: An anonymous public-website appointment request captures a ProspectiveContact,
    remains requested and is rejected once the public rate-limit is exceeded.
  generatable: false
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-APT-001-01
  - TST-APT-001-02
  - TST-APT-001-03
  - TST-APT-001-05
  - TST-APT-001-06
  - TST-APT-001-08
```
