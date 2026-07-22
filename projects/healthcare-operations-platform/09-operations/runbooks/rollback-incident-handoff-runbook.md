# HOP Rollback Incident Handoff Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Make `rollback_strategy` from `production-deployment-strategy.yaml` executable, and give a clean
handoff contract between "an incident is being mitigated via rollback" and "the rollback is
complete and verified." `incident-response-runbook.md` delegates here once mitigation escalates
beyond a feature-flag disable.

## Applicable environment

Executable today on `local` as a drill (checking out the previous known-good commit, since no
image-digest registry exists locally). `dev`/`qa` target image-digest rollback; `staging`/`prod`
target blue/green or canary rollback — both are target postures pending infrastructure.

## Capability traceability

- `BCM-PLT-009` — owns the rollback workflow state machine and approval gate.
- `BCM-PLT-007` — the rollback's audit event trigger point.
- `BCM-PLT-002` — feature-flag disable is the fastest lever and must be attempted first.
- `BCM-ORG-001` — tenant-scoped rollback (allowlist, status check, tenant-specific flag) applies
  when the rollback can be scoped narrowly.

## IAM and audit

Requires `platform:rollback:execute`, held by the release manager. Staging/prod approval mirrors
`environment-matrix.yaml` (`product_owner_and_operations_owner` / `release_manager_and_operations_owner`).
Record the triggering incident id, before/after release identity, actor, timestamp and
post-rollback smoke result.

## Prerequisites

- The incident is classified and rollback selected as mitigation via `incident-response-runbook.md`.
- Previous release identity is known (`git_commit_sha`, image digest, migration version).
- Database migration compatibility for the rollback has been checked.

## Procedure

1. **Attempt the narrowest mitigation first** — a feature-flag disable; stop here if it resolves
   the incident.
2. **Identify the previous known-good release identity** from the last passing
   `deployment-readiness-checklist.yaml` execution.
3. **Confirm database compatibility.** Additive, forward-only migrations need no database action;
   otherwise a compensating script or `restore-runbook.md` is required first.
4. **Roll back the application artifact** — `git checkout <previous-known-good-commit-sha>` (local
   drill) or redeploy the previous image digest (target environments).
5. **Restart the rolled-back component** per `local-solution-runbook.yaml` or the environment
   equivalent.
6. **Run the post-rollback smoke test** via `health-readiness-liveness-runbook.md` and the relevant
   smoke-validation entries.
7. **Record the rollback** with actor, timestamp, before/after release identity and smoke result in
   the incident evidence bundle.

## Success criteria

Narrowest mitigation attempted first and documented; rollback target fully specified; post-rollback
smoke test passes; rollback recorded with actor, timestamp and release identities.

## Failure criteria

Rollback executed without confirming database compatibility; failed post-rollback smoke test with
no further escalation; no release identity recorded.

## Evidence expected

Before/after release identity, database-compatibility decision and rationale, smoke results, actor
and timestamp.

## Responsible role

Release manager, with platform operations executing.

## If this fails

If the post-rollback smoke test still fails, escalate to `restore-runbook.md` and keep the incident
open — never close on an unverified rollback.

## Known gaps and forward pointers

- No image-digest registry or blue/green-canary orchestration exists yet beyond local drills.
  Forward pointer: a future deployment-infrastructure backlog item.
- No automated native rollback audit event exists yet; this runbook's manual evidence log is the
  interim control. Forward pointer: `COM-MOD-012-BE-001` and `BCM-PLT-009` workflow automation.
