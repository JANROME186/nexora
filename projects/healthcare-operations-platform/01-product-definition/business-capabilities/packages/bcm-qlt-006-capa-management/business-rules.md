---
id: HOP-BR-BCM-QLT-006
format: markdown_structured_payload
type: business-rules
name: CAPA Management Business Rules
version: 0.1.0
status: modeled
---

# Capa Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-QLT-006
  type: business-rules
  name: CAPA Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-006
rules:
- id: RN-CAP-001
  name: Mandatory Root Cause Analysis
  description: A CAPA investigation cannot move from 'investigating' to 'action_plan_pending'
    without documenting root cause methodology and summary.
  enforcement: synchronous
  error_code: CAPA_RCA_REQUIRED
  message:
    en: Root Cause Analysis methodology and summary are required before submitting
      action plan.
    es: Se requiere registrar la metodología y resumen de Análisis de Causa Raíz antes
      de enviar el plan de acción.
- id: RN-CAP-002
  name: Independent Approval Role Enforcement
  description: Approver of a CAPA action plan cannot be the assigned investigator
    or item assignee.
  enforcement: synchronous
  error_code: CAPA_APPROVER_INVALID
  message:
    en: CAPA action plan approver must be an independent quality role.
    es: El aprobador del plan de acción CAPA debe poseer un rol de calidad independiente.
- id: RN-CAP-003
  name: Mandatory Effectiveness Verification Grace Period
  description: Effectiveness verification cannot occur until at least 14 days after
    all action items are completed.
  enforcement: synchronous
  error_code: CAPA_VERIFICATION_PERIOD_TOO_SHORT
  message:
    en: Effectiveness verification requires a minimum 14-day observation period after
      action item completion.
    es: La verificación de efectividad requiere un periodo mínimo de observación de
      14 días tras la conclusión de las acciones.
- id: RN-CAP-004
  name: Overdue CAPA Escalation Alert
  description: Any CAPA investigation open beyond its targetCompletionDate must trigger
    an overdue alert notification.
  enforcement: automatic
  error_code: CAPA_OVERDUE_ALERT
  message:
    en: CAPA investigation is past its target completion date.
    es: La investigación CAPA ha superado su fecha objetivo de término.
```
