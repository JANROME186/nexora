# Healthcare Operations Platform MVP Framework

## Purpose

This framework turns the Nexora definition repository into an implementation-ready work system for agents.

An agent must be able to start from repository files, select a module, load the right definitions, generate contracts and implementation tasks, and verify traceability without relying on chat history or any named AI platform.

## MVP Principle

The MVP is not the full healthcare platform. It is the smallest coherent diagnostic laboratory operating system:

- Organization and identity foundation.
- Diagnostic catalog.
- Patient and doctor master data.
- Reception and order intake.
- Cashier and billing request.
- Sample and laboratory workflow.
- Result validation, reporting and digital delivery.
- Integration and migration readiness boundaries.

Imaging, advanced inventory, advanced quality and AI assistants are intentionally staged after the first operational spine.

## MVP Modules

| Module | Name | Phase | Primary Output |
| --- | --- | --- | --- |
| MVP-MOD-001 | Platform Foundation | MVP1 | Tenant, lab, branch, IAM, audit and observability baseline. |
| MVP-MOD-002 | Diagnostic Catalog | MVP1 | Tests, analytes, samples, prices and preparation rules. |
| MVP-MOD-003 | People and Clinical Master Data | MVP1 | Patients, doctors and registration workflows. |
| MVP-MOD-004 | Front Desk and Care Delivery | MVP1 | Appointments, reception, admission, orders and quotations. |
| MVP-MOD-005 | Cashier and Billing Request | MVP1 | Cash sessions, payments and fiscal request boundary. |
| MVP-MOD-006 | Laboratory Workflow | MVP1 | Samples, processing, technical validation, medical validation and release. |
| MVP-MOD-007 | Results and Digital Delivery | MVP1 | PDF reports, patient portal, doctor portal and notifications. |
| MVP-MOD-008 | MVP Integration and Migration Readiness | MVP1 | Adapter contracts, import validation and public API governance. |

## Agent Execution Loop

1. Load the business requirement and required context listed in `healthcare-operations-platform-mvp-framework.md`.
2. Select one MVP module.
3. Resolve its capabilities through BCM-001 and BCM-002.
4. Resolve bounded contexts through the context map.
5. Resolve aggregates through the aggregate catalog.
6. Produce source artifacts first.
7. Produce generated artifacts second.
8. Produce implementation tasks third.
9. Verify security, audit, tests and traceability.
10. Update project state.

## Required Module Package

Each module implementation must produce:

- `module-definition.md`
- `domain-model.md`
- `api-contract.openapi.md`
- `database-migration-plan.md`
- `ui-screen-map.md`
- `security-and-audit-rules.md`
- `test-plan.md`
- `traceability.md`

These files can live under a future implementation repository or under module-specific definition folders, but the content must trace back to Nexora source artifacts.

## Definition of Ready

A capability is ready for implementation when:

- It exists in BCM-001.
- It has a dependency profile in BCM-002.
- Its owning bounded context is known.
- Its aggregates are known or intentionally absent.
- Its API classification is known.
- Security and audit expectations are defined.

## Definition of Done

A module is complete when:

- Source artifacts are updated.
- Generated artifacts are refreshed where applicable.
- OpenAPI contracts exist.
- Tests are defined and executable.
- Traceability links capability, API, UI, events and tests.
- Architecture Freeze v1.0 remains intact.

## Agent-Agnostic Rule

The repository is the source of truth.

Agent prompts, tool wrappers, external services, cloud services, AI providers and local runtimes are replaceable adapters. They may accelerate work, but they must not become required context for understanding or implementing the Healthcare Operations Platform.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MVP-FWK-001
  type: implementation-framework
  name: Healthcare Operations Platform MVP Agent-Agnostic Implementation Framework
  version: 1.0.0
  status: draft
  owner: Product Architecture Team
  source_of_truth: 06-delivery/mvp/healthcare-operations-platform-mvp-framework.md
  depends_on:
  - BCM-001
  - BCM-002
  - CTX-MAP-001
  - AGG-CATALOG-001
mission:
  product: Healthcare Operations Platform
  company: Nexora
  goal: Enable implementation agents to generate the MVP from repository definitions
    without relying on chat history or vendor-specific tooling.
  architecture_status: Architecture Freeze v1.0
agent_independence_rules:
- Agents may be AI agents, local model runtimes, deterministic generators or human
  engineers.
- All required context must come from repository artifacts.
- Prompts are operational adapters, not source of truth.
- Generated code must trace back to YAML or Markdown source artifacts.
- Cloud, AI provider, orchestration and deployment targets must remain replaceable.
required_agent_context:
  path_convention: Paths are relative to projects/healthcare-operations-platform unless
    they start with ../../ for repository-level Nexora framework files.
  always_load:
  - ../../AGENT_BOOTSTRAP.md
  - ../../PROJECT_STATE.md
  - ../../SOURCE_OF_TRUTH.md
  - ../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
  - BUSINESS_REQUIREMENT.md
  - PROJECT_STATE.md
  - SOURCE_OF_TRUTH.md
  - 01-product-definition/business-capabilities/bcm-001/business-capability-map.md
  - 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
  - 02-domain-definition/domain-foundation/context-map/context-map.md
  - 02-domain-definition/domain-foundation/shared-kernel/shared-kernel.md
  - 02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md
  load_when_building_contracts:
  - 05-contracts/contracts/openapi/
  - ../../nexora-framework/06-templates/templates/openapi-template.md
  load_when_building_ui:
  - 04-requirements/ux/
  - 04-requirements/ui/
  - 03-architecture/application-architecture/channel-architecture/channel-strategy.md
  load_when_building_mobile:
  - 04-requirements/mobile/
  - 04-requirements/ui/mobile/
  load_when_building_security:
  - 03-architecture/security-compliance/
  - 09-operations/engineering-governance/03-engineering/validators/
  load_when_building_ai:
  - 03-architecture/ai-platform/
  - 03-architecture/integration-architecture/
mvp_modules:
- id: MVP-MOD-001
  name: Platform Foundation
  objective: Establish tenant, laboratory, branch, identity, authorization, audit
    and observability baseline.
  mvp_phase: MVP1
  capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-ORG-006
  - BCM-ORG-008
  - BCM-PLT-001
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  bounded_contexts:
  - organization-management
  - identity-access
  - audit-compliance
  - observability
  implementation_outputs:
  - backend modulith modules
  - OpenAPI contracts
  - database migrations
  - authorization policy model
  - audit event model
  - local docker compose services
  - smoke tests
  ready_when:
  - A tenant, laboratory and branch can be created.
  - Users can authenticate and receive scoped permissions.
  - Protected APIs reject unauthorized access.
  - Audit events are emitted for protected state changes.
  - Logs, metrics and traces have local development sinks.
- id: MVP-MOD-002
  name: Diagnostic Catalog
  objective: Configure services, tests, panels, analytes, reference ranges, patient
    preparation, samples and prices.
  mvp_phase: MVP1
  capabilities:
  - BCM-SVC-001
  - BCM-SVC-002
  - BCM-SVC-003
  - BCM-SVC-004
  - BCM-SVC-005
  - BCM-SVC-006
  - BCM-SVC-007
  - BCM-SVC-009
  bounded_contexts:
  - catalog-test-configuration
  implementation_outputs:
  - catalog aggregate model
  - catalog administration APIs
  - catalog import/export format
  - employee portal catalog screens
  - validation rules
  - contract tests
  ready_when:
  - A diagnostic test can be defined with analytes, sample requirements and reference
    ranges.
  - A price can be assigned to a test or panel.
  - Orders can query only published catalog items.
  - Changes are auditable and version-aware.
- id: MVP-MOD-003
  name: People and Clinical Master Data
  objective: Manage patients, doctors and core person records required by orders and
    results.
  mvp_phase: MVP1
  capabilities:
  - BCM-PER-001
  - BCM-PER-002
  - BCM-PER-003
  - BCM-ATT-002
  bounded_contexts:
  - patient-management
  - medical-staff
  implementation_outputs:
  - patient APIs
  - doctor APIs
  - duplicate detection hooks
  - patient registration UI
  - doctor management UI
  - import mapping stubs
  ready_when:
  - A patient can be registered, updated and searched.
  - A doctor can be registered and linked to orders.
  - Patient and doctor data cannot be mutated by orders, billing or results.
  - Core patient registration supports patient portal identity linking later.
- id: MVP-MOD-004
  name: Front Desk and Care Delivery
  objective: Support appointments, reception, admission, quotations and order intake.
  mvp_phase: MVP1
  capabilities:
  - BCM-ATT-001
  - BCM-ATT-003
  - BCM-ATT-004
  - BCM-ATT-006
  - BCM-LAB-001
  bounded_contexts:
  - orders-samples
  - patient-management
  - catalog-test-configuration
  implementation_outputs:
  - appointment APIs
  - reception queue APIs
  - diagnostic order APIs
  - quote calculation APIs
  - employee portal worklist
  - order lifecycle tests
  ready_when:
  - Staff can create a walk-in or scheduled order.
  - Orders reference patient, doctor, branch and catalog snapshots.
  - Order pricing uses catalog price snapshots.
  - Order cancellation follows domain rules and emits events.
- id: MVP-MOD-005
  name: Cashier and Billing Request
  objective: Register sales, payments, cash sessions and billing requests.
  mvp_phase: MVP1
  capabilities:
  - BCM-ATT-005
  - BCM-ATT-008
  bounded_contexts:
  - cash-sales
  - billing-tax
  implementation_outputs:
  - cash session APIs
  - payment APIs
  - billing request APIs
  - country-pack fiscal adapter interface
  - cashier screens
  - audit and reconciliation tests
  ready_when:
  - A cashier can open and close a session.
  - A payment can be registered against an order sale.
  - Billing requests are created through an adapter boundary.
  - Cash and billing cannot mutate patient or order aggregates directly.
- id: MVP-MOD-006
  name: Laboratory Workflow
  objective: Manage sample collection, labeling, reception, processing and validation.
  mvp_phase: MVP1
  capabilities:
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-LAB-005
  - BCM-LAB-006
  - BCM-LAB-008
  - BCM-LAB-009
  - BCM-LAB-010
  bounded_contexts:
  - orders-samples
  - laboratory-results
  - catalog-test-configuration
  implementation_outputs:
  - sample lifecycle APIs
  - result capture APIs
  - validation workflow APIs
  - sample labels
  - lab worklists
  - event tests
  ready_when:
  - Samples can be collected, labeled, received and rejected.
  - Results can be captured against order lines and analytes.
  - Technical and medical validation are separate controlled actions.
  - Released results are immutable except through amendment workflows.
- id: MVP-MOD-007
  name: Results and Digital Delivery
  objective: Generate reports and deliver released results to patient and doctor channels.
  mvp_phase: MVP1
  capabilities:
  - BCM-RES-001
  - BCM-RES-002
  - BCM-RES-004
  - BCM-RES-005
  - BCM-RES-006
  - BCM-RES-007
  - BCM-PLT-003
  - BCM-PLT-008
  bounded_contexts:
  - laboratory-results
  - notifications
  - document-management
  implementation_outputs:
  - report generation service
  - released result APIs
  - patient result portal screens
  - doctor result portal screens
  - notification templates
  - critical result workflow tests
  ready_when:
  - Released results can generate a PDF report.
  - Patients and doctors can view only authorized released results.
  - Critical results trigger traceable notification workflows.
  - Result history is available by patient with access controls.
- id: MVP-MOD-008
  name: MVP Integration and Migration Readiness
  objective: Define adapter boundaries and import readiness without coupling the MVP
    to a specific external platform.
  mvp_phase: MVP1
  capabilities:
  - BCM-PLT-004
  - BCM-PLT-005
  bounded_contexts:
  - integration-interoperability
  - data-migration-portability
  implementation_outputs:
  - adapter interface contracts
  - import validation model
  - migration dry-run reports
  - webhook strategy
  - public API governance
  ready_when:
  - External messages must be normalized before reaching domains.
  - Imports can validate patients, catalog, orders and results before mutation.
  - OpenAPI contracts identify public, internal and partner surfaces.
  - Integration failures are observable and auditable.
non_mvp1_modules:
- id: FUT-MOD-001
  name: Inventory and Quality
  default_phase: MVP2
  capabilities:
  - BCM-INV-001
  - BCM-INV-002
  - BCM-INV-003
  - BCM-INV-004
  - BCM-INV-005
  - BCM-INV-006
  - BCM-INV-007
  - BCM-INV-008
  - BCM-INV-009
  - BCM-QLT-001
  - BCM-QLT-003
  - BCM-QLT-004
  - BCM-QLT-005
- id: FUT-MOD-002
  name: Advanced Quality and Compliance
  default_phase: MVP3
  capabilities:
  - BCM-QLT-002
  - BCM-QLT-006
  - BCM-QLT-007
- id: FUT-MOD-003
  name: Imaging Operations
  default_phase: MVP2-MVP3
  capabilities:
  - BCM-IMG-001
  - BCM-IMG-002
  - BCM-IMG-003
  - BCM-IMG-004
  - BCM-IMG-005
  - BCM-IMG-006
  - BCM-IMG-007
  - BCM-IMG-008
- id: FUT-MOD-004
  name: AI Overlay
  default_phase: MVP2-MVP3
  capabilities:
  - BCM-AI-001
  - BCM-AI-002
  - BCM-AI-003
  - BCM-AI-004
  - BCM-AI-005
  - BCM-AI-006
  - BCM-AI-007
  - BCM-AI-008
implementation_contract:
  per_module_required_artifacts:
  - module-definition.md
  - domain-model.md
  - api-contract.openapi.md
  - database-migration-plan.md
  - ui-screen-map.md
  - security-and-audit-rules.md
  - test-plan.md
  - traceability.md
  per_capability_required_artifacts:
  - capability-scope.md
  - business-rules.md
  - commands-and-queries.md
  - domain-events.md
  - acceptance-tests.md
  definition_of_ready:
  - Capability exists in BCM-001.
  - Capability dependency profile exists in BCM-002.
  - Owning bounded context is known.
  - Aggregate ownership is known or explicitly marked as no aggregate.
  - API surface is classified as public, internal, partner or system.
  - Security role and audit event expectations are defined.
  definition_of_done:
  - Source artifact updated.
  - Generated artifact refreshed when applicable.
  - Tests defined and executable.
  - Traceability updated from capability to API, UI, domain events and tests.
  - PROJECT_STATE updated with completed work.
  - No architecture freeze violation.
```
