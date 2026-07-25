---
id: HOP-CAP-PKG-BCM-PLT-001
format: markdown_structured_payload
type: capability-package
name: Identity and Access Management Capability Package
version: 1.1.0
status: validated
---

# Identity And Access Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-PLT-001
  type: capability-package
  name: Identity and Access Management Capability Package
  version: 1.1.0
  status: validated
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-19
  updated_date: 2026-07-22
  roadmap_group: COM-MOD-009
  extended_by_modules:
  - COM-MOD-012
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-PLT-001
  name:
    en: Identity and Access Management
    es: Gestión de Identidad y Accesos
  domain: DOM-10 Platform
  priority: Critical
  roadmap: MVP1
  dependency_profile: foundation
  bounded_context: identity-access
  primary_aggregate: UserAccount
  aggregate_ref: AGG-004
scope:
  summary: 'Governs user authentication, session management, multi-factor authentication
    (MFA), service account token governance, fine-grained action permissions (domain.resource.action.scope
    per TD-IAM-002), and SaaS operational authorization for all HOP channels and platform
    operations.

    '
  in_scope:
  - User registration, authentication, login, logout, MFA verification, and session
    lifecycle.
  - Password hashing, verification, and credential management.
  - Service account token generation and scope policy enforcement for automated SaaS
    operations.
  - Fine-grained permission model (`domain.resource.action.scope`) and role-to-permission
    mapping (addressing TD-IAM-002).
  - Support-assisted access (delegated login support for troubleshooting) with strict
    audit checks.
  - es-MX / en-US localization of IAM errors and interface keys.
  out_of_scope:
  - Business capabilities logic for downstream clinical or billing actions.
  - PDF report storage or generation (BCM-RES-002, BCM-PLT-008).
  - Email/SMS provider integrations (handled via BCM-PLT-003 Notification Management).
roadmap:
  module: COM-MOD-009
  release: REL-002
  package_status: module_closed
  next_backlog_item: COM-MOD-012-OPS-001
dependencies:
  required_capabilities:
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-002
  - BCM-PLT-003
  - BCM-PLT-006
  downstream_capabilities:
  - BCM-PER-002
  - BCM-PER-003
  - BCM-ATT-001
  - BCM-RES-004
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: required
  doctor_portal: required
  mobile_app: required
  operations_console: required
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
