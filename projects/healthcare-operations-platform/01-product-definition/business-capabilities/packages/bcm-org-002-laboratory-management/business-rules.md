---
id: HOP-BUS-RUL-BCM-ORG-002
format: markdown_structured_payload
type: business-rules
name: Laboratory Management Business Rules
version: 1.0.0
---

# Laboratory Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BUS-RUL-BCM-ORG-002
  type: business-rules
  name: Laboratory Management Business Rules
  version: 1.0.0
rules:
- id: RN-LAB-001
  statement: Laboratory code must be unique within tenant boundaries.
  applies_to: LaboratoryRoot.code
  enforcement_point: LaboratoryRegistrationService.createLaboratory
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-LAB-001
- id: RN-LAB-002
  statement: An active laboratory must possess a valid, non-expired sanitary license.
  applies_to: LaboratorySanitaryLicense
  enforcement_point: LaboratoryStatusService.activateLaboratory
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-LAB-002
- id: RN-LAB-003
  statement: An active laboratory must have an assigned, verified clinical director.
  applies_to: ClinicalDirectorAssignment
  enforcement_point: LaboratoryDirectorService.assignClinicalDirector
  severity: HIGH
  audit_required: true
  test_refs:
  - TEST-LAB-003
- id: RN-LAB-004
  statement: Sanitary license expiration within 30 days triggers an automated operational
    warning alert.
  applies_to: LaboratorySanitaryLicense.expiration_date
  enforcement_point: LaboratoryLicenseScheduler.checkExpirations
  severity: MEDIUM
  audit_required: true
  test_refs:
  - TEST-LAB-004
- id: RN-LAB-005
  statement: Laboratory status changes to SUSPENDED or ARCHIVED propagate immediate
    operational pause to all child branches.
  applies_to: LaboratoryRoot.status
  enforcement_point: LaboratoryStatusService.updateStatus
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-LAB-005
```
