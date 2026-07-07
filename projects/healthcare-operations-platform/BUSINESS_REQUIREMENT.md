# Healthcare Operations Platform Business Requirement

## Purpose of This Document

This document is the high-level business requirement for Healthcare Operations Platform.

It explains why the product exists, which operational problems it solves, what the first MVP must prove, and which constraints must guide every analysis, architecture decision and implementation module.

This file is intentionally broader than `PROJECT_BRIEF.md`. The brief structures this requirement into product scope, while the downstream artifacts translate it into capabilities, domain boundaries, architecture, contracts, MVP modules and implementation packages.

## Business Context

Diagnostic healthcare organizations operate through a chain of administrative, clinical, financial and digital interactions:

- Patients request appointments, register information, attend branches and receive results.
- Reception teams manage appointments, intake, quotations, orders and patient communication.
- Cashiers manage payments, cash sessions and billing requests.
- Sample collection and laboratory teams manage collection, labeling, reception, processing, validation and release.
- Medical validators authorize clinical release of results.
- Referring doctors need secure access to released patient results.
- Administrators need multi-branch configuration, users, roles, auditing and operational visibility.
- External systems, devices, fiscal services and migration processes need controlled integration boundaries.

Many organizations still operate this chain through fragmented tools, legacy systems, spreadsheets, manual handoffs and local integrations that are difficult to audit, scale or migrate.

Nexora needs Healthcare Operations Platform to become a reusable, specification-driven reference project for building agent-assisted enterprise software. The first domain is diagnostic laboratory operations, but the method, folder structure and MVP definition approach must serve as a model for future Nexora solutions.

## Business Opportunity

Healthcare Operations Platform should help diagnostic organizations move from disconnected operational tools to a unified, secure and traceable operating model.

The opportunity is not only to replace a legacy laboratory system. The larger opportunity is to define a platform foundation that can support:

- Multi-tenant diagnostic organizations.
- Multi-branch laboratory operations.
- Consistent patient, doctor and catalog master data.
- Auditable clinical and administrative workflows.
- Digital result delivery.
- Integration and migration readiness.
- Future healthcare modules such as imaging, inventory, quality, advanced compliance and AI-assisted operations.

The platform should also prove that Nexora can transform a high-level business need into an MVP-ready project folder that specialized agents can implement incrementally from repository artifacts.

## User Need

Diagnostic healthcare operators need a platform that lets them manage the end-to-end laboratory operating spine from organization setup to released result delivery.

The platform must support:

- Administrators configuring tenants, laboratories, branches, employees, roles and operational settings.
- Catalog managers defining diagnostic services, tests, panels, analytes, samples, reference ranges, preparation instructions and prices.
- Front desk teams registering patients, scheduling appointments, creating orders and managing reception queues.
- Cashiers opening sessions, registering payments and requesting billing through fiscal adapter boundaries.
- Sample collectors collecting and labeling samples with full traceability.
- Laboratory technicians receiving samples, processing work and capturing results.
- Technical and medical validators controlling result validation and release.
- Patients and representatives accessing only authorized released results.
- Referring doctors accessing only assigned and released results.
- External systems and devices integrating through normalized, validated and auditable adapters.

## Current Pain

The business pain is caused by operational fragmentation and weak traceability.

Common pain points include:

- Patient data is duplicated or inconsistently updated across tools.
- Orders, payments, samples and results are not always linked through a single traceable lifecycle.
- Branch operations depend on local practices instead of consistent workflows.
- Catalog configuration is hard to govern, version and audit.
- Sample collection and rejection events may be disconnected from order and result release decisions.
- Technical validation, medical validation and result release are not always clearly separated.
- Billing and fiscal logic can leak into clinical or cashier workflows.
- External device and partner integrations can bypass validation when implemented as direct point-to-point connections.
- Migrations from legacy systems are risky because imported records are not normalized before entering operational domains.
- Audits are difficult when systems allow direct mutation without append-only event traces.
- Digital portals may expose privacy risk if released-result authorization is not strict.

## Desired Outcome

The desired outcome is a modern healthcare operations platform that standardizes the first diagnostic laboratory operating spine and makes every critical action traceable.

At a business level, the platform must enable:

- A diagnostic organization to configure its tenant, laboratories and branches.
- Authorized users to work with scoped permissions.
- Patients and doctors to be represented as managed master data.
- Diagnostic catalog items to be published before order use.
- Orders to preserve patient, catalog and price snapshots.
- Payments and billing requests to remain auditable and adapter-driven.
- Samples to trace back to order, patient snapshot, collector, branch and collection time.
- Technical validation to precede medical validation unless policy explicitly allows an exception.
- Released results to become available through authorized digital channels.
- Critical results to create traceable notification or escalation records.
- Integrations and migrations to pass through anti-corruption and validation layers.

At an engineering level, the platform must enable:

- Agents to read repository artifacts and begin implementation without chat history.
- Specialized subagents to work by module without breaking bounded-context ownership.
- Every implementation output to trace back to source artifacts.
- The MVP to start from `MVP-MOD-001 Platform Foundation`.

## Target Users and Actors

The product must serve the following actor groups.

Internal operational staff:

- Platform Super Administrator.
- Tenant Administrator.
- Branch Administrator.
- Receptionist.
- Cashier.
- Sample Collector.
- Laboratory Technician.
- Technical Validator.
- Medical Validator.
- Catalog Manager.

External clinical and patient users:

- Referring Doctor.
- Patient.
- Patient Representative.

External systems and platform services:

- Integration Partner System.
- Laboratory Device.
- Fiscal Authority Adapter.
- Notification Service.
- Audit Service.

Actor definitions, permissions, scopes and audit levels are refined in:

`02-domain-definition/actors/acm-001/actor-catalog.yaml`

## Business Capabilities

The business capability map defines 11 domains and 90 capabilities.

The first MVP concentrates on the operational spine across:

- Organization.
- People.
- Diagnostic Services.
- Care Delivery.
- Clinical Operations.
- Results.
- Platform.
- Integration and migration readiness.

MVP1 intentionally uses a smaller coherent scope than the full capability map. Advanced imaging, inventory, advanced quality, external quality programs, advanced workflow automation and advanced AI overlays are staged for later phases.

Capability definitions are refined in:

`01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`

Capability dependency and sequencing rules are refined in:

`01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`

## MVP Expectation

The MVP must prove the first executable diagnostic laboratory operating spine.

MVP modules:

| Module | Name | Business Purpose |
| --- | --- | --- |
| MVP-MOD-001 | Platform Foundation | Establish tenant, laboratory, branch, identity, authorization, audit and observability baseline. |
| MVP-MOD-002 | Diagnostic Catalog | Configure services, tests, panels, analytes, reference ranges, samples, preparation rules and prices. |
| MVP-MOD-003 | People and Clinical Master Data | Manage patients, doctors and core person records used by orders and results. |
| MVP-MOD-004 | Front Desk and Care Delivery | Support appointments, reception, admission, quotations and order intake. |
| MVP-MOD-005 | Cashier and Billing Request | Register sales, payments, cash sessions and billing requests through fiscal boundaries. |
| MVP-MOD-006 | Laboratory Workflow | Manage sample collection, labeling, reception, processing and validation. |
| MVP-MOD-007 | Results and Digital Delivery | Generate reports and deliver released results to patient and doctor channels. |
| MVP-MOD-008 | MVP Integration and Migration Readiness | Define adapter boundaries, import validation and public API governance. |

The first implementation target is:

`06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

## First MVP Module Requirement

`MVP-MOD-001 Platform Foundation` must be implemented first because the rest of the platform depends on identity, organization, audit and observability.

It must establish:

- Tenant management.
- Laboratory management.
- Branch management.
- Employee baseline.
- Organizational configuration.
- Identity and access management.
- Platform configuration.
- Observability.
- Append-only audit trail.

The module is ready to code only because it already has:

- Domain model.
- API contract.
- Database migration plan.
- UI screen map.
- Security and audit rules.
- Test plan.
- Traceability.

The module definition is:

`06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`

## Business Rules and Guardrails

The solution must enforce these business guardrails:

- Protected actions require authenticated human or service actors.
- Role assignments must be scoped to platform, tenant, laboratory or branch.
- Patient master data is owned by patient-management.
- Orders must use patient snapshots and must not mutate patient master data.
- Only published catalog items can be ordered.
- Accepted orders must preserve price snapshots.
- Payments require active cashier sessions.
- Fiscal billing must go through country-pack adapter interfaces.
- Every collected sample must trace to order, patient snapshot, branch, collector and collection time.
- Rejected samples block dependent result release unless a replacement or override process is completed.
- Technical validation precedes medical validation unless explicitly waived by policy.
- Medical validation is required before external result release.
- Critical results require traceable notification or escalation.
- Patient and doctor portals show only authorized released results.
- Patient representative access requires active authorization.
- External messages and migrations must be normalized and validated before reaching domain commands.
- AI may assist, summarize or accelerate work, but cannot validate, release, amend or diagnose clinical results.
- Audit records are append-only.

Detailed rules are refined in:

`02-domain-definition/business-rules/brm-001/business-rules-catalog.yaml`

## Domain and Integration Principles

The platform must preserve clear domain ownership.

Required domain principles:

- A bounded context owns its aggregates.
- Other contexts reference external aggregates only through stable identifiers or snapshots.
- Cross-context behavior must use APIs, events, commands, published language or anti-corruption layers.
- External protocols must be translated before entering domain logic.
- AI, migration and integration contexts cannot bypass validation or authorization.

Strategic relationships include:

- Identity and Organization share core identifiers such as tenant, laboratory, branch, user and permission.
- Orders consume patient snapshots but cannot mutate patient master data.
- Orders consume catalog definitions and sample requirements.
- Results follow upstream order and sample lifecycle events.
- Billing consumes fiscal-eligible sale and payment events.
- Migration imports pass through a universal import model, canonical data model, validation and reconciliation.

The context map is refined in:

`02-domain-definition/domain-foundation/context-map/context-map.yaml`

## Architecture Expectations

The architecture must support modular implementation without forcing deployment lock-in.

Expected architectural qualities:

- Modular domain boundaries.
- OpenAPI-first service contracts.
- Event-aware workflows where lifecycle traceability matters.
- Scoped authorization and audit by default.
- Data ownership by bounded context.
- Adapter boundaries for devices, fiscal services, public APIs, webhooks and migration.
- Local development runtime for implementation speed.
- Replaceable cloud, AI, orchestration and deployment choices.
- Observability through logs, metrics and traces.

Architecture source artifacts live under:

`03-architecture/`

## Data, Privacy and Audit Expectations

The platform handles sensitive patient, clinical, operational and fiscal data.

The business requirement demands:

- Patient information must be protected by scoped access.
- External users may access only authorized released information.
- Clinical validation and release actions must be auditable.
- Audit events must be immutable and correction-friendly through additional events.
- Imported data must be validated before mutation.
- Result history must be available only through authorized channels.
- Notifications for critical results must be traceable.
- Country-specific fiscal and regulatory behavior must be isolated through extension packs or adapters.

## AI and Automation Expectations

AI is a progressive capability, not a dependency for the first operating spine.

AI may later support:

- Administrative assistance.
- Clinical summarization support.
- Intelligent OCR.
- Semantic search.
- RAG-based knowledge retrieval.
- Specialized operational agents.

AI must not:

- Validate clinical results.
- Release results.
- Amend clinical results.
- Make diagnoses.
- Become required for core business continuity.
- Bypass privacy, authorization, audit or validation rules.

AI capability details are refined in:

`03-architecture/ai-platform/`

## Out of Scope for First MVP

The following are intentionally outside the first MVP:

- Full PACS implementation.
- Full DICOM operational flow.
- Advanced imaging reporting.
- Full inventory and procurement automation.
- Advanced quality management.
- External quality control programs.
- CAPA workflows.
- Equipment maintenance automation.
- Country-specific fiscal connector implementations beyond adapter boundaries.
- Advanced AI assistants as required operational features.
- Advanced workflow engine as a core dependency.

These capabilities can be promoted later only when the MVP operating spine remains stable.

## Constraints

The solution must respect these constraints:

- Development must remain agent agnostic.
- Architecture must remain cloud agnostic and provider agnostic.
- Source artifacts are the source of truth.
- Chat history is not source of truth.
- Project-specific artifacts must stay inside the project folder.
- Generated artifacts must not replace authoritative source artifacts.
- Architecture Freeze v1.0 must be preserved unless an ADR changes it.
- Implementation must begin from module definition packages, not from an empty scaffold.
- Each module must update traceability, tests and project state.

## Success Criteria

The business requirement is satisfied for MVP readiness when:

- A development agent can load the project folder without prior conversation.
- `SOURCE_OF_TRUTH.yaml` identifies the authoritative artifacts.
- `PROJECT_STATE.yaml` has no blocking definition gaps.
- MVP modules are ordered and traceable.
- `MVP-MOD-001 Platform Foundation` is ready to implement.
- Specialized subagents can receive module packages and start work independently.
- Security, audit, privacy and integration guardrails are visible before coding.
- Every module can trace implementation outputs back to business capabilities and this requirement.

## Reference Pattern for Future Nexora Projects

Future Nexora projects should use this document as a model for `BUSINESS_REQUIREMENT.md`.

A robust business requirement should describe:

- Business context.
- Business opportunity.
- User need.
- Current pain.
- Desired outcome.
- Target users and actors.
- Capability areas.
- MVP expectation.
- First module requirement.
- Business rules and guardrails.
- Domain and integration principles.
- Architecture expectations.
- Data, privacy and audit expectations.
- AI and automation expectations when relevant.
- Out-of-scope boundaries.
- Constraints.
- Success criteria.
- Links to downstream source artifacts.

The purpose is to give agents enough context to perform analysis and produce an MVP proposal without relying on unstated assumptions.

## Downstream Source Artifacts

This requirement is refined by:

- `PROJECT_BRIEF.md`
- `SOURCE_OF_TRUTH.yaml`
- `PROJECT_STATE.yaml`
- `ORDERED_DEVELOPMENT_GUIDE.md`
- `01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
- `02-domain-definition/actors/acm-001/actor-catalog.yaml`
- `02-domain-definition/business-rules/brm-001/business-rules-catalog.yaml`
- `02-domain-definition/domain-foundation/context-map/context-map.yaml`
- `03-architecture/`
- `04-requirements/`
- `05-contracts/`
- `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
- `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`
