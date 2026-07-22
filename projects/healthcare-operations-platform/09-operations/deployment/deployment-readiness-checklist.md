# HOP Deployment Readiness Checklist

Backlog item: `COM-MOD-012-OPS-001`

Use this checklist before promoting any HOP release candidate.

## Before Deployment

- Confirm the release commit.
- Run all quality gates for changed deployment units.
- Confirm zero unresolved vulnerabilities across all severities.
- Generate SBOM evidence for backend and containerized artifacts.
- Run database migration dry-run.
- Attach rollback and restore commands.

## During Deployment

- Deploy immutable images or bundles.
- Load configuration and secrets from approved providers.
- Validate health, readiness and liveness probes.
- Execute tenant isolation smoke checks.
- Confirm API gateway policy, CORS, CSP, HSTS and rate limits.

## After Deployment

- Run smoke tests for backend, web portals, public website and mobile compatibility paths.
- Confirm metrics, logs and traces include tenant and correlation context.
- Confirm deployment, migration and privileged-operation audit events.
- Attach backup schedule and latest restore rehearsal evidence.
- Archive release evidence through the document management capability.
