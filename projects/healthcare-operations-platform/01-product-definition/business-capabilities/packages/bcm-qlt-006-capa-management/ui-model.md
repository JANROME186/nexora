---
id: HOP-UI-BCM-QLT-006
format: markdown_structured_payload
type: ui-model
name: CAPA Management UI Model
version: 0.1.0
status: modeled
---

# Capa Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-006
  type: ui-model
  name: CAPA Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-006
  target_surface: employee_portal
screens:
- id: SCR-CAP-001
  name: CAPA Management Dashboard & List
  title:
    en: CAPA Management
    es: Gestión CAPA
  route: /quality/capa
  permission_required: quality.capa.read
  components:
  - id: CMP-CAP-001
    type: DataTable
    title:
      en: Active & Closed CAPA Investigations
      es: Investigaciones CAPA Activas y Cerradas
    operations:
    - listCapaInvestigations
  - id: CMP-CAP-002
    type: FormModal
    title:
      en: Initiate CAPA Investigation
      es: Iniciar Investigación CAPA
    operations:
    - createCapaInvestigation
- id: SCR-CAP-002
  name: CAPA Investigation Detail & Workflow
  title:
    en: CAPA Investigation Workspace
    es: Espacio de Trabajo de Investigación CAPA
  route: /quality/capa/:id
  permission_required: quality.capa.read
  components:
  - id: CMP-CAP-003
    type: DetailPanel
    title:
      en: Investigation Overview & RCA
      es: Resumen de Investigación y Causa Raíz
    operations:
    - getCapaInvestigation
    - recordRootCauseAnalysis
  - id: CMP-CAP-004
    type: ActionBoard
    title:
      en: Action Plan Items
      es: Elementos del Plan de Acción
    operations:
    - approveCapaActionPlan
  - id: CMP-CAP-005
    type: VerificationModal
    title:
      en: Effectiveness Verification & Closure
      es: Verificación de Efectividad y Cierre
    operations:
    - verifyCapaEffectiveness
i18n:
  namespaces:
  - quality.capa
  supported_locales:
  - es-MX
  - en-US
```
