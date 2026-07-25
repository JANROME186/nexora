---
id: HOP-API-SRC-BCM-QLT-005
format: markdown_structured_payload
type: openapi-source
name: Maintenance Management API Source Model
version: 0.1.0
status: modeled
---

# Maintenance Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-QLT-005
  type: openapi-source
  name: Maintenance Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-005
api:
  base_path: /api/quality/maintenance
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - quality.maintenance.manage
    - quality.maintenance.read
resources:
- name: MaintenanceEvent
  operations:
  - id: recordMaintenance
    method: POST
    path: /items/{inventoryItemId}/maintenance
    scopes:
    - quality.maintenance.manage
    generatable: false
    custom_reason: Delegated append mutation with cross-capability itemType validation
      and event publication.
  - id: completeMaintenance
    method: POST
    path: /maintenance/{maintenanceEventId}/complete
    scopes:
    - quality.maintenance.manage
    generatable: false
    custom_reason: Delegated mutation with a temporal guard and event publication.
  - id: listMaintenanceEvents
    method: GET
    path: /items/{inventoryItemId}/maintenance
    scopes:
    - quality.maintenance.read
    generatable: true
schemas_source:
- MaintenanceEvent
error_model:
  standard: rfc7807
  code_field: 'Every error response carries a first-class `code` string field, consistent
    with the COM-MOD-010 module convention.

    '
  domain_errors:
  - code: MAINTENANCE_ITEM_TYPE_NOT_ELIGIBLE
    maps_to_rule: RN-001
  - code: MAINTENANCE_COMPLETED_BEFORE_STARTED
    maps_to_rule: RN-004
  - code: MAINTENANCE_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
