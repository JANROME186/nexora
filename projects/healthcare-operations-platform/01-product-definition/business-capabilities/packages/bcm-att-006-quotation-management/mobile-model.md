---
id: HOP-MOB-BCM-ATT-006
format: markdown_structured_payload
type: mobile-model
name: Quotation Management Mobile Model
version: 0.1.0
status: not_applicable
---

# Quotation Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-ATT-006
  type: mobile-model
  name: Quotation Management Mobile Model
  version: 0.1.0
  status: not_applicable
  classification: editable_model
  capability: BCM-ATT-006
mobile_scope:
  status: not_required
  rationale: 'MVP-MOD-004 declares mobile_app not_required for Quotation Management.
    Quotations are produced by front-desk staff today; a future patient-facing quotation
    request surface is more likely to arrive through the public website (COM-MOD-011)
    or patient portal (COM-MOD-009) than a dedicated mobile flow, and is not committed
    in this package.

    '
  deferred_to:
  - COM-MOD-011 Public Website and Digital Growth
  flows: []
  offline_expectations: none
```
