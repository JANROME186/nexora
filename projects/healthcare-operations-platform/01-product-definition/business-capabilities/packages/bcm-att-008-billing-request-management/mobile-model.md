---
id: HOP-MOB-BCM-ATT-008
format: markdown_structured_payload
type: mobile-model
name: Billing Request Management Mobile Model
version: 0.1.0
status: modeled
---

# Billing Request Management Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-ATT-008
  type: mobile-model
  name: Billing Request Management Mobile Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-008
mobile_app:
  status: payment_receipt_later
  rationale: 'Employee portal owns operational billing request execution in MVP-MOD-005.
    Patient mobile access to issued fiscal document status is deferred to payment
    receipt/history surfaces.

    '
  future_screens:
  - id: MOB-BILL-001
    name: Invoice request status
    actor: patient
    depends_on: patient_portal_payment_history
offline_support:
  required_now: false
  future_consideration: receipt_status_cache_only
```
