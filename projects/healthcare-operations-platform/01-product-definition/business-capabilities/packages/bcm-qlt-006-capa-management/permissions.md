---
id: HOP-PRM-BCM-QLT-006
format: markdown_structured_payload
type: permissions
name: CAPA Management Permissions
version: 0.1.0
status: modeled
---

# Capa Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-QLT-006
  type: permissions
  name: CAPA Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-006
permissions:
- code: quality.capa.read
  name:
    en: View CAPA Investigations
    es: Ver Investigaciones CAPA
  description: Grants permission to view CAPA lists, RCA details, action plans, and
    effectiveness reports.
  actions:
  - listCapaInvestigations
  - getCapaInvestigation
- code: quality.capa.manage
  name:
    en: Manage CAPA Investigations
    es: Gestionar Investigaciones CAPA
  description: Grants permission to initiate CAPAs, record RCA, add action plan items,
    and complete action items.
  actions:
  - createCapaInvestigation
  - recordRootCauseAnalysis
- code: quality.capa.approve
  name:
    en: Approve CAPA Action Plans & Verify Effectiveness
    es: Aprobar Planes CAPA y Verificar Efectividad
  description: Grants administrative quality permission to approve action plans and
    sign off on effectiveness verification.
  actions:
  - approveCapaActionPlan
  - verifyCapaEffectiveness
```
