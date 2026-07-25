---
id: HOP-UI-BCM-QLT-005
format: markdown_structured_payload
type: ui-model
name: Maintenance Management UI Model
version: 0.1.0
status: modeled
---

# Maintenance Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-005
  type: ui-model
  name: Maintenance Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-005
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
- id: SCR-MNT-005-01
  name: Maintenance Log
  route: /admin/quality/equipment/{inventoryItemId}/maintenance
  purpose: Start, complete and review maintenance events, including downtime and scheduling.
  components:
  - DataTable
  - MaintenanceEventForm
  - DowntimeSummary
  generatable: partial
  custom_reason: Start/complete actions invoke custom commands with event publication
    (RN-001, RN-002, RN-004).
states:
- scheduled
- in_progress
- completed
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: quality.maintenance.*
  note: New user-facing strings must be registered under the quality.maintenance.*
    message-key namespace, not hardcoded.
rationale: 'Maintenance recording is an internal laboratory back-office task performed
  from the employee portal only.

  '
```
