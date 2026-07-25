# 09 Operations

Current operational and deployment artifacts are in:

- `../technology-architecture/`
- `../platform-engineering/` inherited from `../../nexora-framework/08-engineering/platform-engineering/`

Use this folder for project-specific runbooks, deployment profiles and operations evidence.

## Production Deployment Strategy

COM-MOD-012 defines HOP's production deployment and environment strategy in:

- `deployment/production-deployment-strategy.md`
- `deployment/production-deployment-strategy.md`
- `deployment/environment-matrix.md`
- `deployment/environment-matrix.md`
- `deployment/deployment-readiness-checklist.md`
- `deployment/deployment-readiness-checklist.md`

These files describe the open-source-first, agent-agnostic environment path from local to production, including configuration, secrets, tenant onboarding, release promotion, rollback and deployment readiness checks.

## Customer Onboarding Guides

COM-MOD-016-DOC-001 defines customer onboarding and configuration guides in:

- `onboarding/README.md`
- `onboarding/onboarding-index.md`

Covers customer/tenant onboarding, organization & branch config, roles & permissions (27 permissions), regional localization, technical prerequisites, open data migration checklist, training/human validation, and initial support & operations.

## Support, Escalation and Release Governance

COM-MOD-016-OPS-001 defines operational support, escalation, and release governance in:

- `governance/README.md`
- `governance/governance-index.md`

Covers L1/L2/L3 support model, escalation matrix, SLAs/SLOs, incident management, problem management/RCA, change management/CAB, release governance, readiness checklists, rollback/hotfix procedures, implementation-to-ops handoff, customer incident/release communication, and operational acceptance criteria (OAC).

## Integrated Local Solution Runbook

Human reviewers should start here when they need to run the complete local solution:

- `runbooks/local-solution-runbook.md`
- `runbooks/local-solution-runbook.md`

This runbook covers prerequisites, infrastructure, backend, employee portal, mobile validation, smoke checks, quality checks, shutdown and reset steps in dependency order.
