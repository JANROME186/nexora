---
id: HOP-MOB-BCM-PER-002
format: markdown_structured_payload
type: mobile-model
name: Patient Management Mobile Model
version: 0.1.0
status: deferred
---

# Patient Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-PER-002
  type: mobile-model
  name: Patient Management Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-PER-002
mobile_scope:
  status: required
  flows:
  - id: MOB-FLOW-PAT-001
    name: Patient Profile View
    description: Read and edit patient contact and localization details.
    screens:
    - MobileProfileSummary
    - MobileContactEditForm
  offline_expectations: read_only_cached_profile
```
