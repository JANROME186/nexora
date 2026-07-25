---
id: HOP-UI-BCM-RES-006
format: markdown_structured_payload
type: ui-model
name: Critical Results UI Model
version: 0.1.0
status: modeled
---

# Critical Results Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-RES-006
  type: ui-model
  name: Critical Results UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
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
- id: SCR-CRR-006-01
  name: Critical Result Escalation Worklist
  route: /results/critical-escalations
  type: list
  scopes:
  - escalation.read
  components:
  - DataTable
  - EscalationTierBadge
  - DeadlineCountdown
  generatable: true
- id: SCR-CRR-006-02
  name: Acknowledge Critical Result
  route: /results/critical-escalations/{escalationId}/acknowledge
  type: action_panel
  scopes:
  - escalation.manage
  components:
  - CriticalReasonPanel
  - AcknowledgeButton
  generatable: false
  custom_reason: Enforces the terminal-state guard requiring both acknowledgement
    fields.
states:
- open
- acknowledged
- escalated
- closed
localization:
  languages:
  - en
  - es
  default: es
```
