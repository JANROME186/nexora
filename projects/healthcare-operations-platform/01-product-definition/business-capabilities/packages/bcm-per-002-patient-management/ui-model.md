---
id: HOP-UI-BCM-PER-002
format: markdown_structured_payload
type: ui-model
name: Patient Management UI Model
version: 0.1.0
status: modeled
---

# Patient Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PER-002
  type: ui-model
  name: Patient Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: required
    generatable: true
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-PAT-002-07
  name: Patient Self Profile Editor
  route: /patient/profile
  type: form
  scopes:
  - PORTAL_PATIENT_ACCESS
  components:
  - PatientIdentityForm
  - ContactsSection
  - AddressSection
  - EmergencyContactSection
  - PreferredLocaleSelector
  generatable: true
- id: SCR-PAT-002-08
  name: Patient Consent Control Panel
  route: /patient/consents
  type: panel
  scopes:
  - PORTAL_PATIENT_ACCESS
  components:
  - ConsentList
  - ConsentToggle
  generatable: true
- id: SCR-PAT-002-09
  name: Patient Representative Management
  route: /patient/representatives
  type: panel
  scopes:
  - PORTAL_PATIENT_ACCESS
  components:
  - RepresentativeList
  - AddRepresentativeForm
  generatable: true
- id: SCR-PAT-002-01
  name: Patient List
  route: /patients
  type: list
  scopes:
  - patient.read
  components:
  - DataTable
  - StatusFilter
  - SearchBar
  - LaboratoryFilter
  generatable: true
- id: SCR-PAT-002-02
  name: Patient Editor
  route: /patients/{patientId}
  type: form
  scopes:
  - patient.write
  components:
  - PatientIdentityForm
  - PrimaryDocumentSection
  - AdditionalDocumentsSection
  - ContactsSection
  - AddressSection
  - EmergencyContactSection
  - PreferredLocaleSelector
  generatable: true
- id: SCR-PAT-002-03
  name: Patient Consent Panel
  route: /patients/{patientId}/consents
  type: panel
  scopes:
  - patient.write
  components:
  - ConsentList
  - ConsentEditor
  - ConsentEvidenceAttacher
  generatable: true
- id: SCR-PAT-002-04
  name: Patient Representative Panel
  route: /patients/{patientId}/representatives
  type: panel
  scopes:
  - patient.write
  components:
  - RepresentativeList
  - RepresentativeEditor
  - AuthorizationRangeEditor
  generatable: true
- id: SCR-PAT-002-05
  name: Patient Merge Workspace
  route: /patients/{patientId}/merge
  type: workspace
  scopes:
  - patient.merge
  components:
  - MergeCandidateSelector
  - MergePreview
  - MergeConfirmationDialog
  generatable: false
  custom_reason: Displays cross-projection preview and rewiring impact.
- id: SCR-PAT-002-06
  name: Patient Documents Panel
  route: /patients/{patientId}/documents
  type: panel
  scopes:
  - patient.write
  components:
  - DocumentList
  - DocumentUploader
  - ExpirationBadge
  generatable: true
states:
- active
- inactive
- merged
- deceased
localization:
  languages:
  - en
  - es
  default: es
```
