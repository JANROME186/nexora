---
id: HOP-UI-BCM-RES-001
format: markdown_structured_payload
type: ui-model
name: Result Management UI Model
version: 0.1.0
status: modeled
---

# Result Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-RES-001
  type: ui-model
  name: Result Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-001
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
- id: SCR-RMG-001-01
  name: Result Search and Worklist
  route: /results
  type: list
  scopes:
  - result.search
  components:
  - DataTable
  - StatusFilter
  - LaboratoryFilter
  - CriticalIndicatorBadge
  - AnalyteFilter
  generatable: true
- id: SCR-RMG-001-02
  name: Result Detail (internal)
  route: /results/{resultId}
  type: detail
  scopes:
  - result.read
  components:
  - ResultSummaryPanel
  - LifecycleStatusTimeline
  - AuditTraceLink
  generatable: true
states:
- captured
- pending_technical_validation
- technically_validated
- pending_medical_validation
- medically_validated
- released
- amended
localization:
  languages:
  - en
  - es
  default: es
```
