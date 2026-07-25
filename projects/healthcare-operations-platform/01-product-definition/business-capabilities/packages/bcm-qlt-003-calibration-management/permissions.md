---
id: HOP-PERM-BCM-QLT-003
format: markdown_structured_payload
type: permissions
name: Calibration Management Permissions
version: 0.1.0
status: modeled
---

# Calibration Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-QLT-003
  type: permissions
  name: Calibration Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  depends_on_capability: BCM-PLT-001
scopes:
- code: quality.calibration.manage
  description: Record a calibration event.
- code: quality.calibration.read
  description: Read calibration history.
roles:
- role: tenant-administrator
  grants:
  - quality.calibration.manage
  - quality.calibration.read
- role: branch-administrator
  grants:
  - quality.calibration.read
- role: laboratory-technician
  grants:
  - quality.calibration.manage
  - quality.calibration.read
access_policies:
- id: POL-CAL-003-01
  statement: Calibration commands are scoped to the calling actor's tenant, laboratory
    and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-CAL-003-02
  statement: calibrationRecord may only be appended to by this capability; equipmentProfile
    is never written here.
  enforcement: field_level_delegation_boundary
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: CalibrationRecorded
    fields:
    - calibrationEventId
    - inventoryItemId
    - result
    - nextDueDate
  - event: CalibrationFailed
    fields:
    - calibrationEventId
    - inventoryItemId
```
