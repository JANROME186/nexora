---
id: HOP-OBS-BCM-ATT-001
format: markdown_structured_payload
type: observability-model
name: Appointment Scheduling Observability Model
version: 0.1.0
status: modeled
---

# Appointment Scheduling Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-ATT-001
  type: observability-model
  name: Appointment Scheduling Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
  depends_on_capability: BCM-PLT-006
logs:
- event: appointment_requested
  level: info
  fields:
  - appointmentId
  - branchId
  - actorId
  - channel
- event: appointment_confirmed
  level: info
  fields:
  - appointmentId
  - branchId
  - actorId
- event: appointment_checked_in
  level: info
  fields:
  - appointmentId
  - branchId
  - actorId
- event: appointment_cancelled
  level: info
  fields:
  - appointmentId
  - actorId
  - reasonCode
- event: appointment_no_show
  level: warn
  fields:
  - appointmentId
  - branchId
metrics:
- name: appointment_requested_total
  type: counter
  labels:
  - tenantId
  - branchId
  - channel
- name: appointment_confirmed_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: appointment_no_show_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: appointment_lead_time_ms
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: ConfirmAppointment
  child_spans:
  - ValidateBranchOperationalStatus
  - ValidateOverlap
  - PublishAppointmentConfirmed
- span: CheckInAppointment
  child_spans:
  - HandoffToOrderCreation
audit_events:
- AppointmentRequested
- AppointmentConfirmed
- AppointmentCheckedIn
- AppointmentCancelled
- AppointmentNoShowMarked
alerts:
- name: HighAppointmentNoShowRate
  condition: appointment_no_show_total rate exceeds threshold
  severity: warning
```
