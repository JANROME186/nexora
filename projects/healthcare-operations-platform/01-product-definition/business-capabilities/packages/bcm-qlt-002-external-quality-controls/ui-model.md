---
id: HOP-UI-BCM-QLT-002
format: markdown_structured_payload
type: ui-model
name: External Quality Controls UI Model
version: 0.1.0
status: modeled
---

# External Quality Controls Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-QLT-002
  type: ui-model
  name: External Quality Controls UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
  target_surface: employee_portal
screens:
- id: SCR-EQC-001
  name: External Quality Controls Screen
  title:
    en: External Quality Controls (EQA / PT)
    es: Controles Externos de Calidad (PEEC / EQA)
  route: /quality/external-controls
  permission_required: quality.externalcontrol.read
  components:
  - id: CMP-EQC-001
    type: DataTable
    title:
      en: EQA Surveys & Evaluations
      es: Evaluaciones y Rondas EQA
    operations:
    - listExternalQualityEvaluations
  - id: CMP-EQC-002
    type: FormModal
    title:
      en: Register Survey Measurement
      es: Registrar Medición de Muestra EQA
    operations:
    - createExternalQualityEvaluation
  - id: CMP-EQC-003
    type: ScoreModal
    title:
      en: Enter Provider Evaluation Scoring
      es: Registrar Calificación del Proveedor EQA
    operations:
    - scoreExternalQualityEvaluation
i18n:
  namespaces:
  - quality.external_control
  supported_locales:
  - es-MX
  - en-US
```
