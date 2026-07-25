---
id: HOP-MOB-BCM-SVC-009
format: markdown_structured_payload
type: mobile-model
name: Price List Management Mobile Model
version: 0.1.0
status: not_required
---

# Price List Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-SVC-009
  type: mobile-model
  name: Price List Management Mobile Model
  version: 0.1.0
  status: not_required
  classification: editable_model
  capability: BCM-SVC-009
mobile_scope:
  status: not_required
  rationale: 'MVP-MOD-002 declares mobile_app not_required. Price list authoring is
    an internal finance and catalog activity. Published prices may later appear indirectly
    in patient-facing quotation or payment mobile flows delivered by revenue cycle
    modules.

    '
  deferred_to:
  - COM-MOD-009 Patient and Doctor Portals
  flows: []
  offline_expectations: none
```
