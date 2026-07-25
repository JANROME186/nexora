---
id: HOP-UI-BCM-RES-004
format: markdown_structured_payload
type: ui-model
name: Digital Delivery UI Model
version: 0.1.0
status: modeled
---

# Digital Delivery Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-RES-004
  type: ui-model
  name: Digital Delivery UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  target_surface: patient_portal
surfaces:
  employee_portal:
    status: not_required
    generatable: not_applicable
  patient_portal:
    status: required
    generatable: false
    custom_reason: Combines released-result summary, authorized report download and
      view-state recording.
  doctor_portal:
    status: required
    generatable: false
    custom_reason: Combines referred-patient released-result summary and authorized
      report download.
screens:
- id: SCR-DLV-004-01
  name: My Released Results
  route: /portal/results
  type: list
  scopes:
  - delivery.view
  components:
  - DataTable
  - StatusBadge
  generatable: true
- id: SCR-DLV-004-02
  name: Released Result Detail
  route: /portal/results/{deliveryTicketId}
  type: detail
  scopes:
  - delivery.view
  components:
  - ResultSummaryPanel
  - DownloadReportButton
  - ViewedIndicator
  generatable: false
  custom_reason: Triggers RecordResultViewed on open and re-verifies authorization
    before rendering.
states:
- pending_authorization
- authorized
- delivered
- viewed
- withheld
localization:
  languages:
  - en
  - es
  default: es
```
