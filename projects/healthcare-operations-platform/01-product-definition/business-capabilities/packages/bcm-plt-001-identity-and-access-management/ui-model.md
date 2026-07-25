---
id: HOP-UI-BCM-PLT-001
format: markdown_structured_payload
type: ui-model
name: Identity and Access Management UI Model
version: 0.1.0
status: modeled
---

# Identity And Access Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-001
  type: ui-model
  name: Identity and Access Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-001
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: required
    generatable: true
  doctor_portal:
    status: required
    generatable: true
screens:
- id: SCR-IAM-001-01
  name: Patient Login
  route: /patient/login
  type: form
  scopes:
  - anonymous
  components:
  - LoginForm
  - TenantSelector
  - PasswordResetLink
  - LanguageSwitcher
  generatable: true
- id: SCR-IAM-001-02
  name: Doctor Login
  route: /doctor/login
  type: form
  scopes:
  - anonymous
  components:
  - LoginForm
  - TenantSelector
  - PasswordResetLink
  - LanguageSwitcher
  generatable: true
- id: SCR-IAM-001-03
  name: Support Impersonation Panel
  route: /admin/support/impersonate
  type: panel
  scopes:
  - users.write
  components:
  - ActiveTicketSelector
  - TargetUserSearch
  - ImpersonateButton
  - AuditReasonField
  generatable: true
- id: SCR-IAM-001-04
  name: Portal Dashboard Shell
  route: /portal/dashboard
  type: shell
  scopes:
  - PORTAL_PATIENT_ACCESS
  - PORTAL_DOCTOR_ACCESS
  components:
  - DynamicNavigationMenu
  - LanguageSwitcher
  - UserProfileDropdown
  generatable: true
states:
- authenticating
- authenticated
- blocked
- assisted_session
localization:
  languages:
  - en
  - es
  default: es
```
