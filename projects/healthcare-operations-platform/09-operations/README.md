# 09 Operations

Current operational and deployment artifacts are in:

- `../technology-architecture/`
- `../platform-engineering/` inherited from `../../nexora-framework/08-engineering/platform-engineering/`

Use this folder for project-specific runbooks, deployment profiles and operations evidence.

## Production Deployment Strategy

COM-MOD-012 defines HOP's production deployment and environment strategy in:

- `deployment/production-deployment-strategy.md`
- `deployment/production-deployment-strategy.yaml`
- `deployment/environment-matrix.md`
- `deployment/environment-matrix.yaml`
- `deployment/deployment-readiness-checklist.md`
- `deployment/deployment-readiness-checklist.yaml`

These files describe the open-source-first, agent-agnostic environment path from local to production, including configuration, secrets, tenant onboarding, release promotion, rollback and deployment readiness checks.

## Integrated Local Solution Runbook

Human reviewers should start here when they need to run the complete local solution:

- `runbooks/local-solution-runbook.md`
- `runbooks/local-solution-runbook.yaml`

This runbook covers prerequisites, infrastructure, backend, employee portal, mobile validation, smoke checks, quality checks, shutdown and reset steps in dependency order.
