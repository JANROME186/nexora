---
id: HOP-MOB-BCM-LAB-009
format: markdown_structured_payload
type: mobile-model
name: Medical Validation Mobile Model
version: 0.1.0
status: deferred
---

# Medical Validation Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-LAB-009
  type: mobile-model
  name: Medical Validation Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-LAB-009
mobile_scope:
  status: not_required
  rationale: 'MVP-MOD-006 declares mobile_app not_required for medical validation.
    A future doctor-facing mobile validation surface, if ever built, would be scoped
    under COM-MOD-009 Patient and Doctor Portals with its own licensed-authority controls,
    not under this module.

    '
  deferred_to:
  - COM-MOD-009 Patient and Doctor Portals (not yet scheduled for medical validation)
  flows: []
  offline_expectations: none
```
