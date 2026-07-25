---
id: HOP-UI-BCM-QLT-007
format: markdown_structured_payload
type: ui-model
name: Audit Management UI Model
version: 0.1.0
status: modeled
---

# Audit Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-007
  type: ui-model
  name: Audit Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
  target_surface: employee_portal
screens:
- id: SCR-AUD-001
  name: Audit Calendar & Overview
  title:
    en: Audit Management
    es: Gestión de Auditorías
  route: /quality/audits
  permission_required: quality.audit.read
  components:
  - id: CMP-AUD-001
    type: DataTable
    title:
      en: Scheduled & Past Audits
      es: Auditorías Programadas y Realizadas
    operations:
    - listAuditSchedules
  - id: CMP-AUD-002
    type: FormModal
    title:
      en: Schedule New Audit
      es: Programar Nueva Auditoría
    operations:
    - createAuditSchedule
- id: SCR-AUD-002
  name: Audit Execution & Findings Workspace
  title:
    en: Audit Workspace & Finding Log
    es: Espacio de Ejecución y Registro de Hallazgos
  route: /quality/audits/:id
  permission_required: quality.audit.read
  components:
  - id: CMP-AUD-003
    type: DetailPanel
    title:
      en: Audit Plan Overview
      es: Resumen del Plan de Auditoría
    operations:
    - getAuditSchedule
  - id: CMP-AUD-004
    type: FindingLog
    title:
      en: Non-Conformities & Findings
      es: Hallazgos y No Conformidades
    operations:
    - recordAuditFinding
  - id: CMP-AUD-005
    type: CloseModal
    title:
      en: Audit Report & Closure
      es: Informe y Cierre de Auditoría
    operations:
    - closeAuditSchedule
i18n:
  namespaces:
  - quality.audit
  supported_locales:
  - es-MX
  - en-US
```
