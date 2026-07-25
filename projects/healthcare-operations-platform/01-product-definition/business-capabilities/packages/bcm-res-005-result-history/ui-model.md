---
id: HOP-UI-BCM-RES-005
format: markdown_structured_payload
type: ui-model
name: Result History UI Model
version: 0.1.0
status: modeled
---

# Result History Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-RES-005
  type: ui-model
  name: Result History UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-005
  target_surface: patient_portal
surfaces:
  employee_portal:
    status: not_required
    generatable: not_applicable
  patient_portal:
    status: required
    generatable: false
    custom_reason: Combines chronological history rendering with trend-indicator visualization
      scoped to authorized entries.
  doctor_portal:
    status: required
    generatable: false
    custom_reason: Same chronological/trend view, scoped to referred patients.
screens:
- id: SCR-RHS-005-01
  name: Result History and Trends
  route: /portal/results/history
  type: list
  scopes:
  - history.view
  components:
  - ChronologicalResultTimeline
  - AnalyteTrendChart
  - TrendIndicatorBadge
  generatable: false
  custom_reason: Trend visualization requires cross-entry, authorization-scoped comparison.
states: []
localization:
  languages:
  - en
  - es
  default: es
```
