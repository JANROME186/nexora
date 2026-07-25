---
id: HOP-UI-BCM-INV-005
format: markdown_structured_payload
type: ui-model
name: Stock Entries UI Model
version: 0.1.0
status: modeled
---

# Stock Entries Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-INV-005
  type: ui-model
  name: Stock Entries UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-005
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: partial
  patient_portal:
    status: not_required
    generatable: not_applicable
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-SEN-005-01
  name: Stock Receipts
  route: /admin/inventory/stock-entries
  purpose: Record a manual or purchase-order-linked stock receipt and review receipt
    history.
  components:
  - DataTable
  - StockReceiptForm
  - PurchaseOrderLinePicker
  generatable: partial
  custom_reason: Receipt confirmation invokes the custom ApplyStockReceipt command
    (RN-002, RN-003).
states:
- recorded
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: inventory.entries.*
  note: New user-facing strings must be registered under the inventory.entries.* message-key
    namespace, not hardcoded.
rationale: 'Stock receipt is a warehouse/receiving-desk task performed from the employee
  portal, with a mobile capture option deferred per mobile-model.md.

  '
```
