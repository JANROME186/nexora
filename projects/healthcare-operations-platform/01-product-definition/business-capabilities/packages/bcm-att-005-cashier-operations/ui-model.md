---
id: HOP-UI-BCM-ATT-005
format: markdown_structured_payload
type: ui-model
name: Cashier Operations UI Model
version: 0.1.0
status: modeled
---

# Cashier Operations Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-ATT-005
  type: ui-model
  name: Cashier Operations UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-005
employee_portal:
  required: true
  navigation_group: Revenue Cycle
  screens:
  - id: SCR-CASH-001
    name: Cash session console
    purpose: Open, monitor and close the active branch cash session.
    actions:
    - open_session
    - close_session
    - view_variance
  - id: SCR-CASH-002
    name: Sale worklist
    purpose: List payable, partially paid and paid sales by branch and date.
    actions:
    - create_sale_from_order
    - create_sale_from_quotation
    - view_sale
  - id: SCR-CASH-003
    name: Payment registration
    purpose: Register payment allocations and show outstanding balance.
    actions:
    - register_payment
    - cancel_sale
    - request_billing
states:
  loading: required
  empty: required
  error: required
  confirmation: required_for_cancel_and_close
  success: required
i18n:
  visible_text_externalization_required: true
  repeated_message_catalog_required: true
```
