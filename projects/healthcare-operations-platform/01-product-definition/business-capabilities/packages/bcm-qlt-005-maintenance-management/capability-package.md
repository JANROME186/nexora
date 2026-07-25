---
id: HOP-CAP-PKG-BCM-QLT-005
format: markdown_structured_payload
type: capability-package
name: Maintenance Management Capability Package
version: 0.1.0
status: modeled
---

# Maintenance Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-QLT-005
  type: capability-package
  name: Maintenance Management Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-20
  roadmap_group: COM-MOD-010
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-QLT-005
  name:
    en: Maintenance Management
    es: Mantenimiento
  domain: DOM-09 Quality
  priority: High
  roadmap: MVP2
  dependency_profile: inventory_quality
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001; maintenanceRecord
    delegated to this capability)
  process_ref: not_yet_defined_in_HRP-001
scope:
  summary: 'Records preventive and corrective maintenance events for equipment-classified
    InventoryItem records, including downtime and next-scheduled-maintenance tracking.
    Holds delegated, field-scoped mutation authority over InventoryItem.maintenanceRecord
    only. Publishes MaintenanceScheduled and MaintenanceCompleted for BCM-QLT-004
    Equipment Management to react to (out_of_service during the maintenance window,
    back to available on completion) rather than writing equipmentProfile itself.

    '
  in_scope:
  - 'Maintenance event recording: maintenanceType, description, downtimeMinutes, nextScheduledAt.'
  - Maintenance due-date monitoring.
  out_of_scope:
  - InventoryItem creation and core identity fields (BCM-INV-001).
  - equipmentProfile mutation (BCM-QLT-004).
  - Calibration event recording (BCM-QLT-003).
roadmap:
  module: COM-MOD-010
  release: REL-002
  package_status: module_closed
  next_backlog_item: none (module closed; see COM-MOD-011-DEF for the next roadmap
    module)
  paused_functional_backlog_item: null
dependencies:
  required_capabilities:
  - BCM-INV-001
  - BCM-QLT-004
  - BCM-SVC-002
  - BCM-SVC-007
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-009
  downstream_capabilities:
  - BCM-QLT-007
  - BCM-QLT-004
  upstream_contexts:
  - inventory-procurement
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: not_required
  doctor_portal: not_required
  mobile_app: not_required
required_artifacts:
- capability-package.md
- business-model.md
- business-rules.md
- processes.md
- events.md
- openapi-source.md
- permissions.md
- ui-model.md
- mobile-model.md
- test-model.md
- observability-model.md
- generation-plan.md
- traceability.md
- README.md
```
