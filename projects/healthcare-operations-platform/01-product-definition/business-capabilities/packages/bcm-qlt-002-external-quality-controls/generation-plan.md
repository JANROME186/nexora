---
id: HOP-GEN-BCM-QLT-002
format: markdown_structured_payload
type: generation-plan
name: External Quality Controls Generation Plan
version: 0.1.0
status: modeled
---

# External Quality Controls Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-QLT-002
  type: generation-plan
  name: External Quality Controls Generation Plan
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
targets:
  backend:
    module_name: externalqualitycompliance
    package_path: com.nexora.hop.platformfoundation.externalqualitycompliance.externalqualitycontrols
    artifacts:
    - entity: ExternalQualityEvaluationEntity
    - repository: ExternalQualityEvaluationRepository
    - service: ExternalQualityControlService
    - controller: ExternalQualityControlController
  frontend:
    surface: employee_portal
    api_facade: src/api/externalQualityApi.ts
    screen_components:
    - src/components/screens/ExternalQualityControlsScreen.tsx
  schema:
    migration_file: db/external-quality-and-compliance/schema.sql
    table_name: quality_external_evaluations
```
