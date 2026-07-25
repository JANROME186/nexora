---
id: HOP-MOB-BCM-SVC-005
format: markdown_structured_payload
type: mobile-model
name: Patient Preparation Management Mobile Model
version: 0.1.0
status: deferred
---

# Patient Preparation Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-SVC-005
  type: mobile-model
  name: Patient Preparation Management Mobile Model
  version: 0.1.0
  status: deferred
  classification: editable_model
  capability: BCM-SVC-005
mobile_scope:
  status: deferred
  rationale: 'MVP-MOD-002 declares mobile_app not_required for catalog authoring.
    However patient preparation guidance is inherently patient-facing and is expected
    to appear read-only in the patient mobile app when patient channels are delivered.
    Mobile is therefore deferred rather than not_required.

    '
  deferred_to:
  - COM-MOD-009 Patient and Doctor Portals
  - MVP-MOD-004 Front Desk and Care Delivery
  planned_flows:
  - id: MOB-SVC-005-01
    name: View appointment preparation instructions
    status: planned
    surface: patient_app
    read_only: true
  offline_expectations:
  - Published preparation guidance may be cached read-only for an upcoming appointment.
```
