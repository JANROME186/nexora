---
id: HOP-PERM-BCM-RES-002
format: markdown_structured_payload
type: permissions
name: PDF Report Generation Permissions
version: 0.1.0
status: modeled
---

# Pdf Report Generation Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-RES-002
  type: permissions
  name: PDF Report Generation Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-002
  depends_on_capability: BCM-PLT-001
scopes:
- code: report.generate
  description: Trigger manual report generation or regeneration.
- code: report.read
  description: Retrieve and view a generated report.
roles:
- role: medical-validator
  grants:
  - report.generate
  - report.read
- role: laboratory-technician
  grants:
  - report.read
- role: technical-validator
  grants:
  - report.read
- role: tenant-administrator
  grants:
  - report.read
- role: system
  grants:
  - report.generate
access_policies:
- id: POL-RPT-002-01
  statement: Report commands are scoped to the actor's tenant and laboratory.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-RPT-002-02
  statement: This capability never mutates LaboratoryResult, Sample, Patient or Doctor.
  enforcement: read_only_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ReportGenerated
    fields:
    - reportId
    - resultId
    - reportVersion
    - contentHash
  - event: ReportSuperseded
    fields:
    - reportId
    - resultId
    - reportVersion
```
