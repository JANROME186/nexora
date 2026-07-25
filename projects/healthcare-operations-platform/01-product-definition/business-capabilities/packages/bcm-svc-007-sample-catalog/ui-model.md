---
id: HOP-UI-BCM-SVC-007
format: markdown_structured_payload
type: ui-model
name: Sample Catalog UI Model
version: 0.1.0
status: modeled
---

# Sample Catalog Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-SVC-007
  type: ui-model
  name: Sample Catalog UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: read_only_later
    generatable: deferred
  doctor_portal:
    status: read_only_later
    generatable: deferred
screens:
- id: SCR-SVC-007-01
  name: Sample Type List
  route: /catalog/samples/types
  type: list
  scopes:
  - catalog.sample.read
  components:
  - DataTable
  - MatrixFilter
  - StatusFilter
  - SearchBar
  generatable: true
- id: SCR-SVC-007-02
  name: Sample Requirement List
  route: /catalog/samples/requirements
  type: list
  scopes:
  - catalog.sample.read
  components:
  - DataTable
  - SampleTypeFilter
  - StatusFilter
  generatable: true
- id: SCR-SVC-007-03
  name: Sample Requirement Editor
  route: /catalog/samples/requirements/{requirementId}
  type: form
  scopes:
  - catalog.sample.write
  components:
  - RequirementDetailForm
  - SampleTypeSelector
  - ContainerSelector
  - HandlingEditor
  generatable: true
- id: SCR-SVC-007-04
  name: Publish Sample Requirement Dialog
  route: /catalog/samples/requirements/{requirementId}/publish
  type: confirmation
  scopes:
  - catalog.sample.publish
  components:
  - PublicationChecklist
  - HandlingCompletenessSummary
  generatable: false
  custom_reason: Displays sample type publication and handling completeness validation.
states:
- draft
- published
- deprecated
- retired
localization:
  languages:
  - en
  - es
  default: es
```
