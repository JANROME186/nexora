# HOP Engineering Excellence Prioritization

This document applies the Nexora Engineering Excellence Prioritization Standard to HOP.

The goal is not to block development with every desirable practice. The goal is:

- Implement the minimum enterprise baseline now.
- Register important improvements as technical debt.
- Keep desirable/contextual practices from blocking delivery unless risk promotes them.

## P0: Minimum Before Resuming `MVP-MOD-004-FE-001`

HOP must satisfy P0 for the changed scope:

- Clean/hexagonal boundaries, DDD ownership, API First and Contract First.
- SOLID, DRY, KISS, YAGNI and configuration over hard-coded values.
- No dead code or unnecessary duplication in changed scope.
- Automated tests, contract tests and smoke tests where applicable.
- OWASP Top 10 controls, all-severity vulnerability management and secrets handling.
- Structured logs, correlation IDs, audit and health/readiness/liveness checks.
- Controlled migrations, data integrity and stable API contracts.
- Reproducible scripts, simple configuration and fast feedback.
- Definition of Ready, Definition of Done, acceptance criteria and ADR for architecture changes.

## P1: Technical Debt And Incremental Improvement

These should be implemented as the product matures and as affected components are touched:

- Event-driven architecture for cross-boundary events.
- CQRS where read/write models diverge.
- Mutation testing for high-risk domain logic.
- E2E tests for core journeys.
- Threat modeling and OWASP ASVS mapping.
- Distributed tracing, metrics and alerts.
- Infrastructure as Code and release automation.
- Backward compatibility tests.
- AI governance practices when AI features begin.

## P2: Desirable, Not Blocking Now

These should not block HOP right now:

- Event Sourcing unless temporal replay becomes a true requirement.
- Chaos testing before production resilience maturity.
- Blue/Green, Canary or Continuous Deployment before deployment maturity.
- Six Sigma unless operational measurement and scale justify it.
- Pair/Mob Programming as team practice.
- AI features without a modeled business need.

## Current HOP Decision

The quality alignment backlog must close all P0 gaps, register P1 gaps as debt and prevent P2 items
from blocking functional delivery.

After `HOP-QA-ALIGN-CLOSEOUT`, development can resume with `MVP-MOD-004-FE-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-ENG-EXCELLENCE-PRIORITY-001
  type: project-engineering-excellence-prioritization
  name: HOP Engineering Excellence Prioritization
  version: 1.0.0
  status: active
  created_date: 2026-07-15
  human_readable: HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
  machine_readable: HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
  framework_standard: ../../../../../nexora-framework/02-standards/standards/engineering-excellence-prioritization-standard.md
  quality_alignment_backlog: ../../../06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
purpose: 'Apply the Nexora Engineering Excellence Prioritization Standard to HOP so
  the project implements the highest-value practices without blocking delivery on
  contextual or premature practices.

  '
decision:
  functional_development_can_resume_when:
  - P0 minimum enterprise baseline is satisfied for the current changed scope.
  - P1 gaps are registered or updated as technical debt with target backlog and acceptance
    criteria.
  - P2 items are documented as contextual/desirable and do not block MVP-MOD-004-FE-001.
  current_blocking_backlog_at_time_of_writing: HOP-QA-ALIGN-001
  current_blocking_backlog: none
  resolution: HOP-QA-ALIGN-001 through HOP-QA-ALIGN-006 are closed and HOP-QA-ALIGN-CLOSEOUT
    verified all six must_be_brought_to_p0_now practices below are satisfied. See
    08-qa/qa/quality-alignment/HOP-QA-ALIGN-CLOSEOUT.md for full closeout evidence.
  paused_functional_backlog_at_time_of_writing: MVP-MOD-004-FE-001
  paused_functional_backlog: null
hop_p0_minimum_to_satisfy_before_resuming_mvp_mod_004_fe_001:
  architecture:
  - Clean/hexagonal boundaries for changed backend and frontend code.
  - DDD ownership for capability packages and aggregates.
  - API First and Contract First for changed API surfaces.
  - Modularity, separation of concerns, high cohesion and low coupling.
  software_design:
  - SOLID, DRY, KISS, YAGNI and least surprise for changed code.
  - Configuration over hard-coded values.
  - Fail fast and defensive programming at API, persistence and integration boundaries.
  code_quality:
  - No dead code or unnecessary duplication in changed scope.
  - Meaningful names, low nesting, controlled complexity and correct exception handling.
  - Continuous refactoring where code is already touched.
  development_practices:
  - Boy Scout Rule for touched code.
  - Small commits and Conventional Commits.
  - Review evidence through agent or human review.
  testing:
  - Automated tests for changed behavior.
  - Unit, integration, contract and smoke tests where applicable.
  - Meaningful coverage evidence for changed scope.
  security:
  - Secure by Design and Security by Default.
  - Least privilege, OWASP Top 10 controls, secure authn/authz/session handling where
    applicable.
  - All-severity vulnerability management, secrets handling and dependency management.
  - SBOM readiness for releasable artifacts.
  performance:
  - Pagination for unbounded lists.
  - Idempotency for retryable commands and integrations.
  - Safe concurrency and efficient queries for touched persistence paths.
  observability:
  - Structured logging, correlation IDs, audit, health, readiness and liveness checks.
  devops:
  - CI-compatible reproducible quality gates.
  - Shift-left checks and safe rollback notes for deployable changes.
  maintainability:
  - Living documentation for changed behavior.
  - Technical debt management and dependency hygiene.
  data:
  - Data integrity, consistency, atomicity, referential integrity and controlled migrations.
  apis:
  - OpenAPI First, stable contracts, contract validation, consistent errors and versioning
    strategy.
  developer_experience:
  - Reproducible scripts, simple configuration, consistent local environment and fast
    feedback.
  artificial_intelligence:
  - Human-in-the-loop and traceability for any future sensitive AI feature.
  project_management:
  - Definition of Ready, Definition of Done, acceptance criteria and ADR for architecture
    decisions.
  process_quality:
  - Quality by Design, DevSecOps and continuous improvement feedback.
hop_p1_incremental_quality_backlog:
  strategy: Register or update technical debt; consume through debt-first execution
    without blocking unrelated delivery.
  practices:
  - Event Driven Architecture for cross-boundary business events.
  - CQRS where read/write models diverge.
  - Design by contract and Law of Demeter enforcement.
  - Rich domain objects where rules are currently procedural.
  - Mutation testing for core domain logic.
  - E2E tests for core journeys.
  - Threat modeling for sensitive clinical, financial and portal flows.
  - OWASP ASVS mapping.
  - Distributed tracing, metrics and alerts.
  - Infrastructure as Code and release automation.
  - Backward compatibility tests for public APIs.
  - Schema versioning and index optimization for known queries.
  - AI prompt/context/version/evaluation practices when AI backlog begins.
  - Event Storming and Impact Mapping for ambiguous business domains.
  - SRE and Platform Engineering practices as operations mature.
hop_p2_desirable_not_blocking:
  strategy: Document only when relevant; do not block MVP-MOD-004-FE-001 unless risk
    promotes the item.
  practices:
  - Event Sourcing unless temporal replay becomes a true domain requirement.
  - Full formal ASVS certification during early internal development.
  - Chaos testing before production resilience maturity.
  - Blue/Green, Canary or Continuous Deployment before deployment maturity requires
    it.
  - Six Sigma unless operational measurement and scale justify it.
  - Pair Programming and Mob Programming as team practices, not agent closure requirements.
  - Advanced internal developer portals before team scale requires them.
  - AI features before a modeled business need exists.
current_hop_alignment:
  already_strong:
  - DDD and capability package modeling are established.
  - API/OpenAPI source contracts exist for capability packages.
  - Technical-debt index exists.
  - Stack quality baseline exists.
  - Integrated local runbook exists.
  must_be_brought_to_p0_now:
  - backend Java/Maven quality profile
  - frontend web quality profile
  - all-severity vulnerability evidence
  - DAST execution or exact actionable blocker
  - message externalization and magic-string inventory
  - debt-first execution sequence
  p1_to_manage_as_debt:
  - mutation testing
  - broader threat modeling and ASVS mapping
  - distributed tracing and alerting depth
  - infrastructure as code maturity
  - AI governance practices until AI features start
  p2_not_to_block_now:
  - event sourcing
  - chaos testing
  - blue_green_or_canary_deployment
  - six_sigma
required_updates_to_quality_alignment_backlog:
- HOP-QA-ALIGN-001 must classify all open debt as P0, P1 or P2.
- HOP-QA-ALIGN-CLOSEOUT must verify that P0 is satisfied, P1 is registered, and P2
  is not used as a blocker.
```
