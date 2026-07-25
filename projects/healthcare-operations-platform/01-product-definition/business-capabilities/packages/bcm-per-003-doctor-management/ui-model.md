---
id: HOP-UI-BCM-PER-003
format: markdown_structured_payload
type: ui-model
name: Doctor Management UI Model
version: 0.1.0
status: modeled
---

# Doctor Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PER-003
  type: ui-model
  name: Doctor Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  doctor_portal:
    status: required
    generatable: true
  patient_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-DOC-003-06
  name: Doctor Portal Profile View
  route: /doctor/profile
  type: form
  scopes:
  - PORTAL_DOCTOR_ACCESS
  components:
  - DoctorIdentityForm
  - ContactsSection
  - AddressSection
  generatable: true
- id: SCR-DOC-003-01
  name: Doctor List
  route: /doctors
  type: list
  scopes:
  - doctor.read
  components:
  - DataTable
  - StatusFilter
  - SearchBar
  - LaboratoryFilter
  generatable: true
- id: SCR-DOC-003-02
  name: Doctor Editor
  route: /doctors/{doctorId}
  type: form
  scopes:
  - doctor.write
  components:
  - DoctorIdentityForm
  - PrimaryDocumentSection
  - ContactsSection
  - AddressSection
  - DoctorTypeSelector
  generatable: true
- id: SCR-DOC-003-03
  name: Credential Panel
  route: /doctors/{doctorId}/credentials
  type: panel
  scopes:
  - doctor.write
  components:
  - CredentialList
  - CredentialEditor
  - VerificationBadge
  generatable: true
- id: SCR-DOC-003-04
  name: Specialty Panel
  route: /doctors/{doctorId}/specialties
  type: panel
  scopes:
  - doctor.write
  components:
  - SpecialtyList
  - SpecialtySelector
  generatable: true
- id: SCR-DOC-003-05
  name: Portal Access Baseline Panel
  route: /doctors/{doctorId}/portal-access
  type: panel
  scopes:
  - doctor.portal.prepare
  components:
  - PortalStatusBadge
  - PortalReadinessForm
  - ProvisioningDeferredNotice
  generatable: false
  custom_reason: Displays deferred provisioning boundary explicitly.
states:
- active
- suspended
- retired
localization:
  languages:
  - en
  - es
  default: es
```
