---
id: HOP-UI-BCM-ORG-003
format: markdown_structured_payload
type: ui-model
name: Branch Management UI Model
version: 1.0.0
---

# Branch Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-ORG-003
  type: ui-model
  name: Branch Management UI Model
  version: 1.0.0
screens:
- id: SCR-BRN-001
  name: BranchManagementScreen
  route: /admin/branches
  surface: employee_portal
  permission_required: branch:read
  components:
  - BranchListTable
  - CreateBranchModal
  - BranchStatusBadge
- id: SCR-BRN-002
  name: BranchDetailScreen
  route: /admin/branches/:branchId
  surface: employee_portal
  permission_required: branch:read
  components:
  - BranchAddressForm
  - BranchCapacityCard
  - BranchScheduleView
- id: SCR-BRN-003
  name: BranchScheduleFormScreen
  route: /admin/branches/:branchId/schedule
  surface: employee_portal
  permission_required: branch:manage_schedule
  components:
  - OperatingHoursForm
  - HolidayScheduleTable
```
