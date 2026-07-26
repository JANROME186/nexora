# HOP Operational Runbooks

This folder contains HOP's executable operational runbooks for
`COM-MOD-012 Platform Hardening and SaaS Operations`.

`local-solution-runbook.md` / `.md` remain the integrated local startup/shutdown/validation
runbook (component inventory, startup order, smoke tests). The runbooks below are the
observability, backup, restore and incident-management runbooks delivered by
`COM-MOD-012-OPS-002`, built on `../deployment/production-deployment-strategy.md`.

## Observability

- `observability-runbook.md` / `.md` — parent entry point for telemetry checks.
- `health-readiness-liveness-runbook.md` / `.md` — per-component probe matrix.
- `metrics-logs-traces-validation-runbook.md` / `.md` — telemetry content validation.

## Data protection

- `backup-runbook.md` / `.md` — produce and verify a restorable PostgreSQL backup.
- `restore-runbook.md` / `.md` — rehearse or execute a restore from a verified backup.

## Incident management

- `incident-response-runbook.md` / `.md` — hub runbook: detect, classify, contain, verify.
- `rollback-incident-handoff-runbook.md` / `.md` — application/database rollback mechanics.
- `tenant-impact-triage-runbook.md` / `.md` — determine tenant scope and check for cross-tenant
  leakage (compensating control for `TD-DB-004`).
- `evidence-collection-runbook.md` / `.md` — shared evidence bundle contract used by every
  runbook above.
- `post-incident-review-runbook.md` / `.md` — blameless review and durable follow-up tracking.

## How these fit together

`incident-response-runbook.md` is the entry point during a real incident; it delegates to the
others. Outside an incident, `observability-runbook.md` and `backup-runbook.md` are run routinely
(and `restore-runbook.md` as a rehearsal) to keep the system deployment-ready per
`../deployment/deployment-readiness-checklist.md`.

These runbooks are executable today against the local Docker Compose stack
(`07-implementation/compose.local.json`). Several steps document a target posture for `dev`, `qa`,
`staging` and `prod` that depends on infrastructure not yet provisioned; each runbook lists its own
`known_gaps_and_forward_pointers`.

## AI overlay

- `ai-overlay-assistant-review-runbook.md` — human-review procedure for AI assistant draft, review and audit-record backend outputs delivered by `COM-MOD-015-BE-001`.
