---
id: HOP-MOB-BCM-RES-001
format: markdown_structured_payload
type: mobile-model
name: Result Management Mobile Model
version: 0.1.0
status: deferred
---

# Result Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-RES-001
  type: mobile-model
  name: Result Management Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-RES-001
mobile_scope:
  status: not_required
  rationale: 'MVP-MOD-007 declares mobile_app as result_view_required at the module
    level, but that scope is the patient/doctor released-result view owned by BCM-RES-004/BCM-RES-005,
    not the internal staff search facade modeled here. Internal result management
    is an employee_portal-only surface.

    '
  deferred_to: []
  flows: []
  offline_expectations: none
```
