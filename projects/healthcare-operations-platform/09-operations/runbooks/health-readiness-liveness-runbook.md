# HOP Health, Readiness and Liveness Check Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Verify every runnable HOP component exposes and passes its liveness, readiness and startup probes,
satisfying the `BCM-PLT-006` invariant that every deployed service must expose liveness and
readiness endpoints. Required inside `DEP-RUN-003` of `deployment-readiness-checklist.md` and
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

- The component under check is started per `local-solution-runbook.md`.
- PowerShell or a compatible shell.

## Component probe matrix

| Component | Command | Expected result |
|---|---|---|
| Local infrastructure (`INF-001`) | `docker compose --env-file .env -f compose.local.json ps` (in `07-implementation`) | postgres, redis, otel-collector all healthy/Up |
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
`local-solution-runbook.md` troubleshooting.

## Known gaps and forward pointers

- Backend actuator does not yet expose distinct `/actuator/health/liveness` and
  `/actuator/health/readiness` probe groups wired through an ingress layer for shared environments.
  Forward pointer: `COM-MOD-012-BE-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-HEALTH-001
  type: operational-runbook
  name: Health, Readiness and Liveness Check Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: health-readiness-liveness-runbook.md
  machine_readable: health-readiness-liveness-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Verify that every runnable HOP component exposes and passes its liveness,
  readiness and startup probes, satisfying the BCM-PLT-006 invariant that every deployed
  service must expose /health/liveness and /health/readiness. Used standalone during
  routine checks, and as a mandatory step inside DEP-RUN-003 of deployment-readiness-checklist.md
  and inside incident triage.

  '
applicable_environments:
  local: executable_today
  dev: target_posture_infrastructure_not_yet_provisioned
  qa: target_posture_infrastructure_not_yet_provisioned
  staging: target_posture_infrastructure_not_yet_provisioned
  prod: target_posture_infrastructure_not_yet_provisioned
capability_traceability:
  BCM-PLT-006:
    responsibility: Owns HealthCheckProbe value object (LIVENESS/READINESS/STARTUP),
      check_interval_seconds and failure_threshold.
  BCM-ORG-001:
    responsibility: Tenant subscription-state check surfaced as part of readiness
      for tenant-scoped operations.
  BCM-PLT-001:
    responsibility: IAM service must itself be reachable for readiness to be meaningful
      (auth-dependent endpoints).
iam_and_audit:
  required_permissions:
  - health:read
  minimum_role: any_authenticated_operational_role_or_anonymous_for_liveness_only
  audit_expectation: Routine probe checks are not audited individually; repeated probe
    failures that trigger an incident are captured by incident-response-runbook.md.
prerequisites:
- id: PRE-001
  name: Component under check is started per its component_inventory entry in local-solution-runbook.md
  required_for: any_check
- id: PRE-002
  name: PowerShell or compatible shell
  required_for: local_commands_on_windows
component_probe_matrix:
- component_id: INF-001
  name: Local infrastructure services (PostgreSQL, Redis, OTel Collector)
  probe_type: container_healthcheck
  command: docker compose --env-file .env -f compose.local.json ps
  working_directory: 07-implementation
  expected_result: postgres, redis and otel-collector all report healthy or Up.
- component_id: BE-001
  name: HOP backend API
  probe_type: liveness_and_readiness_via_actuator
  command: Invoke-RestMethod http://localhost:8080/actuator/health
  working_directory: repository_root
  expected_result: status is UP; components.db (when present) is UP.
- component_id: WEB-001
  name: Employee portal web app
  probe_type: http_reachability
  command: Invoke-WebRequest http://localhost:5173
  working_directory: repository_root
  expected_result: HTTP 200.
- component_id: OTEL-001
  name: OpenTelemetry Collector
  probe_type: collector_health_extension
  command: Invoke-WebRequest http://localhost:13133
  working_directory: repository_root
  expected_result: HTTP 200.
procedure:
- id: HEALTH-STEP-001
  name: Run every row of component_probe_matrix relevant to the environment under
    check
- id: HEALTH-STEP-002
  name: Record pass/fail per component with timestamp
- id: HEALTH-STEP-003
  name: If any probe fails 3 consecutive times (failure_threshold default), classify
    as a candidate incident
  reference: incident-response-runbook.md
success_criteria:
- Every applicable component in component_probe_matrix reports healthy/UP/200 within
  its check_interval.
- No probe requires more than failure_threshold consecutive retries to recover.
failure_criteria:
- Any component fails its probe for 3 or more consecutive intervals.
- A component that should exist per local-solution-runbook.md component_inventory
  is unreachable entirely (process not started, port not bound).
evidence_expected:
- Pass/fail table per component with UTC timestamps, retained with the deployment
  or incident evidence bundle (see evidence-collection-runbook.md).
responsible_role: platform_operations_on_call
next_action_if_failed: 'A single transient failure: retry once. A sustained failure
  (3+ consecutive) on a shared or production environment: open incident-response-runbook.md
  immediately and do not proceed with deployment promotion per deployment-readiness-checklist.md
  DEP-RUN-003. On local/dev, consult local-solution-runbook.md troubleshooting.

  '
related_runbooks:
- observability-runbook.md
- incident-response-runbook.md
known_gaps_and_forward_pointers:
- gap: Backend actuator does not yet expose separate /actuator/health/liveness and
    /actuator/health/readiness groups (management.endpoint.health.probes.enabled=true
    is set, but the web exposure include list and Kubernetes-style probe paths are
    not yet confirmed wired end-to-end at the ingress layer for dev/qa/staging/prod).
  status: closed_by_COM_MOD_012_BE_001
  resolution: management.health.livenessstate.enabled and readinessstate.enabled explicitly
    set in application.properties; GET /actuator/health/liveness and /actuator/health/readiness
    verified reachable (200) by ObservabilityEndpointsWebTest. Ingress-layer path
    wiring for a real dev/qa/staging/prod deployment target remains a future infrastructure-provisioning
    item (no such environment exists yet).
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
