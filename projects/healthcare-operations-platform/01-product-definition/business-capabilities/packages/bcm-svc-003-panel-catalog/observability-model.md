---
id: HOP-OBS-BCM-SVC-003
format: markdown_structured_payload
type: observability-model
name: Panel Catalog Observability Model
version: 0.1.0
status: modeled
---

# Panel Catalog Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-003
  type: observability-model
  name: Panel Catalog Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-003
  depends_on_capability: BCM-PLT-006
logs:
- event: panel_created
  level: info
  fields:
  - panelId
  - tenantId
  - actorId
- event: panel_published
  level: info
  fields:
  - panelId
  - version
  - actorId
- event: panel_publish_rejected
  level: warn
  fields:
  - panelId
  - reasonCode
metrics:
- name: catalog_panels_total
  type: gauge
  labels:
  - tenantId
  - status
- name: catalog_panel_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_panel_write_latency_ms
  type: histogram
traces:
- span: CreatePanelDefinition
- span: PublishPanelDefinition
  child_spans:
  - ValidateMemberPublication
  - FreezePanelSnapshot
audit_events:
- PanelDefinitionCreated
- PanelDefinitionPublished
- PanelDefinitionDeprecated
alerts:
- name: HighPanelPublishFailureRate
  condition: catalog_panel_publish_failures_total rate exceeds threshold
  severity: warning
```
