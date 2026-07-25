---
id: HOP-UI-BCM-SVC-001
format: markdown_structured_payload
type: ui-model
name: Diagnostic Service Catalog UI Model
version: 0.2.0
status: modeled
---

# Diagnostic Service Catalog Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-SVC-001
  type: ui-model
  name: Diagnostic Service Catalog UI Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
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
  public_website:
    status: required
    generatable: not_applicable
    note: Public website pages are a COM-MOD-011-WEB-001 delivery concern, not an
      employee-portal screen. This capability only exposes the published-catalog read
      API (openapi-source.md public_surface) consumed by those public pages.
screens:
- id: SCR-SVC-001-01
  name: Diagnostic Service List
  route: /catalog/services
  type: list
  scopes:
  - catalog.service.read
  components:
  - DataTable
  - StatusFilter
  - CategoryFilter
  - SearchBar
  generatable: true
- id: SCR-SVC-001-02
  name: Diagnostic Service Editor
  route: /catalog/services/{serviceId}
  type: form
  scopes:
  - catalog.service.write
  components:
  - ServiceDetailForm
  - ComponentLinkEditor
  - CategorySelector
  generatable: true
- id: SCR-SVC-001-03
  name: Publish Service Dialog
  route: /catalog/services/{serviceId}/publish
  type: confirmation
  scopes:
  - catalog.service.publish
  components:
  - PublicationChecklist
  - ComponentEligibilitySummary
  generatable: false
  custom_reason: Displays cross-aggregate publication validation results.
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
