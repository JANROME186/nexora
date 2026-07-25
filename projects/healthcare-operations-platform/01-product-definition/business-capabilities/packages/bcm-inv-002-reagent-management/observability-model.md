---
id: HOP-OBS-BCM-INV-002
format: markdown_structured_payload
type: observability-model
name: Reagent Management Observability Model
version: 0.1.0
status: modeled
---

# Reagent Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-002
  type: observability-model
  name: Reagent Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-002
  depends_on_capability: BCM-PLT-006
logs:
- event: reagent_profile_assigned
  level: info
  fields:
  - inventoryItemId
  - reagentCategory
  - linkedTestDefinitionId
  correlation_id: inventoryItemId
metrics:
- name: reagent_profiles_assigned_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reagentCategory
traces:
- span: AssignReagentProfile
  child_spans:
  - ValidateItemTypeEligibility
  - ValidateTestDefinitionReference
audit_events:
- ReagentProfileAssigned
alerts: []
```
