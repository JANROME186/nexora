---
id: HOP-PRM-BCM-QLT-002
format: markdown_structured_payload
type: permissions
name: External Quality Controls Permissions
version: 0.1.0
status: modeled
---

# External Quality Controls Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-QLT-002
  type: permissions
  name: External Quality Controls Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
permissions:
- code: quality.externalcontrol.read
  name:
    en: View External Quality Controls
    es: Ver Controles de Calidad Externos
  description: Grants view access to EQA survey entries, z-score calculations, and
    provider evaluation reports.
  actions:
  - listExternalQualityEvaluations
  - getExternalQualityEvaluation
- code: quality.externalcontrol.manage
  name:
    en: Manage External Quality Controls
    es: Gestionar Controles de Calidad Externos
  description: Grants permission to register EQA surveys, enter measurement results,
    and record provider scoring.
  actions:
  - createExternalQualityEvaluation
  - scoreExternalQualityEvaluation
```
