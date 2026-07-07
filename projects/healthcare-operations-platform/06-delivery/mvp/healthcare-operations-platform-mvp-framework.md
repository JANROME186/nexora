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

1. Load the business requirement and required context listed in `healthcare-operations-platform-mvp-framework.yaml`.
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

- `module-definition.yaml`
- `domain-model.md`
- `api-contract.openapi.yaml`
- `database-migration-plan.md`
- `ui-screen-map.md`
- `security-and-audit-rules.md`
- `test-plan.md`
- `traceability.yaml`

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
