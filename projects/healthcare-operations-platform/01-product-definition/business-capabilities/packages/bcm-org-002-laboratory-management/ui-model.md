---
id: HOP-UI-BCM-ORG-002
format: markdown_structured_payload
type: ui-model
name: Laboratory Management UI Model
version: 1.0.0
---

# Laboratory Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-ORG-002
  type: ui-model
  name: Laboratory Management UI Model
  version: 1.0.0
screens:
- id: SCR-LAB-001
  name: LaboratoryManagementScreen
  route: /admin/laboratories
  surface: employee_portal
  permission_required: laboratory:read
  components:
  - LaboratoryListTable
  - RegisterLaboratoryModal
  - LaboratoryStatusBadge
- id: SCR-LAB-002
  name: LaboratoryDetailScreen
  route: /admin/laboratories/:laboratoryId
  surface: employee_portal
  permission_required: laboratory:read
  components:
  - LaboratoryProfileForm
  - SanitaryLicenseCard
  - ClinicalDirectorCard
  - BranchSummaryList
- id: SCR-LAB-003
  name: LaboratoryLicenseFormScreen
  route: /admin/laboratories/:laboratoryId/license
  surface: employee_portal
  permission_required: laboratory:manage_license
  components:
  - SanitaryLicenseForm
  - DocumentUploadWidget
  - ClinicalDirectorSelector
```
