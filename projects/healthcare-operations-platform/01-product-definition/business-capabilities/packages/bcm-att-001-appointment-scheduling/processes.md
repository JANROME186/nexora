---
id: HOP-PROC-BCM-ATT-001
format: markdown_structured_payload
type: processes
name: Appointment Scheduling Processes
version: 0.1.0
status: modeled
---

# Appointment Scheduling Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-ATT-001
  type: processes
  name: Appointment Scheduling Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
actors:
- id: receptionist
  actor_ref: ACT-004
  name: Receptionist
  source: ACM-001
- id: patient
  actor_ref: ACT-012
  name: Patient
  source: ACM-001
  note: Patient portal appointment request is appointment_request_later (COM-MOD-009).
processes:
- id: PRC-APT-001-01
  name: Request appointment
  actor: receptionist
  trigger: A patient needs a scheduled diagnostic service visit.
  commands:
  - RequestAppointment
  preconditions:
  - Actor holds appointment.manage.
  steps:
  - Capture patient, optional doctor and requested catalog items.
  - Surface preparation instructions for requested items.
  - Select a scheduled window.
  - Persist AppointmentSlot with requested status.
  outcome: AppointmentRequested
  rules:
  - RN-003
  - RN-004
- id: PRC-APT-001-02
  name: Confirm appointment
  actor: receptionist
  trigger: The requested slot is validated and finalized.
  commands:
  - ConfirmAppointment
  preconditions:
  - Branch is operationally active.
  - No overlapping confirmed appointment exists for the patient.
  steps:
  - Validate branch operational status.
  - Validate absence of overlapping confirmed appointments.
  - Transition appointment to confirmed status.
  - Publish AppointmentConfirmed.
  outcome: AppointmentConfirmed
  rules:
  - RN-001
  - RN-002
  - RN-004
  - RN-007
- id: PRC-APT-001-03
  name: Check in appointment
  actor: receptionist
  trigger: The patient arrives for the confirmed appointment.
  commands:
  - CheckInAppointment
  preconditions:
  - Appointment is confirmed.
  steps:
  - Transition appointment to checked_in status.
  - Hand off appointment as intake reference to Reception Management.
  outcome: AppointmentCheckedIn
  rules:
  - RN-004
  - RN-005
- id: PRC-APT-001-04
  name: Cancel appointment
  actor: receptionist
  trigger: The appointment must be discarded before check-in.
  commands:
  - CancelAppointment
  steps:
  - Capture cancellation reason.
  - Transition appointment to cancelled status.
  - Publish AppointmentCancelled.
  outcome: AppointmentCancelled
  rules:
  - RN-004
- id: PRC-APT-001-05
  name: Mark appointment no-show
  actor: receptionist
  trigger: The grace period after the scheduled window elapsed without check-in.
  commands:
  - MarkAppointmentNoShow
  steps:
  - Transition appointment to no_show status.
  - Publish AppointmentNoShowMarked.
  outcome: AppointmentNoShowMarked
  rules:
  - RN-006
commands:
- name: RequestAppointment
  generatable: false
  custom_reason: Preparation-instruction surfacing and catalog publication check.
- name: ConfirmAppointment
  generatable: false
  custom_reason: Branch operational-status and overlap validation.
- name: CheckInAppointment
  generatable: false
  custom_reason: Handoff to BCM-ATT-003 / BCM-LAB-001 order creation.
- name: CancelAppointment
  generatable: true
- name: MarkAppointmentNoShow
  generatable: false
  custom_reason: Tenant-configurable grace-period policy.
```
