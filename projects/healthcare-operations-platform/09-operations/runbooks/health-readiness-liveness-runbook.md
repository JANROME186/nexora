# HOP Health, Readiness and Liveness Check Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Verify every runnable HOP component exposes and passes its liveness, readiness and startup probes,
satisfying the `BCM-PLT-006` invariant that every deployed service must expose liveness and
readiness endpoints. Required inside `DEP-RUN-003` of `deployment-readiness-checklist.yaml` and
during incident triage.

## Applicable environment

Executable today on `local`. `dev`/`qa`/`staging`/`prod` share the same procedure once their
infrastructure exists.

## Capability traceability

- `BCM-PLT-006` — owns the `HealthCheckProbe` value object (liveness/readiness/startup, interval,
  failure threshold).
- `BCM-ORG-001` — tenant subscription-state check feeds tenant-scoped readiness.
- `BCM-PLT-001` — IAM reachability is itself part of meaningful readiness for auth-dependent paths.

## IAM and audit

Requires `health:read`; liveness checks may be anonymous. Individual probe checks are not audited;
sustained probe failures that become an incident are captured by `incident-response-runbook.md`.

## Prerequisites

- The component under check is started per `local-solution-runbook.yaml`.
- PowerShell or a compatible shell.

## Component probe matrix

| Component | Command | Expected result |
|---|---|---|
| Local infrastructure (`INF-001`) | `docker compose --env-file .env -f compose.local.yml ps` (in `07-implementation`) | postgres, redis, otel-collector all healthy/Up |
| Backend API (`BE-001`) | `Invoke-RestMethod http://localhost:8080/actuator/health` | status `UP` |
| Employee portal (`WEB-001`) | `Invoke-WebRequest http://localhost:5173` | HTTP 200 |
| OTel Collector | `Invoke-WebRequest http://localhost:13133` | HTTP 200 |

## Procedure

1. Run every applicable row of the probe matrix.
2. Record pass/fail per component with a timestamp.
3. Three consecutive failures on one component escalates to `incident-response-runbook.md`.

## Success criteria

Every applicable component reports healthy within its check interval, recovering within the
failure threshold.

## Failure criteria

Any component fails 3+ consecutive intervals, or a component expected to exist is unreachable
entirely.

## Evidence expected

Pass/fail table with UTC timestamps, retained with the deployment or incident evidence bundle.

## Responsible role

Platform operations on-call.

## If this fails

One transient failure: retry once. Sustained failure on shared/production: open
`incident-response-runbook.md` immediately and hold deployment promotion. On local/dev, consult
`local-solution-runbook.yaml` troubleshooting.

## Known gaps and forward pointers

- Backend actuator does not yet expose distinct `/actuator/health/liveness` and
  `/actuator/health/readiness` probe groups wired through an ingress layer for shared environments.
  Forward pointer: `COM-MOD-012-BE-001`.
