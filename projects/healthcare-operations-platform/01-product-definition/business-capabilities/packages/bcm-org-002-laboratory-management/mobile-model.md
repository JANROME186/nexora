---
id: HOP-MOB-BCM-ORG-002
format: markdown_structured_payload
type: mobile-model
name: Laboratory Management Mobile Model
version: 1.0.0
---

# Laboratory Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-ORG-002
  type: mobile-model
  name: Laboratory Management Mobile Model
  version: 1.0.0
views:
- id: MOB-LAB-001
  name: LaboratoryInfoCard
  surface: mobile_app
  scope: read_only_info
  description: Displays primary laboratory brand info, sanitary license verification
    badge, and contact details in mobile screens.
offline_support:
  read_cached: true
  mutation_allowed: false
```
