---
id: HOP-UI-BCM-PLT-002
format: markdown_structured_payload
type: ui-model
name: Platform Configuration UI Model
version: 1.0.0
---

# Platform Configuration Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-002
  type: ui-model
  name: Platform Configuration UI Model
  version: 1.0.0
screens:
- id: SCR-CFG-001
  name: Feature Flags & Configuration Console
  surface: employee_portal / operations_console
  route: /admin/config
  components:
  - FeatureFlagToggleTable
  - RolloutPercentageSlider
  - PiiMaskingConfigForm
```
