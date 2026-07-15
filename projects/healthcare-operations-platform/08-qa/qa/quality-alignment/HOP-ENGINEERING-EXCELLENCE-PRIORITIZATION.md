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
