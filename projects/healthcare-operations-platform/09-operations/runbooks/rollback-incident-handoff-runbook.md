# HOP Rollback Incident Handoff Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Make `rollback_strategy` from `production-deployment-strategy.md` executable, and give a clean
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
`environment-matrix.md` (`product_owner_and_operations_owner` / `release_manager_and_operations_owner`).
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
   `deployment-readiness-checklist.md` execution.
3. **Confirm database compatibility.** Additive, forward-only migrations need no database action;
   otherwise a compensating script or `restore-runbook.md` is required first.
4. **Roll back the application artifact** — `git checkout <previous-known-good-commit-sha>` (local
   drill) or redeploy the previous image digest (target environments).
5. **Restart the rolled-back component** per `local-solution-runbook.md` or the environment
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-ROLLBACK-HANDOFF-001
  type: operational-runbook
  name: Rollback Incident Handoff Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: rollback-incident-handoff-runbook.md
  machine_readable: rollback-incident-handoff-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Make rollback_strategy from production-deployment-strategy.md executable
  and give the operator a clean handoff contract between "an incident is being mitigated
  via rollback" and "the rollback itself is complete and verified." This is the runbook
  incident-response-runbook.md delegates to when mitigation escalates beyond a feature-flag
  disable.

  '
applicable_environments:
  local: 'Executable as a drill: "rollback" means checking out and running the previous
    known-good commit/build, since no image-digest registry exists locally yet.'
  dev: target_posture_image_digest_rollback
  qa: target_posture_image_digest_rollback
  staging: target_posture_blue_green_or_canary_rollback
  prod: target_posture_blue_green_or_canary_rollback
capability_traceability:
  BCM-PLT-009:
    responsibility: Owns the rollback workflow state machine and approval gate.
  BCM-PLT-007:
    responsibility: rollback_strategy.application.required_controls includes audit_event_for_rollback;
      this runbook is the trigger point for that event.
  BCM-PLT-002:
    responsibility: feature_flag_disable_path is the fastest rollback lever and must
      be checked/attempted first.
  BCM-ORG-001:
    responsibility: tenant rollback strategy (tenant_allowlist, tenant_status_check,
      tenant_specific_feature_flag) applies when the rollback can be scoped to specific
      tenants.
iam_and_audit:
  required_permissions:
  - platform:rollback:execute
  minimum_role: release_manager
  approval_required: 'For staging/prod, mirrors environment-matrix.md approval_required
    (product_owner_and_operations_owner for staging, release_manager_and_operations_owner
    for prod).

    '
  audit_expectation: 'Record the triggering incident id, the previous and rolled-back-to
    release identity (git commit sha, image digest when applicable), actor, timestamp
    and post-rollback smoke test result.

    '
prerequisites:
- id: PRE-001
  name: The incident has been classified via incident-response-runbook.md and rollback
    has been selected as the mitigation
  required_for: any_rollback
- id: PRE-002
  name: Previous release identity is known (git_commit_sha, image digest, migration
    version) per production-deployment-strategy.md release_artifact_identity
  required_for: rollback_target_selection
- id: PRE-003
  name: If the release included a database migration, confirm whether it is forward-compatible
    or requires a compensating script/restore
  required_for: database_rollback_decision
procedure:
- id: ROLLBACK-STEP-001
  name: Attempt the narrowest mitigation first
  detail: Check whether a feature-flag disable (BCM-PLT-002) resolves the incident
    without a full rollback. If yes, stop here and return to incident-response-runbook.md
    verification.
- id: ROLLBACK-STEP-002
  name: Identify the previous known-good release identity
  detail: git_commit_sha, image digest (when applicable) and migration_version from
    the last passing deployment-readiness-checklist.md execution.
- id: ROLLBACK-STEP-003
  name: Confirm database compatibility for rollback
  detail: 'If the current schema is backward-compatible with the previous application
    version (additive, forward-only migrations per rollback_strategy.database.preferred),
    no database action is needed. If not, a compensating script or a restore (restore-runbook.md)
    is required before the application rollback is safe.

    '
- id: ROLLBACK-STEP-004
  name: Roll back the application artifact
  working_directory: 07-implementation/backend
  command: git checkout <previous-known-good-commit-sha>
  expected_result: 'Local/dev drill: working tree matches the previous known-good
    commit. dev/qa/staging/prod target: redeploy the previous image digest via the
    environment''s orchestration tooling (not yet provisioned).

    '
- id: ROLLBACK-STEP-005
  name: Restart the rolled-back component
  reference: local-solution-runbook.md STEP-004 (or the environment-equivalent redeploy
    action)
- id: ROLLBACK-STEP-006
  name: Run post-rollback smoke test
  reference: health-readiness-liveness-runbook.md
  detail: Re-run the relevant smoke_validation entries from local-solution-runbook.md
    (or its environment equivalent).
- id: ROLLBACK-STEP-007
  name: Record the rollback as an audit-traceable event and close the handoff
  detail: 'Until BCM-PLT-009 automates a native rollback audit event, log actor, timestamp,
    previous and target release identity, and smoke-test result in the incident evidence
    bundle (evidence-collection-runbook.md).

    '
success_criteria:
- The narrowest effective mitigation was attempted first and documented even if not
  sufficient.
- Rollback target release identity is fully specified (commit sha, digest when applicable,
  migration version).
- Post-rollback smoke test passes.
- Rollback is recorded with actor, timestamp and before/after release identity.
failure_criteria:
- Rollback executed without confirming database compatibility first.
- Post-rollback smoke test fails and no further escalation (restore or broader incident
  response) is triggered.
- No release identity is recorded for either the failed or the restored release.
evidence_expected:
- Before/after release identity, database-compatibility decision and rationale, smoke
  test results, actor and timestamp.
responsible_role: release_manager_with_platform_operations_execution
next_action_if_failed: 'If the post-rollback smoke test still fails, escalate to restore-runbook.md
  (database-level recovery) and keep the incident open in incident-response-runbook.md;
  do not close the incident on an unverified rollback.

  '
related_runbooks:
- incident-response-runbook.md
- restore-runbook.md
- tenant-impact-triage-runbook.md
- evidence-collection-runbook.md
- post-incident-review-runbook.md
known_gaps_and_forward_pointers:
- gap: No image-digest registry or blue/green-canary orchestration exists yet outside
    local drills; rollback beyond local is a target posture, not an executable mechanism
    today.
  forward_pointer: future deployment-infrastructure backlog item building on production-deployment-strategy.md.
- gap: No automated native rollback audit event exists yet; this runbook's manual
    evidence log is the interim control.
  forward_pointer: COM-MOD-012-BE-001 closed without implementing BCM-PLT-009 workflow
    orchestration (deliberately deferred, registered as TD-BE-017); a future dedicated
    workflow-engine backlog item owns automating this runbook's execution.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
