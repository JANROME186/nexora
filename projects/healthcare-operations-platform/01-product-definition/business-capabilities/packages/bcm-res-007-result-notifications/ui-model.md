---
id: HOP-UI-BCM-RES-007
format: markdown_structured_payload
type: ui-model
name: Result Notifications UI Model
version: 0.1.0
status: modeled
---

# Result Notifications Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-RES-007
  type: ui-model
  name: Result Notifications UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
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
- id: SCR-RNT-007-01
  name: Result Notification History
  route: /results/{resultId}/notifications
  type: list
  scopes:
  - notification.read
  components:
  - DataTable
  - DispatchStatusBadge
  generatable: true
states:
- pending_submission
- submitted
- dispatched
- delivered
- failed
localization:
  languages:
  - en
  - es
  default: es
```
