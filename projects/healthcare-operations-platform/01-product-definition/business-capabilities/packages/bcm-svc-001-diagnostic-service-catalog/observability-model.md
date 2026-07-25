---
id: HOP-OBS-BCM-SVC-001
format: markdown_structured_payload
type: observability-model
name: Diagnostic Service Catalog Observability Model
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-001
  type: observability-model
  name: Diagnostic Service Catalog Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
  depends_on_capability: BCM-PLT-006
logs:
- event: service_created
  level: info
  fields:
  - serviceId
  - tenantId
  - actorId
- event: service_published
  level: info
  fields:
  - serviceId
  - version
  - actorId
- event: service_publish_rejected
  level: warn
  fields:
  - serviceId
  - reasonCode
metrics:
- name: catalog_services_total
  type: gauge
  labels:
  - tenantId
  - status
- name: catalog_service_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_service_write_latency_ms
  type: histogram
traces:
- span: CreateDiagnosticService
- span: PublishDiagnosticService
  child_spans:
  - ValidateComponentPublication
  - FreezeServiceSnapshot
audit_events:
- DiagnosticServiceCreated
- DiagnosticServicePublished
- DiagnosticServiceDeprecated
alerts:
- name: HighServicePublishFailureRate
  condition: catalog_service_publish_failures_total rate exceeds threshold
  severity: warning
```
