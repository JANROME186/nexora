# HOP Database Restore Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Restore a backup produced by `backup-runbook.md` into a verification target and rehearse restore
procedures before they are needed in a real incident. Satisfies
`deployment-readiness-checklist.yaml`'s `DEP-POST-004` and the `restore_runbook_reference` rollback
control in `production-deployment-strategy.yaml`. A restore is always rehearsed against an isolated
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
product-owner-and-operations-owner approval, mirroring `environment-matrix.yaml`. Record actor,
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
