---
id: HOP-PROC-BCM-INV-002
format: markdown_structured_payload
type: processes
name: Reagent Management Processes
version: 0.1.0
status: modeled
---

# Reagent Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-INV-002
  type: processes
  name: Reagent Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-002
actors:
- id: catalog-manager
  actor_ref: ACT-010
  name: Catalog Manager
  source: ACM-001
processes:
- id: PRC-RGT-002-01
  name: Assign reagent profile
  actor: catalog-manager
  trigger: A reagent- or consumable-type InventoryItem needs test-consumption classification.
  commands:
  - AssignReagentProfile
  preconditions:
  - InventoryItem exists, is active and itemType is reagent or consumable.
  - Actor holds inventory.reagent.manage scope.
  steps:
  - Validate linkedTestDefinitionId, if present, against the published catalog.
  - Invoke AssignReagentProfile on InventoryItem.reagentProfile.
  - Publish ReagentProfileAssigned.
  outcome: ReagentProfileAssigned
  rules:
  - RN-001
  - RN-002
  - RN-004
commands:
- name: AssignReagentProfile
  generatable: false
  custom_reason: Delegated single-field mutation on the shared InventoryItem aggregate
    with cross-capability itemType validation.
```
