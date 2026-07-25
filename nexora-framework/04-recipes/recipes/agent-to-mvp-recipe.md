# Nexora Agent-to-MVP Recipe

## Purpose

This recipe tells an agent how to take a project description and prepare a development-ready MVP.

It is project-agnostic. Healthcare Operations Platform is the first project using it.

## Required Input

Every project starts with:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

This file must be supplied by the requester. The agent must not generate it.

Then the requester-supplied requirement is structured into:

`projects/<project-slug>/PROJECT_BRIEF.md`

If the business requirement is missing, the agent must stop and request it. The agent may mark the project blocked for analysis, but it must not infer or synthesize the missing file.

If the project brief is missing, the agent must create it from the business requirement before continuing.

## Phases

1. Intake normalization.
2. Product definition.
3. Domain foundation.
4. Architecture baseline, including open-source-first technology baseline and security quality gates.
5. MVP delivery framework.
6. Development readiness gate.

## Development Readiness Gate

Development can start when:

- The project brief exists.
- The high-level business requirement exists.
- Source of truth exists.
- Project state exists.
- Capability map and dependency map exist.
- Actor catalog exists.
- Reference processes exist.
- Business rules exist.
- Open-source-first technology baseline exists.
- Security quality gate strategy exists for SAST/static analysis, DAST when applicable, dependency vulnerability checks, secrets scan and coverage.
- MVP framework exists.
- First module definition package exists.
- `blocking_definition_gaps` is empty.

## Output

At the end of the recipe, an agent should know exactly which module to implement first and which files to load before coding.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-REC-001
  type: agent-recipe
  name: Nexora Agent-to-MVP Recipe
  version: 1.0.0
  status: approved
  owner: Nexora Engineering
inputs:
  required:
  - projects/<project-slug>/BUSINESS_REQUIREMENT.md
  - projects/<project-slug>/PROJECT_BRIEF.md
  - projects/<project-slug>/PROJECT_BRIEF.md
  - nexora-framework/02-standards/standards/project-folder-standard.md
  - nexora-framework/02-standards/standards/documentation-standard.md
  - nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  - nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
  - nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
  optional:
  - existing_source_material
  - reference_systems
  - regulatory_constraints
  - design_preferences
phases:
- id: PHASE-001
  name: Intake normalization
  goal: Validate requester-supplied business requirement and convert it into structured
    project context.
  outputs:
  - PROJECT_BRIEF.md
  - PROJECT_BRIEF.md
  - 00-intake/user-needs.yaml
  - 00-intake/assumptions.md
- id: PHASE-002
  name: Product definition
  goal: Define product scope, personas, capabilities and MVP boundary.
  outputs:
  - 01-product-definition/product.md
  - 01-product-definition/capability-map.yaml
  - 01-product-definition/capability-dependency-map.md
- id: PHASE-003
  name: Domain foundation
  goal: Define bounded contexts, aggregates, vocabulary, actors, processes and rules.
  outputs:
  - 02-domain-definition/context-map.md
  - 02-domain-definition/aggregate-catalog.md
  - 02-domain-definition/actor-catalog.md
  - 02-domain-definition/reference-processes.md
  - 02-domain-definition/business-rules-catalog.md
- id: PHASE-004
  name: Architecture baseline
  goal: Define application, technology, data, security, integration, AI, deployment,
    client stack market validation and open-source-first quality architecture.
  outputs:
  - 03-architecture/application-architecture.yaml
  - 03-architecture/technology-architecture.md
  - 03-architecture/client-stack-market-validation.md
  - 03-architecture/stack-quality-toolchain-baseline.md
  - 03-architecture/security-architecture.yaml
  - 03-architecture/integration-architecture.yaml
  - 03-architecture/open-source-first-technology-baseline.yaml
  - 03-architecture/security-quality-gates.yaml
  - 08-qa/technical-debt/README.md
  - 08-qa/technical-debt/technical-debt-index.md
  - 08-qa/framework-feedback/README.md
  - 08-qa/framework-feedback/framework-feedback-index.md
  - 09-operations/runbooks/local-solution-runbook.md
  - 09-operations/runbooks/local-solution-runbook.md
- id: PHASE-005
  name: MVP delivery framework
  goal: Convert definition into executable implementation modules.
  outputs:
  - 06-delivery/mvp-framework.yaml
  - 06-delivery/modules/<module-id>/module-definition.md
  - 06-delivery/modules/<module-id>/api-contract.openapi.md
  - 06-delivery/modules/<module-id>/test-plan.md
  - 06-delivery/modules/<module-id>/test-plan.md
  - 06-delivery/modules/<module-id>/domain-model.md
  - 06-delivery/modules/<module-id>/domain-model.md
  - 06-delivery/modules/<module-id>/database-migration-plan.md
  - 06-delivery/modules/<module-id>/database-migration-plan.md
  - 06-delivery/modules/<module-id>/ui-screen-map.md
  - 06-delivery/modules/<module-id>/ui-screen-map.md
  - 06-delivery/modules/<module-id>/security-and-audit-rules.md
  - 06-delivery/modules/<module-id>/security-and-audit-rules.md
  - 06-delivery/modules/<module-id>/traceability.md
- id: PHASE-006
  name: Development readiness gate
  goal: Decide whether coding may start.
  outputs:
  - PROJECT_STATE.md
  - SOURCE_OF_TRUTH.md
  - 08-qa/framework-feedback/framework-feedback-index.md
  readiness_condition:
    status: ready
    blocking_definition_gaps: []
rules:
- Never start analysis without BUSINESS_REQUIREMENT.md.
- BUSINESS_REQUIREMENT.md is supplied by the requester and must not be generated by
  an agent.
- If BUSINESS_REQUIREMENT.md is missing, stop and request it before creating project
  definitions.
- Never start implementation without PROJECT_BRIEF.md and PROJECT_BRIEF.md.
- Agent-executable artifacts must have YAML machine-readable files and Markdown human-readable
  companions when applicable.
- Never place project-specific artifacts outside the project folder.
- Never skip actor, process, rule and contract traceability.
- Do not introduce provider lock-in unless the project brief explicitly requires it.
- Prefer open source, self-hostable and standards-based technologies unless an ADR
  approves an exception.
- Validate any requester-proposed or existing stack against current stable or LTS
  versions, official lifecycle sources, security advisories, ecosystem health and
  required quality gates before accepting it as the implementation baseline.
- Define a stack-specific quality toolchain baseline during architecture analysis;
  for Java/Maven, evaluate SonarLint, SpotBugs, Find Security Bugs, Checkstyle, PMD,
  PMD CPD, JaCoCo, OWASP Dependency-Check, Trivy, CycloneDX Maven Plugin, Maven Enforcer,
  License Maven Plugin, PIT/Pitest, ArchUnit, OpenRewrite and Semgrep CE according
  to applicability.
- Define security quality gates for SAST/static analysis, DAST when applicable, dependency
  vulnerability checks, secrets scan and coverage before implementation starts.
- Define and maintain an integrated local solution runbook so a human reviewer can
  start infrastructure, backend, frontend or webapp, mobile validation and smoke checks
  from one guide.
- Treat the initial technology stack as a baseline, not a permanent constraint; every
  code-changing backlog item must review whether safer, better maintained or more
  cost-effective open source technology options now exist.
- Create or update technology debt backlog items for non-blocking upgrades, migrations
  or tooling gaps, and remediate them gradually when affected components are touched.
- Update the integrated local solution runbook whenever implementation changes runtime
  components, ports, environment variables, startup order or validation commands.
- Capture framework feedback when execution reveals ambiguity, missing templates,
  missing prompts, contradictory guidance, repetitive manual work or reusable automation
  opportunities.
- Do not implement framework improvement feedback unless Nexora explicitly assigns
  a central framework improvement backlog item.
- Implementation starts with the first module definition package, not with an empty
  app scaffold.
```
