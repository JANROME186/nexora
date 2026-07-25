---
id: HOP-UI-BCM-PLT-003
format: markdown_structured_payload
type: ui-model
name: Notification Management UI Model
version: 0.1.0
status: not_applicable
---

# Notification Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-003
  type: ui-model
  name: Notification Management UI Model
  version: 0.1.0
  status: not_applicable
  classification: editable_model
  capability: BCM-PLT-003
  target_surface: none
surfaces:
  employee_portal:
    status: not_required
    generatable: not_applicable
  patient_portal:
    status: not_required
    generatable: not_applicable
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens: []
states:
- queued
- dispatched
- delivered
- failed
- suppressed_by_preference
localization:
  languages:
  - en
  - es
  default: es
rationale: 'BCM-PLT-003 is a system-to-system internal dispatch service with no direct
  UI; requesting capabilities (BCM-RES-007) surface any notification-facing screens
  themselves. No screens are modeled here.

  '
```
