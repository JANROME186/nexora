---
id: HOP-UI-BCM-QLT-004
format: markdown_structured_payload
type: ui-model
name: Equipment Management UI Model
version: 0.1.0
status: modeled
---

# Equipment Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-004
  type: ui-model
  name: Equipment Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
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
- id: SCR-EQP-004-01
  name: Equipment Registry
  route: /admin/quality/equipment
  purpose: Register equipment profiles and manage availability status.
  components:
  - DataTable
  - EquipmentProfileForm
  - AvailabilityStatusBadge
  generatable: partial
  custom_reason: Profile assignment and availability changes invoke custom commands
    (RN-001, RN-002, RN-004).
states:
- available
- in_use
- out_of_service
- retired
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: quality.equipment.*
  note: New user-facing strings must be registered under the quality.equipment.* message-key
    namespace, not hardcoded.
rationale: 'Equipment registry management is an internal laboratory back-office task
  performed from the employee portal only.

  '
```
