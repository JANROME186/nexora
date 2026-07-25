---
id: HOP-OBS-BCM-QLT-004
format: markdown_structured_payload
type: observability-model
name: Equipment Management Observability Model
version: 0.1.0
status: modeled
---

# Equipment Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-QLT-004
  type: observability-model
  name: Equipment Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
  depends_on_capability: BCM-PLT-006
logs:
- event: equipment_profile_set
  level: info
  fields:
  - inventoryItemId
  - assetTag
  correlation_id: inventoryItemId
- event: equipment_availability_changed
  level: info
  fields:
  - inventoryItemId
  - previousStatus
  - newStatus
  - reasonCode
  correlation_id: inventoryItemId
metrics:
- name: equipment_items_registered_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: equipment_out_of_service_gauge
  type: gauge
  labels:
  - tenantId
  - branchId
traces:
- span: SetEquipmentProfile
  child_spans:
  - ValidateItemTypeEligibility
- span: ChangeEquipmentAvailability
  child_spans:
  - ValidateTerminalStateGuard
audit_events:
- EquipmentProfileSet
- EquipmentAvailabilityChanged
alerts:
- name: EquipmentOutOfServiceProlonged
  condition: equipment_out_of_service_gauge remains above zero beyond a configured
    window
  severity: high
```
