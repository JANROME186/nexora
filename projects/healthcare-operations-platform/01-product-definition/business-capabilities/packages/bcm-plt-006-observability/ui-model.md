---
id: HOP-UI-BCM-PLT-006
format: markdown_structured_payload
type: ui-model
name: Observability UI Model
version: 1.0.0
---

# Observability Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-006
  type: ui-model
  name: Observability UI Model
  version: 1.0.0
screens:
- id: SCR-OBS-001
  name: Operational Monitoring Dashboard
  surface: employee_portal / operations_console
  route: /admin/observability
  components:
  - ServiceHealthGrid
  - PrometheusMetricsChart
  - ActiveAlertsBanner
```
