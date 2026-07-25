---
id: HOP-CAP-PKG-BCM-ORG-001
format: markdown_structured_payload
type: capability-package
name: Tenant Management Capability Package
version: 1.1.0
status: extended_customer_enablement_controls
---

# Tenant Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ORG-001
  type: capability-package
  name: Tenant Management Capability Package
  version: 1.1.0
  status: extended_customer_enablement_controls
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-22
  updated_date: 2026-07-24
  roadmap_group: COM-MOD-016
  extended_by_modules:
  - COM-MOD-012
  - COM-MOD-016
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ORG-001
  name:
    en: Tenant Management
    es: Gestión de Tenants y Habilitación de Clientes
  domain: DOM-01 Organization
  priority: High
  roadmap: MVP1
  dependency_profile: foundation
  bounded_context: organization-management
  primary_aggregate: Tenant
  aggregate_ref: AGG-017
scope:
  summary: 'Governs multi-tenant lifecycle management, tenant provisioning, customer
    onboarding workflows, initial tenant configuration, commercial launch readiness
    checkpoints, resource quota enforcement, database/schema isolation policies, and
    SaaS subscription status binding for Healthcare Operations Platform.

    '
  in_scope:
  - Tenant onboarding, customer initialization, provisioning, activation, suspension,
    and archive lifecycle.
  - Initial customer configuration management, setup guides, and commercial onboarding
    status.
  - Commercial launch readiness validation and operational evidence checkpoints.
  - Tenant-level quota definition (storage, API calls, active users, branch limits).
  - Tenant database/schema isolation policies (PostgreSQL native Row-Level Security
    parameters per TD-DB-004).
  - Tenant organization profile, branding customization, locale defaults, and subscription
    binding.
  - Tenant offboarding and data retention/purge workflow triggering.
  out_of_scope:
  - Individual user credential management (handled by BCM-PLT-001 IAM).
  - Fine-grained feature flag toggling (handled by BCM-PLT-002 Platform Configuration).
  - Invoicing and fiscal billing for SaaS subscription billing (handled by BCM-ATT-008).
roadmap:
  module: COM-MOD-016
  release: REL-003
  package_status: operational_governance_completed
  next_backlog_item: COM-MOD-016-COM-001
dependencies:
  required_capabilities:
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-008
  downstream_capabilities:
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-SVC-001
  - BCM-PER-001
  - BCM-ATT-001
  - BCM-LAB-001
product_surfaces:
  backend: required
  employee_portal: admin_required
  operations_console: required
  patient_portal: isolated
  doctor_portal: isolated
  mobile_app: isolated
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
