---
id: HOP-UI-BCM-ATT-003
format: markdown_structured_payload
type: ui-model
name: Reception Management UI Model
version: 0.1.0
status: modeled
---

# Reception Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-ATT-003
  type: ui-model
  name: Reception Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-003
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: not_required
    generatable: not_applicable
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-REC-003-01
  name: Reception Queue
  route: /reception
  type: worklist
  scopes:
  - reception.manage
  - reception.read
  components:
  - QueueTable
  - PriorityFilter
  - StatusFilter
  - StartVisitButton
  generatable: false
  custom_reason: Real-time queue ordering with tenant-configurable priority rules.
- id: SCR-REC-003-02
  name: Reception Identity Confirmation
  route: /reception/{visitId}/confirm-identity
  type: form
  scopes:
  - reception.manage
  components:
  - PatientIdentitySummary
  - IdentityConfirmationMethodSelector
  generatable: false
  custom_reason: Reads patient identity from BCM-PER-002 without exposing mutation
    actions.
- id: SCR-REC-003-03
  name: Reception Visit Detail
  route: /reception/{visitId}
  type: detail
  scopes:
  - reception.read
  components:
  - VisitSummary
  - LinkedAppointmentPanel
  - AuditTraceLink
  generatable: true
states:
- waiting
- called
- in_admission
- completed
- abandoned
localization:
  languages:
  - en
  - es
  default: es
```
