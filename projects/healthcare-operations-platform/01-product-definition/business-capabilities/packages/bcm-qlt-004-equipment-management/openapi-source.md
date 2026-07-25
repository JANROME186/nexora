---
id: HOP-API-SRC-BCM-QLT-004
format: markdown_structured_payload
type: openapi-source
name: Equipment Management API Source Model
version: 0.1.0
status: modeled
---

# Equipment Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-QLT-004
  type: openapi-source
  name: Equipment Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
api:
  base_path: /api/quality/equipment
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - quality.equipment.manage
    - quality.equipment.read
resources:
- name: EquipmentProfile
  operations:
  - id: setEquipmentProfile
    method: POST
    path: /items/{inventoryItemId}/equipment-profile
    scopes:
    - quality.equipment.manage
    generatable: false
    custom_reason: Delegated single-field mutation with cross-capability itemType
      validation.
  - id: changeEquipmentAvailability
    method: POST
    path: /items/{inventoryItemId}/availability
    scopes:
    - quality.equipment.manage
    generatable: false
    custom_reason: Manual and event-driven state transition with a terminal-state
      guard.
  - id: getEquipmentProfile
    method: GET
    path: /items/{inventoryItemId}/equipment-profile
    scopes:
    - quality.equipment.read
    generatable: true
schemas_source:
- EquipmentAvailabilityChange
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: EQUIPMENT_ITEM_TYPE_NOT_ELIGIBLE
    maps_to_rule: RN-001
  - code: EQUIPMENT_RETIRED_TRANSITION_FORBIDDEN
    maps_to_rule: RN-002
  - code: EQUIPMENT_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
