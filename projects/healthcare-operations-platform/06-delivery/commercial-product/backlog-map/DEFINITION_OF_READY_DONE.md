---
artifact:
  id: HOP-COM-BACKLOG-DOR-DOD
  type: backlog-definition-gates
  status: active
  optimization: atomic_context
---

# HOP Definition Of Ready And Done

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
global_definition_of_ready:
- BUSINESS_REQUIREMENT.md exists and remains requester-supplied source material.
- Required framework and project source artifacts can be loaded from SOURCE_OF_TRUTH.md.
- Target module appears in this backlog and has all dependencies completed or explicitly mocked.
- Capabilities are mapped to BCM-001 and dependency profile exists in BCM-002.
- Bounded context and aggregate ownership are known.
- API surface classification is defined as public, internal, partner or system.
- Security roles, audit events, data retention and privacy expectations are documented.
- Web, mobile and portal surfaces are classified as in-scope, deferred or not applicable.
- Tests, traceability and closeout evidence expected for the module are defined before implementation.
- Capability Package models exist for every capability included in the target roadmap group.
- Generation plan identifies generated outputs and custom implementation points.
- Marketplace-ready capabilities apply the Product Marketplace Standard and declare offer, entitlement, installation and compatibility
  models.
global_definition_of_done:
- Capability Package required artifacts exist in YAML and Markdown when applicable.
- Generated outputs trace to source models.
- Backend implementation passes generated and custom unit, integration and contract tests relevant to the capability.
- Web implementation passes generated and custom typecheck, automated tests and build.
- Mobile or portal implementation passes generated and custom typecheck, automated tests and build when in scope.
- Database migrations are reversible where practical and documented when not reversible.
- Security and audit rules are implemented and validated.
- OpenAPI contracts are updated and remain compatible or have migration notes.
- Traceability links capabilities, requirements, APIs, UI, domain events and tests.
- QA evidence is written under 08-qa.
- Security quality evidence is written under 08-qa/security-quality/<backlog-item-id>/ for code-changing work.
- Every code-changing backlog item reduces or materially improves technical debt before feature work when debt exists; the
  amount of debt burn-down must increase as HOP approaches release readiness.
- Every applicable delivered stack targets at least 80 percent line coverage.
- If a stack is below 80 percent, its previous measured coverage is the minimum allowed baseline for the next iteration.
- If a changed stack remains below 80 percent, each relevant iteration must target a 3 to 5 percentage point line-coverage
  improvement; smaller improvements require explicit justification, maximum meaningful in-scope tests and immediate coverage
  debt.
- Enterprise foundations must be satisfied before customer-facing portal/app expansion continues: es-MX/en-US localization,
    IAM permission mapping, dynamic menus/actions, login/session context, product database deliverables, UX/UI design baseline,
    code documentation, persistence architecture and OpenAPI/contract-first generation review.
- HOP cannot be marked commercially complete or GA-ready while any technical debt remains open or any applicable stack is
  below 80 percent line coverage.
- PROJECT_STATE.md and SOURCE_OF_TRUTH.md are updated.
- Verifiable closure audit passed before any backlog item is marked closed: YAML parse, stale-pointer sweep, evidence-state
    sweep, git diff --check, evidence metrics matching command output, synchronized registries, commit hash and clean git
    status.
- Backlog items with missing closure-audit evidence, dirty git status, missing commit hash, stale next_backlog_item pointers
  or limited/unexecuted mandatory gates are incomplete and must not be treated as closed.
- Marketplace-ready capabilities validate purchase, entitlement, installation, activation, upgrade and retirement flows where
  applicable.
- No agent-specific dependency, prompt or configuration is required.
- Mandatory proprietary technology choices have an approved exception ADR.
capability_package_execution_contract:
  package_root: ../../01-product-definition/business-capabilities/packages/
  required_model_artifacts:
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
  generated_platform_outputs:
  - backend
  - employee_portal
  - mobile_app_when_applicable
  - patient_or_doctor_portal_when_applicable
  - openapi_rendered_contracts
  - sdk_when_applicable
  - repetitive_tests
  - repetitive_documentation
  - observability_assets
  custom_implementation_streams:
  - non_generatable_business_rules
  - external_adapters
  - security_sensitive_policies
  - performance_sensitive_queries
  - ambiguous_migration_mappings
  legacy_module_definition_artifacts:
    status: compatibility_only
    rule: Module definition packages may group capability packages but must not replace them as the source of truth.
commercial_ga_gates:
- id: GA-001
  name: Operational workflow gate
  criteria:
  - REL-001 modules are complete and validated.
  - End-to-end order to released result flow passes.
- id: GA-002
  name: Digital channel gate
  criteria:
  - Patient, doctor, employee and mobile surfaces pass access and privacy validation.
  - Public website handoffs do not expose private APIs.
- id: GA-003
  name: Security and compliance gate
  criteria:
  - Role, permission, audit, retention and privacy controls are validated.
  - Clinical authority restrictions are enforced.
- id: GA-004
  name: Operations gate
  criteria:
  - Deployment, monitoring, backup, restore and incident procedures are validated.
  - Performance and resilience tests pass agreed thresholds.
- id: GA-005
  name: Commercial enablement gate
  criteria:
  - Onboarding, training, support and release governance documents are complete.
  - Sales demo and customer pilot package are ready.
- id: GA-006
  name: Marketplace gate
  criteria:
  - Product Marketplace Standard is applied to HOP.
  - BCM-PLT-011 and COM-MOD-017 are modeled and traceable.
  - Marketplace package lifecycle, entitlement, installation and observability evidence is complete.
```
