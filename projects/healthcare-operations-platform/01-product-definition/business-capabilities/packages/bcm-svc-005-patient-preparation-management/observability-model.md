---
id: HOP-OBS-BCM-SVC-005
format: markdown_structured_payload
type: observability-model
name: Patient Preparation Management Observability Model
version: 0.1.0
status: modeled
---

# Patient Preparation Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-005
  type: observability-model
  name: Patient Preparation Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-005
  depends_on_capability: BCM-PLT-006
logs:
- event: preparation_created
  level: info
  fields:
  - preparationId
  - tenantId
  - actorId
- event: preparation_published
  level: info
  fields:
  - preparationId
  - version
  - actorId
- event: preparation_assign_rejected
  level: warn
  fields:
  - preparationId
  - targetRefId
  - reasonCode
metrics:
- name: catalog_preparations_total
  type: gauge
  labels:
  - tenantId
  - status
  - category
- name: catalog_preparation_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_preparation_write_latency_ms
  type: histogram
traces:
- span: CreatePreparation
- span: PublishPreparation
  child_spans:
  - ValidateLocalization
  - FreezePreparationSnapshot
audit_events:
- PreparationCreated
- PreparationAssigned
- PreparationPublished
- PreparationDeprecated
alerts:
- name: HighPreparationPublishFailureRate
  condition: catalog_preparation_publish_failures_total rate exceeds threshold
  severity: warning
```
