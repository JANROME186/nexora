---
id: HOP-CAP-PKG-BCM-ORG-003
format: markdown_structured_payload
type: capability-package
name: Branch Management Capability Package
version: 1.0.0
status: validated
---

# Branch Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ORG-003
  type: capability-package
  name: Branch Management Capability Package
  version: 1.0.0
  status: validated
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-24
  roadmap_group: COM-MOD-016
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ORG-003
  name:
    en: Branch Management
    es: Gestión de Sucursales
  domain: DOM-01 Organization
  priority: High
  roadmap: MVP1
  dependency_profile: foundation
  bounded_context: organization-management
  primary_aggregate: Branch
  aggregate_ref: AGG-003
scope:
  summary: 'Governs physical branch location setup, operating schedules, reception
    and sample collection readiness, contact details, and operational status management
    for Healthcare Operations Platform.

    '
  in_scope:
  - Branch creation, physical address, and geo-location registration under a parent
    laboratory.
  - Branch operational capacity (reception windows, sample collection stations, appointment
    capacity).
  - Branch operating hours, holiday calendars, and emergency closure status.
  - Branch contact details, local manager assignment, and customer communication info.
  - Branch status lifecycle (CONFIGURATION, OPERATIONAL, MAINTENANCE, SUSPENDED, CLOSED).
  out_of_scope:
  - Parent laboratory identity and sanitary license management (handled by BCM-ORG-002
    Laboratory Management).
  - Detailed appointment slot scheduling algorithms (handled by BCM-ATT-001 Appointment
    Scheduling).
  - Cash register assignment per cashier (handled by BCM-ATT-005 Cashier Operations).
roadmap:
  module: COM-MOD-016
  release: REL-003
  package_status: operational_governance_completed
  next_backlog_item: COM-MOD-016-COM-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-008
  downstream_capabilities:
  - BCM-ATT-001
  - BCM-ATT-003
  - BCM-ATT-005
  - BCM-LAB-002
product_surfaces:
  backend: required
  employee_portal: admin_required
  operations_console: required
  patient_portal: read_only_info
  doctor_portal: read_only_info
  mobile_app: read_only_info
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
