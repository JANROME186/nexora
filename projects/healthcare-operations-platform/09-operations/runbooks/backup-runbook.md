# HOP Database Backup Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Produce a restorable PostgreSQL backup with verifiable integrity, satisfying
`environment-matrix.yaml`'s production `backup_required` control and
`production-deployment-strategy.yaml`'s `pre_migration_backup` rollback control. Executable today
against the local Compose stack; the same `pg_dump`/`pg_restore` mechanics apply unchanged to
dev/qa/staging.

## Applicable environment

Executable today on `local` via `docker exec` + `pg_dump`. `dev`/`qa`/`staging` reuse the same
mechanics once provisioned. `prod` should adopt an open-source, PostgreSQL-native continuous backup
tool such as pgBackRest (MIT licensed, self-hostable) rather than a proprietary managed service,
consistent with open-source-first policy; that adoption is a future backlog item, not covered by any
ADR yet.

## Capability traceability

- `BCM-ORG-001` — a backup covers all tenants in the shared database; tenant-scoped restore is
  handled at restore time, not backup time.
- `BCM-PLT-007` — the backup action should become an audited event once `BCM-PLT-009` automates it;
  until then this runbook's execution log is the evidence of record.
- `BCM-PLT-008` — backup artifacts are operational documents subject to future Document Management
  retention policy.
- `BCM-PLT-009` — owns the eventual scheduled/automated execution of this runbook.

## IAM and audit

Requires `platform:backup:execute`, held by platform operations or the release manager. The
operator must log who ran the backup, when, against which environment, and the resulting artifact
path/checksum in the evidence bundle until this is automated and audited natively.

## Prerequisites

- Local infrastructure running with PostgreSQL healthy.
- Sufficient local disk space for the dump.
- Docker CLI available.

## Procedure

1. `docker compose --env-file .env -f compose.local.yml ps postgres` (in `07-implementation`) —
   confirm `postgres` is healthy.
2. `docker exec hop-local-postgres pg_dump -U hop -d hop -F c -f /tmp/hop-backup-<timestamp>.dump`
   — produce a timestamped custom-format dump inside the container.
3. `docker cp hop-local-postgres:/tmp/hop-backup-<timestamp>.dump
   ../08-qa/qa/platform-hardening-and-saas-operations/backups/hop-backup-<timestamp>.dump` — copy
   the dump to the host evidence location.
4. `Get-FileHash <path-to-dump> -Algorithm SHA256` — compute and record an integrity checksum.
5. `docker exec hop-local-postgres pg_restore --list /tmp/hop-backup-<timestamp>.dump` — verify the
   archive is structurally valid without a full restore.

## Success criteria

Dump produced, copied, checksummed and structurally verified via `pg_restore --list`, with the
artifact path and checksum recorded in evidence.

## Failure criteria

`pg_dump` exits non-zero or writes a zero-byte file; `pg_restore --list` reports corruption; disk
space is insufficient.

## Evidence expected

Dump file (or its retention pointer), SHA-256 checksum, timestamp, operator identity, and the
`pg_restore --list` output excerpt, stored under
`08-qa/qa/platform-hardening-and-saas-operations/backups/`.

## Responsible role

Platform operations on-call.

## If this fails

Do not proceed with any planned destructive migration or risky deployment if the required
pre-migration backup cannot be produced and verified. Escalate to `incident-response-runbook.md` if
the failure occurs outside a planned maintenance window.

## Known gaps and forward pointers

- No scheduled/automated backup job exists yet; this is a manual, on-demand procedure. Forward
  pointer: `COM-MOD-012-BE-001` and `BCM-PLT-009` workflow automation.
- No object-storage or off-host retention target is configured. Forward pointer: future
  infrastructure-provisioning backlog item.
- Backup covers the whole shared database; there is no per-tenant export path yet, though
  `BCM-PLT-010` Open Data Ingestion and Migration already provides a per-tenant export mechanism
  worth evaluating for this purpose.
