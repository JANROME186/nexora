# Healthcare Operations Platform — Project Brief

## Project Name

Healthcare Operations Platform

Commercial name:

Nexora Healthcare Operations Platform

Short name:

HOP

## User Need

Diagnostic healthcare organizations need a modern operations platform that can manage clinical laboratory and imaging workflows across branches while supporting patients, doctors, employees, integrations, data migration and AI-assisted operations.

The platform must help organizations move away from fragmented legacy systems and toward a secure, auditable, interoperable and scalable operating model.

Source business requirement:

`BUSINESS_REQUIREMENT.md`

## Target Users

- Laboratory administrators.
- Branch administrators.
- Receptionists.
- Cashiers.
- Sample collectors.
- Laboratory technicians.
- Technical validators.
- Medical validators.
- Referring doctors.
- Patients.
- Patient representatives.
- Integration partners.
- Fiscal and regulatory adapters.
- Laboratory devices.

## Problem Statement

Diagnostic organizations commonly operate with disconnected tools for patient registration, orders, samples, results, billing, portals, integrations and reporting. This causes duplicated data, weak traceability, slow result delivery, limited interoperability and difficult migrations.

## MVP Objective

Create the first executable operating spine for diagnostic laboratory operations:

1. Platform foundation.
2. Diagnostic catalog.
3. Patient and doctor master data.
4. Reception and order intake.
5. Cashier and billing request.
6. Sample workflow.
7. Result validation and digital delivery.
8. Integration and migration readiness.

## Must Have

- Multi-tenant foundation.
- Laboratory and branch management.
- Identity, scoped authorization and audit trail.
- Diagnostic catalog.
- Patient and doctor management.
- Order and sample lifecycle.
- Result validation and release.
- PDF result delivery through patient and doctor channels.
- OpenAPI-first contracts.
- Adapter boundaries for integrations and migration.
- Agent-agnostic documentation and implementation packages.

## Out of Scope for First MVP

- Full PACS/DICOM implementation.
- Advanced AI assistants.
- Full inventory and procurement automation.
- Advanced CAPA and external quality workflows.
- Country-specific fiscal connector implementation beyond adapter boundaries.

## Constraints

- Architecture Freeze v1.0 must be preserved.
- Development must remain agent-agnostic, cloud-agnostic and provider-agnostic.
- Clinical actions must be auditable.
- AI cannot validate, release or amend clinical results.
- Migration and external integrations must pass through anti-corruption layers.

## Success Criteria

- A development agent can load this project folder and start `MVP-MOD-001`.
- No blocking definition gaps remain for the first implementation module.
- Platform Foundation has API contract, domain model, database plan, UI map, tests, security rules and traceability.
- Future modules follow the same definition-package pattern.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BRIEF-001
  type: project-brief
  name: Healthcare Operations Platform Project Brief
  version: 1.0.0
  status: approved
  human_readable: PROJECT_BRIEF.md
  machine_readable: PROJECT_BRIEF.md
  source_business_requirement: BUSINESS_REQUIREMENT.md
project:
  name: Healthcare Operations Platform
  commercial_name: Nexora Healthcare Operations Platform
  short_name: HOP
  company: Nexora
user_need:
  summary: Diagnostic healthcare organizations need a modern operations platform for
    clinical laboratory and imaging workflows across branches.
  outcomes:
  - Move away from fragmented legacy systems.
  - Support secure, auditable, interoperable and scalable operations.
  - Support patients, doctors, employees, integrations, data migration and AI-assisted
    operations.
target_users:
- Laboratory administrators
- Branch administrators
- Receptionists
- Cashiers
- Sample collectors
- Laboratory technicians
- Technical validators
- Medical validators
- Referring doctors
- Patients
- Patient representatives
- Integration partners
- Fiscal and regulatory adapters
- Laboratory devices
problem_statement:
  summary: Diagnostic organizations commonly operate with disconnected tools for patient
    registration, orders, samples, results, billing, portals, integrations and reporting.
  impacts:
  - Duplicated data.
  - Weak traceability.
  - Slow result delivery.
  - Limited interoperability.
  - Difficult migrations.
mvp_objective:
  summary: Create the first executable operating spine for diagnostic laboratory operations.
  scope:
  - Platform foundation
  - Diagnostic catalog
  - Patient and doctor master data
  - Reception and order intake
  - Cashier and billing request
  - Sample workflow
  - Result validation and digital delivery
  - Integration and migration readiness
must_have:
- Multi-tenant foundation
- Laboratory and branch management
- Identity, scoped authorization and audit trail
- Diagnostic catalog
- Patient and doctor management
- Order and sample lifecycle
- Result validation and release
- PDF result delivery through patient and doctor channels
- OpenAPI-first contracts
- Adapter boundaries for integrations and migration
- Agent-agnostic documentation and implementation packages
out_of_scope_first_mvp:
- Full PACS/DICOM implementation
- Advanced AI assistants
- Full inventory and procurement automation
- Advanced CAPA and external quality workflows
- Country-specific fiscal connector implementation beyond adapter boundaries
constraints:
- Architecture Freeze v1.0 must be preserved.
- Development must remain agent-agnostic, cloud-agnostic and provider-agnostic.
- Clinical actions must be auditable.
- AI cannot validate, release or amend clinical results.
- Migration and external integrations must pass through anti-corruption layers.
success_criteria:
- A development agent can load this project folder and start MVP-MOD-001.
- No blocking definition gaps remain for the first implementation module.
- Platform Foundation has API contract, domain model, database plan, UI map, tests,
  security rules and traceability.
- Future modules follow the same definition-package pattern.
```
