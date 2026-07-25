---
id: HOP-PRM-BCM-QLT-007
format: markdown_structured_payload
type: permissions
name: Audit Management Permissions
version: 0.1.0
status: modeled
---

# Audit Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-QLT-007
  type: permissions
  name: Audit Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
permissions:
- code: quality.audit.read
  name:
    en: View Audits
    es: Ver Auditorías
  description: Grants view access to audit schedules, finding logs, and audit reports.
  actions:
  - listAuditSchedules
  - getAuditSchedule
- code: quality.audit.manage
  name:
    en: Manage Audit Schedules
    es: Gestionar Agenda de Auditorías
  description: Grants permission to plan audit schedules, assign auditors, and close
    audit cycles.
  actions:
  - createAuditSchedule
  - closeAuditSchedule
- code: quality.audit.execute
  name:
    en: Execute Audit & Record Findings
    es: Ejecutar Auditoría y Registrar Hallazgos
  description: Grants lead auditor / auditor permission to log non-conformity findings
    during audit execution.
  actions:
  - recordAuditFinding
```
