---
id: HOP-CAP-PKG-BCM-ORG-002
format: markdown_structured_payload
type: capability-package
name: Laboratory Management Capability Package
version: 1.0.0
status: validated
---

# Laboratory Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ORG-002
  type: capability-package
  name: Laboratory Management Capability Package
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
  id: BCM-ORG-002
  name:
    en: Laboratory Management
    es: Gestión de Laboratorios
  domain: DOM-01 Organization
  priority: High
  roadmap: MVP1
  dependency_profile: foundation
  bounded_context: organization-management
  primary_aggregate: Laboratory
  aggregate_ref: AGG-002
scope:
  summary: 'Governs laboratory organization identity, multi-branch laboratory registration,
    sanitary license tracking, clinical director assignment, laboratory operating
    hours, and operational capability enablement for Healthcare Operations Platform.

    '
  in_scope:
  - Laboratory registration, profile management, and brand identity configuration.
  - Sanitary license registration, regulatory accreditation tracking, and expiration
    alerts.
  - Medical/Clinical director assignment and professional credential verification.
  - Laboratory-level operational defaults and capability toggles.
  - Multi-branch laboratory hierarchy binding under parent tenant.
  out_of_scope:
  - Tenant-level SaaS quotas and RLS database isolation (handled by BCM-ORG-001 Tenant
    Management).
  - Individual branch physical location and room management (handled by BCM-ORG-003
    Branch Management).
  - Diagnostic service catalog definitions (handled by BCM-SVC-001).
roadmap:
  module: COM-MOD-016
  release: REL-003
  package_status: operational_governance_completed
  next_backlog_item: COM-MOD-016-COM-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-008
  downstream_capabilities:
  - BCM-ORG-003
  - BCM-PER-002
  - BCM-SVC-001
  - BCM-LAB-001
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
