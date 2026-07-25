---
id: HOP-UI-BCM-QLT-003
format: markdown_structured_payload
type: ui-model
name: Calibration Management UI Model
version: 0.1.0
status: modeled
---

# Calibration Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-003
  type: ui-model
  name: Calibration Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: partial
  patient_portal:
    status: not_required
    generatable: not_applicable
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-CAL-003-01
  name: Calibration Log
  route: /admin/quality/equipment/{inventoryItemId}/calibrations
  purpose: Record calibration events and review calibration history and due dates.
  components:
  - DataTable
  - CalibrationEventForm
  - DueDateBadge
  generatable: partial
  custom_reason: Recording invokes the custom RecordCalibration command with conditional
    event publication (RN-001, RN-002).
states:
- pass
- fail
- adjusted
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: quality.calibration.*
  note: New user-facing strings must be registered under the quality.calibration.*
    message-key namespace, not hardcoded.
rationale: 'Calibration recording is an internal laboratory back-office task performed
  from the employee portal only.

  '
```
