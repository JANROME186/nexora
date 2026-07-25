---
id: HOP-UI-BCM-LAB-002
format: markdown_structured_payload
type: ui-model
name: Sample Collection UI Model
version: 0.1.0
status: modeled
---

# Sample Collection Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-LAB-002
  type: ui-model
  name: Sample Collection UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: status_later
    generatable: not_applicable
  doctor_portal:
    status: status_later
    generatable: not_applicable
screens:
- id: SCR-COL-002-01
  name: Collection Worklist
  route: /samples/collection-worklist
  type: list
  scopes:
  - sample.collect
  components:
  - DataTable
  - BranchFilter
  - PriorityBadge
  generatable: true
- id: SCR-COL-002-02
  name: Collect Sample
  route: /samples/collect/{orderLineId}
  type: form
  scopes:
  - sample.collect
  components:
  - PatientIdentityPanel
  - SampleRequirementPanel
  - CollectionDataForm
  - RejectAtCollectionDialog
  generatable: false
  custom_reason: Combines snapshot capture, sample-requirement display and structured
    rejection capture.
- id: SCR-COL-002-03
  name: Sample Detail
  route: /samples/{sampleId}
  type: detail
  scopes:
  - sample.read
  components:
  - SampleSummary
  - ChainOfCustodyTimeline
  - SnapshotDetailPanel
  - AuditTraceLink
  generatable: true
states:
- collected
- labeled
- in_transit
- received
- rejected
- in_process
- disposed
localization:
  languages:
  - en
  - es
  default: es
```
