---
id: HOP-MOB-BCM-PER-001
format: markdown_structured_payload
type: mobile-model
name: Person Management Mobile Model
version: 0.1.0
status: not_required
---

# Person Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-PER-001
  type: mobile-model
  name: Person Management Mobile Model
  version: 0.1.0
  status: not_required
  classification: editable_model
  capability: BCM-PER-001
mobile_scope:
  status: not_required
  rationale: 'Person Management is an internal shared master-data capability. Mobile
    surfaces do not consume cross-tenant person search directly and access people
    data through the Patient (BCM-PER-002) or Doctor (BCM-PER-003) specialized capabilities.

    '
  deferred_to:
  - COM-MOD-009 Patient and Doctor Portals
  flows: []
  offline_expectations: none
```
