# HOP Database Restore Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Restore a backup produced by `backup-runbook.md` into a verification target and rehearse restore
procedures before they are needed in a real incident. Satisfies
`deployment-readiness-checklist.md`'s `DEP-POST-004` and the `restore_runbook_reference` rollback
control in `production-deployment-strategy.md`. A restore is always rehearsed against an isolated
target first — restoring directly over a live database is a separately-approved incident action,
never the default path.

## Applicable environment

Executable today on `local` via `pg_restore` into an isolated verification database.
`dev`/`qa`/`staging` reuse the same mechanics once provisioned; `prod` should adopt an
open-source point-in-time recovery tool (for example pgBackRest).

## Capability traceability

- `BCM-ORG-001` — a full-database restore affects every tenant; isolation must be re-verified
  afterward via `tenant-impact-triage-runbook.md`.
- `BCM-PLT-007` — the restore action, reason and scope should become an audited event once
  automated.
- `BCM-PLT-009` — owns the eventual approval workflow around production restore execution.
- `BCM-PLT-001` — live-target restore requires the platform operations or release manager role;
  emergency break-glass access applies only to the credential-compromise incident scenario.

## IAM and audit

Requires `platform:restore:execute`. Restoring into any live shared/staging/prod target requires
product-owner-and-operations-owner approval, mirroring `environment-matrix.md`. Record actor,
timestamp, backup artifact (path + checksum), target and outcome.

## Prerequisites

- A verified backup artifact (see `backup-runbook.md` step 5).
- An isolated restore target, separate from the live database, for rehearsal.
- Explicit recorded approval before restoring into any live target.

## Procedure

1. `docker exec hop-local-postgres createdb -U hop hop_restore_verify` — create an isolated
   verification database.
2. `docker exec hop-local-postgres pg_restore -U hop -d hop_restore_verify --clean --if-exists
   /tmp/hop-backup-<timestamp>.dump` — restore into it.
3. `docker exec hop-local-postgres psql -U hop -d hop_restore_verify -c "SELECT count(*) FROM
   identity.user_accounts;"` — verify a sample row count against the known-good baseline.
4. `docker exec hop-local-postgres dropdb -U hop hop_restore_verify` — drop the verification
   database after a successful rehearsal.
5. Live restore only, with recorded approval: stop the backend, restore into the live database,
   restart, then run `tenant-impact-triage-runbook.md`.

## Success criteria

Rehearsal completes without fatal errors and sample row counts match the baseline. For a live
restore, the backend passes health and tenant-impact-triage checks immediately after restart.

## Failure criteria

`pg_restore` reports fatal errors or leaves the target inconsistent; row counts diverge without an
explained, approved data-loss window; a live restore runs without recorded approval.

## Evidence expected

Restore command output, row-count verification, timestamp, operator identity, and — for live
restores — the approval record plus the tenant-impact-triage result.

## Responsible role

Platform operations on-call, with release-manager approval for live targets.

## If this fails

If rehearsal fails, treat the backup as unverified and re-run `backup-runbook.md`; do not proceed
with the deployment/migration that required it. If a live restore fails mid-incident, escalate
immediately within `incident-response-runbook.md` and fall back to the next-most-recent verified
backup.

## Known gaps and forward pointers

- No automated restore-rehearsal schedule exists yet; this is manual and on-demand. Forward
  pointer: `COM-MOD-012-BE-001` and `BCM-PLT-009` workflow automation.
- No point-in-time recovery capability is configured; only full-dump restore is exercised. Forward
  pointer: pgBackRest or equivalent WAL-archiving adoption for staging/prod.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-RESTORE-001
  type: operational-runbook
  name: Database Restore Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: restore-runbook.md
  machine_readable: restore-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Restore a PostgreSQL backup produced by backup-runbook.md into a verification
  target, and rehearse restore procedures before they are needed in a real incident.
  Satisfies deployment-readiness-checklist.md''s DEP-POST-004 (latest restore rehearsal
  evidence attached) and rollback_strategy.database.required_controls.restore_runbook_reference
  in production-deployment-strategy.md. A restore is always rehearsed against an
  isolated target first; restoring directly over a live database is an explicit, separately-approved
  incident action, not the default path.

  '
applicable_environments:
  local:
    tooling: pg_restore_via_docker_exec_into_isolated_database
    executable_today: true
  dev:
    tooling: pg_restore_or_managed_snapshot_restore
    executable_today: false
  qa:
    tooling: pg_restore_or_managed_snapshot_restore
    executable_today: false
  staging:
    tooling: pg_restore_full_rehearsal_required_before_prod_promotion
    executable_today: false
  prod:
    tooling: pgBackRest_or_equivalent_open_source_point_in_time_recovery
    executable_today: false
capability_traceability:
  BCM-ORG-001:
    responsibility: A full-database restore affects every tenant; tenant isolation
      must be re-verified after restore (see tenant-impact-triage-runbook.md).
  BCM-PLT-007:
    responsibility: The restore action, its reason and its scope must be recorded
      as an audit event once BCM-PLT-009 automates it.
  BCM-PLT-009:
    responsibility: Owns the eventual workflow/approval gate around production restore
      execution.
  BCM-PLT-001:
    responsibility: Restore into a live target requires the platform_operations or
      release_manager role; a compromised-credential incident is the one scenario
      where restore may run under emergency break-glass access per production-deployment-strategy.md
      configuration_precedence.
iam_and_audit:
  required_permissions:
  - platform:restore:execute
  minimum_role: platform_operations_or_release_manager
  approval_required_for_live_target: product_owner_and_operations_owner (mirrors staging/prod
    approval_required in environment-matrix.md)
  audit_expectation: Record actor, timestamp, backup artifact used (path + checksum),
    restore target and outcome.
prerequisites:
- id: PRE-001
  name: A verified backup artifact exists (see backup-runbook.md BACKUP-STEP-005)
  required_for: any_restore
- id: PRE-002
  name: An isolated restore target (a separate database/container) for rehearsal,
    distinct from the live database
  required_for: rehearsal_restore
- id: PRE-003
  name: Explicit approval recorded before restoring into any live shared/staging/prod
    target
  required_for: live_restore
procedure:
- id: RESTORE-STEP-001
  name: Create an isolated verification database inside the running PostgreSQL container
  working_directory: 07-implementation
  command: docker exec hop-local-postgres createdb -U hop hop_restore_verify
  expected_result: Database hop_restore_verify created.
- id: RESTORE-STEP-002
  name: Restore the backup artifact into the isolated verification database
  working_directory: 07-implementation
  command: docker exec hop-local-postgres pg_restore -U hop -d hop_restore_verify
    --clean --if-exists /tmp/hop-backup-<timestamp>.dump
  expected_result: pg_restore completes with 0 fatal errors (warnings about missing
    objects on an empty target are expected).
- id: RESTORE-STEP-003
  name: Verify row counts on a sample of tenant-scoped tables against the pre-backup
    source
  working_directory: repository_root
  command: docker exec hop-local-postgres psql -U hop -d hop_restore_verify -c "SELECT
    count(*) FROM identity.user_accounts;"
  expected_result: Row count is consistent with the backup's known-good baseline.
- id: RESTORE-STEP-004
  name: Drop the verification database after successful rehearsal
  working_directory: 07-implementation
  command: docker exec hop-local-postgres dropdb -U hop hop_restore_verify
  expected_result: Verification database removed; no residual data outside the intended
    evidence artifacts.
- id: RESTORE-STEP-005
  name: (Live restore only, with recorded approval) Stop the backend, restore into
    the live database, restart, then run tenant-impact-triage-runbook.md
  reference: tenant-impact-triage-runbook.md
  when: An incident requires restoring the actual live database, not a rehearsal.
success_criteria:
- Restore rehearsal completes without fatal errors and sample row counts match the
  known-good baseline.
- For a live restore, the backend passes health-readiness-liveness-runbook.md and
  tenant-impact-triage-runbook.md immediately after restart.
failure_criteria:
- pg_restore reports fatal errors or the target database is left in an inconsistent
  state.
- Post-restore row counts diverge from the expected baseline without an explained,
  approved data-loss window.
- A live restore is executed without recorded approval.
evidence_expected:
- Restore command output, row-count verification results, timestamp, operator identity,
  and (for live restores) the approval record and the tenant-impact-triage-runbook.md
  result.
responsible_role: platform_operations_on_call_with_release_manager_approval_for_live_targets
next_action_if_failed: 'If rehearsal fails, do not proceed with the deployment or
  migration that required a restorable backup; treat the backup itself as unverified
  and re-run backup-runbook.md. If a live restore fails mid-incident, escalate immediately
  within incident-response-runbook.md and consider the next-most-recent verified
  backup.

  '
related_runbooks:
- backup-runbook.md
- rollback-incident-handoff-runbook.md
- tenant-impact-triage-runbook.md
- incident-response-runbook.md
known_gaps_and_forward_pointers:
- gap: No automated restore-rehearsal schedule exists; rehearsal is manual and on-demand.
  forward_pointer: COM-MOD-012-BE-001 closed without implementing BCM-PLT-009 workflow
    orchestration (deliberately deferred, registered as TD-BE-017); a future dedicated
    workflow-engine backlog item owns automating this runbook's execution.
- gap: No point-in-time recovery (PITR) capability is configured; only full-dump restore
    is exercised.
  forward_pointer: adoption of pgBackRest or equivalent WAL-archiving tool for staging/prod.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
