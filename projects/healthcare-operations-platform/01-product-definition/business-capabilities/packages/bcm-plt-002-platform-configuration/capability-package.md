---
id: HOP-CAP-PKG-BCM-PLT-002
format: markdown_structured_payload
type: capability-package
name: Platform Configuration Capability Package
version: 1.1.0
status: extended_customer_enablement_controls
---

# Platform Configuration Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-PLT-002
  type: capability-package
  name: Platform Configuration Capability Package
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
  id: BCM-PLT-002
  name:
    en: Platform Configuration
    es: Configuración de Plataforma y Feature Flags
  domain: DOM-10 Platform
  priority: High
  roadmap: MVP1
  dependency_profile: foundation
  bounded_context: platform-operations
  primary_aggregate: PlatformConfiguration
scope:
  summary: 'Governs global platform parameters, system environment profiles, tenant
    feature flag toggles, customer onboarding setup guides, configuration templates
    (Starter, Professional, Enterprise defaults), dynamic PII data masking policy
    overrides, and commercial launch feature controls for Healthcare Operations Platform.

    '
  in_scope:
  - Global platform parameters and tenant-specific configuration key-value overrides.
  - Customer onboarding wizard feature flags (customer_onboarding_wizard_enabled,
    customer_self_configuration_enabled, commercial_demo_mode_enabled).
  - Initial customer configuration templates and export/import parameter bundles.
  - Feature flag evaluation engine with percentage rollouts, tenant rules, and targeted
    evaluation.
  - System environment configuration profiles.
  - Dynamic data masking configuration for document/credential read models (addressing
    TD-BE-008).
  - Maintenance mode activation and customer notification banner configuration.
  out_of_scope:
  - User role and permission definitions (handled by BCM-PLT-001 IAM).
  - Hardening of external API endpoints (handled by BCM-PLT-005 API Management).
roadmap:
  module: COM-MOD-016
  release: REL-003
  package_status: operational_governance_completed
  next_backlog_item: COM-MOD-016-COM-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-PLT-001
  optional_capabilities:
  - BCM-PLT-006
  - BCM-PLT-007
product_surfaces:
  backend: required
  employee_portal: admin_required
  operations_console: required
  patient_portal: read_only_config
  doctor_portal: read_only_config
  mobile_app: read_only_config
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
