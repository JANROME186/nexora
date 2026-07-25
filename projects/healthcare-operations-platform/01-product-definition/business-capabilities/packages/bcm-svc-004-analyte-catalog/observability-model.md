---
id: HOP-OBS-BCM-SVC-004
format: markdown_structured_payload
type: observability-model
name: Analyte Catalog Observability Model
version: 0.1.0
status: modeled
---

# Analyte Catalog Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-004
  type: observability-model
  name: Analyte Catalog Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
  depends_on_capability: BCM-PLT-006
logs:
- event: analyte_created
  level: info
  fields:
  - analyteId
  - tenantId
  - actorId
- event: analyte_published
  level: info
  fields:
  - analyteId
  - version
  - actorId
- event: analyte_revised
  level: info
  fields:
  - analyteId
  - previousVersion
  - newVersion
  - dataTypeChanged
metrics:
- name: catalog_analytes_total
  type: gauge
  labels:
  - tenantId
  - status
  - resultDataType
- name: catalog_analyte_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_analyte_write_latency_ms
  type: histogram
traces:
- span: CreateAnalyte
- span: PublishAnalyte
  child_spans:
  - ValidateConstraints
  - FreezeAnalyteSnapshot
  - FlagDependents
audit_events:
- AnalyteCreated
- AnalyteDefinitionPublished
- AnalyteDefinitionRevised
- AnalyteDeprecated
alerts:
- name: HighAnalytePublishFailureRate
  condition: catalog_analyte_publish_failures_total rate exceeds threshold
  severity: warning
```
