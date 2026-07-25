---
id: HOP-UI-BCM-INV-001
format: markdown_structured_payload
type: ui-model
name: Product Catalog UI Model
version: 0.1.0
status: modeled
---

# Product Catalog Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-INV-001
  type: ui-model
  name: Product Catalog UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-001
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
- id: SCR-CAT-001-01
  name: Inventory Item Catalog
  route: /admin/inventory/items
  purpose: List, register, update and discontinue InventoryItem records.
  components:
  - DataTable
  - InventoryItemForm
  - StatusBadge
  generatable: true
states:
- active
- inactive
- discontinued
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: inventory.catalog.*
  note: 'New user-facing strings must be registered under the inventory.catalog.*
    message-key namespace in the backend MessageSource and frontend locale catalogs
    established by HOP-ENT-FOUND-001/HOP-QA-ALIGN-005, not hardcoded.

    '
rationale: 'BCM-INV-001 is an internal back-office catalog capability; the employee
  portal surface is required (module-level product_surfaces: employee_portal required)
  with no patient/doctor visibility.

  '
```
