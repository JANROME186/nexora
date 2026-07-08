# MVP Backlog Execution Prompts

## Purpose

This document is the execution playbook for implementing the Healthcare Operations Platform MVP with any capable development agent.

It supports the generic MVP development prompt at:

```text
nexora-framework/prompts/generic-project-lifecycle-prompts.yaml
```

If this playbook conflicts with the generic prompt, `SOURCE_OF_TRUTH.yaml`, `PROJECT_STATE.yaml` or the module package, stop and report the conflict.

The user should only need to give a generic instruction such as:

```text
Implement the Healthcare Operations Platform MVP using the backlog execution playbook.
Start with the first pending backlog item and follow the documented order.
```

The agent must then read this document, load the required project context, and execute the backlog incrementally.

## Non-Negotiable Rules

- Work only from the definitions already present in the project.
- Use the generic MVP development prompt as the primary instruction.
- Do not modify `BUSINESS_REQUIREMENT.md`.
- Do not create unnumbered folders at the project root.
- Place implementation code under `07-implementation/`.
- Implement one backlog item at a time.
- Do not skip backlog items unless `PROJECT_STATE.yaml` proves they are already complete.
- If a required source file is missing, stop and report the exact missing file.
- If definitions contradict each other, stop and report the contradiction before coding.
- Update `PROJECT_STATE.yaml` only with real, verified progress.
- Keep tests, QA notes or validation evidence aligned with `08-qa/`.
- Keep runtime or operational changes aligned with `09-operations/`.

## Required Loading Order

Before implementing any backlog item, the agent must read these files in order:

1. `BUSINESS_REQUIREMENT.md`
2. `PROJECT_BRIEF.yaml`
3. `SOURCE_OF_TRUTH.yaml`
4. `PROJECT_STATE.yaml`
5. `ORDERED_DEVELOPMENT_GUIDE.yaml`
6. `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
7. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`
8. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.yaml`
9. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/api-contract.openapi.yaml`
10. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.yaml`
11. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.yaml`
12. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.yaml`
13. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.yaml`
14. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.yaml`

## Execution Order

The MVP-MOD-001 backlog must be executed in this order:

1. `PF-BE-001` - Create backend project skeleton.
2. `PF-OPS-001` - Create local development compose profile.
3. `PF-BE-002` - Implement tenant, laboratory and branch commands.
4. `PF-BE-003` - Implement user account and role assignment baseline.
5. `PF-BE-004` - Implement append-only audit event recording.
6. `PF-FE-001` - Create employee portal administration screens.
7. `PF-APP-001` - Create mobile app foundation.
8. `PF-QA-001` - Add smoke and contract tests.
9. `MVP-MOD-001-CLOSEOUT` - Validate and close the module.

## Generic Agent Instruction

Use this instruction when starting development:

```text
Implement Healthcare Operations Platform MVP-MOD-001 using the project backlog execution playbook.

Read:
- projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.yaml
- projects/healthcare-operations-platform/SOURCE_OF_TRUTH.yaml
- projects/healthcare-operations-platform/PROJECT_STATE.yaml
- projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.yaml

Then execute the first pending backlog item in the documented order.

Rules:
- Do not modify BUSINESS_REQUIREMENT.md.
- Do not create unnumbered folders at the project root.
- Put implementation code under projects/healthcare-operations-platform/07-implementation/.
- Validate the result before moving to the next backlog item.
- Update project state only when progress is real and verified.
```

## Base Prompt For Each Backlog Item

Each backlog item inherits this base instruction:

```text
Act as a senior development agent for Healthcare Operations Platform.

Work inside the local repository and strictly respect the existing project definitions. Before implementing, read the required loading order from:

projects/healthcare-operations-platform/06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.yaml

Rules:
- Implement only the requested backlog item.
- Place code in projects/healthcare-operations-platform/07-implementation/.
- Do not modify projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md.
- Do not add unnumbered folders at the project root.
- If a required file is missing or definitions contradict each other, stop and report the issue.
- Update implementation README files, tests and PROJECT_STATE.yaml only when the real implementation justifies it.
```

## PF-BE-001 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-BE-001 - Create backend project skeleton.

Objective:
Create the backend skeleton for MVP-MOD-001 Platform Foundation.

Required scope:
- Java 21.
- Spring Boot 3.x.
- Spring Modulith.
- Hexagonal architecture.
- Modules:
  - organization-management
  - identity-access
  - audit-compliance
  - observability
- Base PostgreSQL configuration.
- Health check endpoint.
- Clear package structure per module.
- Minimum smoke test proving that the application starts.
- Backend README with run and test commands.

Completion criteria:
The backend compiles, the smoke test passes, and the structure is ready for commands, APIs and persistence.
```

## PF-OPS-001 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-OPS-001 - Create local development compose profile.

Objective:
Create the local development runtime.

Required scope:
- Docker Compose under 07-implementation/.
- PostgreSQL.
- Redis.
- Documented environment variables.
- Backend configuration for local PostgreSQL.
- Clear instructions to start and stop the local runtime.
- Minimal seed data if applicable.
- Updated implementation README with local execution steps.

Completion criteria:
The local runtime starts, PostgreSQL and Redis are available, and the backend can connect using local configuration.
```

## PF-BE-002 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-BE-002 - Implement tenant, laboratory and branch commands.

Objective:
Implement the organization-management functional baseline for tenant, laboratory and branch.

Required scope:
- Entities or aggregates defined in domain-model.yaml.
- Use cases and commands to create and query:
  - tenant
  - laboratory
  - branch
- Hexagonal repository ports.
- PostgreSQL persistence adapters.
- Database migrations aligned with database-migration-plan.yaml.
- REST endpoints aligned with api-contract.openapi.yaml.
- Applicable business validations.
- Domain events if defined.
- Minimum unit and integration tests.

Completion criteria:
Tenant, laboratory and branch can be created and queried through the API with real persistence and passing tests.
```

## PF-BE-003 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-BE-003 - Implement user account and role assignment baseline.

Objective:
Implement the identity-access baseline for users, roles and initial authorization.

Required scope:
- UserAccount and role model according to domain-model.yaml.
- Local development authentication mode.
- Role assignment to users.
- Authorization policies according to security-and-audit-rules.yaml.
- REST endpoints aligned with api-contract.openapi.yaml.
- PostgreSQL persistence.
- Scope and role validations.
- Minimum authorization tests for allowed and denied access.

Completion criteria:
Local authentication works, users have roles, protected endpoints enforce authorization, and tests validate permitted and denied access.
```

## PF-BE-004 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-BE-004 - Implement append-only audit event recording.

Objective:
Implement append-only audit recording for relevant Platform Foundation actions.

Required scope:
- AuditEvent model.
- Append-only persistence.
- Automatic audit events for:
  - tenant creation
  - laboratory creation
  - branch creation
  - user creation or role changes
- Audit query API if defined.
- Audit security rules according to security-and-audit-rules.yaml.
- Tests proving audit events are recorded and cannot be modified.

Completion criteria:
Critical actions generate persisted audit events that are queryable according to authorization and protected against modification.
```

## PF-FE-001 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-FE-001 - Create employee portal administration screens.

Objective:
Create the administrative web portal for Platform Foundation.

Required scope:
- React + TypeScript.
- Web implementation under 07-implementation/.
- Screens according to ui-screen-map.yaml:
  - tenant administration
  - laboratory administration
  - branch administration
  - user administration
  - role administration
- API client connected to backend endpoints.
- Loading, error and success states.
- Base navigation.
- Form validation.
- Minimum component or primary-flow tests.
- Updated README with run commands.

Completion criteria:
The web portal can operate the base module functions against the local backend or a clearly documented mock.
```

## PF-APP-001 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-APP-001 - Create mobile app foundation.

Objective:
Create the base mobile app for Healthcare Operations Platform aligned with MVP-MOD-001.

Required scope:
- Mobile app under 07-implementation/.
- React Native, Expo or the simplest maintainable option compatible with the repository.
- Initial navigation.
- Local or baseline login screen.
- Local session handling.
- API client prepared to connect to the backend.
- Initial authenticated home screen.
- Structure prepared for future clinical and operational modules.
- README with run commands.

Restriction:
Do not implement functionality outside Platform Foundation scope.

Completion criteria:
The mobile app runs, supports a basic authentication flow, and is prepared to consume backend APIs.
```

## PF-QA-001 Prompt

```text
Use the Base Prompt For Each Backlog Item.

Backlog item: PF-QA-001 - Add smoke and contract tests.

Objective:
Add the minimum quality suite for MVP-MOD-001.

Required scope:
- Backend smoke tests.
- Contract tests against api-contract.openapi.yaml.
- Endpoint tests for:
  - tenant
  - laboratory
  - branch
  - user
  - role
  - audit
- Minimum web tests.
- Minimum mobile tests if the mobile app exists.
- Documentation for running tests.
- QA evidence or result summary under 08-qa/ when applicable.

Completion criteria:
A minimum test suite validates that backend, contracts, web and mobile foundation work for the MVP-MOD-001 scope.
```

## MVP-MOD-001 Closeout Prompt

```text
Act as the final validation agent for MVP-MOD-001 Platform Foundation.

Validate that these backlog items are complete:
- PF-BE-001
- PF-OPS-001
- PF-BE-002
- PF-BE-003
- PF-BE-004
- PF-FE-001
- PF-APP-001
- PF-QA-001

Review:
- projects/healthcare-operations-platform/PROJECT_STATE.yaml
- projects/healthcare-operations-platform/SOURCE_OF_TRUTH.yaml
- projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.yaml
- projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.yaml
- projects/healthcare-operations-platform/07-implementation/
- projects/healthcare-operations-platform/08-qa/

Confirm:
- Backend runs locally.
- Web runs locally.
- Mobile app runs locally.
- Docker Compose starts local dependencies.
- Minimum tests pass.
- BUSINESS_REQUIREMENT.md was not modified.
- No unnumbered folders were created at the project root.
- The implementation respects MVP-MOD-001 definitions.

If everything is correct, update PROJECT_STATE.yaml to indicate that MVP-MOD-001 is implemented and ready for functional validation.

If something is incomplete, produce a concrete blocker list and do not mark the module complete.
```

## Progress Tracking

After each backlog item, record the result in the implementation README or project state with:

- Backlog item id.
- Implementation status.
- Files or modules created.
- Validation commands executed.
- Known gaps or blockers.
- Next backlog item to execute.
