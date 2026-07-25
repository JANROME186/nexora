# HOP Database Backup Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Produce a restorable PostgreSQL backup with verifiable integrity, satisfying
`environment-matrix.md`'s production `backup_required` control and
`production-deployment-strategy.md`'s `pre_migration_backup` rollback control. Executable today
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

1. `docker compose --env-file .env -f compose.local.json ps postgres` (in `07-implementation`) —
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-BACKUP-001
  type: operational-runbook
  name: Database Backup Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: backup-runbook.md
  machine_readable: backup-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Produce a restorable PostgreSQL backup with verifiable integrity, satisfying
  minimum_resource_profiles.production.backup_required in environment-matrix.md
  and the rollback_strategy.database.required_controls.pre_migration_backup in production-deployment-strategy.md.
  Executable today against the local compose stack; the same pg_dump/pg_restore mechanics
  apply unchanged to dev/qa/staging, with an open-source-first managed tool recommended
  for prod.

  '
applicable_environments:
  local:
    tooling: pg_dump_via_docker_exec
    executable_today: true
  dev:
    tooling: pg_dump_or_managed_snapshot
    executable_today: false
  qa:
    tooling: pg_dump_or_managed_snapshot
    executable_today: false
  staging:
    tooling: pg_dump_plus_pgbackrest_rehearsal
    executable_today: false
  prod:
    tooling: pgBackRest_or_equivalent_open_source_continuous_backup
    executable_today: false
    rationale: 'pgBackRest is open source (MIT), self-hostable and PostgreSQL-native,
      avoiding a proprietary backup dependency per open-source-first policy. Adoption
      for prod is a future backlog item; no ADR has introduced a proprietary alternative.

      '
capability_traceability:
  BCM-ORG-001:
    responsibility: Tenant lifecycle context; a backup covers all tenants in a shared
      database, so tenant-scoped restore must be handled at the restore step, not
      the backup step.
  BCM-PLT-007:
    responsibility: The backup action itself must be recorded as an audit event once
      BCM-PLT-009 automates it; until then this runbook's execution log is the evidence
      of record.
  BCM-PLT-008:
    responsibility: Backup artifacts and this runbook's execution evidence are operational
      documents subject to Document Management retention policy once implemented.
  BCM-PLT-009:
    responsibility: Owns the eventual scheduled/automated execution of this runbook
      as an operational workflow.
iam_and_audit:
  required_permissions:
  - platform:backup:execute
  minimum_role: platform_operations_or_release_manager
  audit_expectation: 'Record who ran the backup, when, against which database/environment,
    and the resulting artifact path/checksum. Until BCM-PLT-009 automates and audits
    this, the operator must log the action manually in the evidence bundle (see evidence-collection-runbook.md).

    '
prerequisites:
- id: PRE-001
  name: Local infrastructure services running with PostgreSQL healthy
  required_for: pg_dump
- id: PRE-002
  name: Sufficient local disk space for the dump file
  required_for: pg_dump
- id: PRE-003
  name: Docker CLI available
  required_for: docker_exec_pg_dump
procedure:
- id: BACKUP-STEP-001
  name: Confirm PostgreSQL container is healthy
  working_directory: 07-implementation
  command: docker compose --env-file .env -f compose.local.json ps postgres
  expected_result: postgres reports healthy.
- id: BACKUP-STEP-002
  name: Produce a timestamped custom-format dump inside the container
  working_directory: 07-implementation
  command: docker exec hop-local-postgres pg_dump -U hop -d hop -F c -f /tmp/hop-backup-$(Get-Date
    -Format yyyyMMdd-HHmmss).dump
  expected_result: Command exits 0; no error output.
- id: BACKUP-STEP-003
  name: Copy the dump file out of the container to the host evidence location
  working_directory: 07-implementation
  command: docker cp hop-local-postgres:/tmp/hop-backup-<timestamp>.dump ../08-qa/qa/platform-hardening-and-saas-operations/backups/hop-backup-<timestamp>.dump
  expected_result: File exists on host with non-zero size.
- id: BACKUP-STEP-004
  name: Compute and record a checksum for integrity verification
  working_directory: repository_root
  command: Get-FileHash <path-to-dump> -Algorithm SHA256
  expected_result: SHA-256 hash recorded alongside the artifact path in evidence.
- id: BACKUP-STEP-005
  name: Verify the dump file is structurally valid without a full restore
  working_directory: repository_root
  command: docker exec hop-local-postgres pg_restore --list /tmp/hop-backup-<timestamp>.dump
  expected_result: A table-of-contents listing is printed with no "unexpected end
    of file" or corruption error.
success_criteria:
- Dump file produced, copied to evidence storage, checksummed and structurally verified
  via pg_restore --list.
- Backup artifact path and checksum recorded in evidence.
failure_criteria:
- pg_dump exits non-zero or produces a zero-byte file.
- pg_restore --list reports corruption or cannot parse the archive.
- Insufficient disk space aborts the dump.
evidence_expected:
- Dump file (or its retention pointer if rotated out), SHA-256 checksum, execution
  timestamp, operator identity, and pg_restore --list output excerpt.
- Stored under 08-qa/qa/platform-hardening-and-saas-operations/backups/ locally; a
  dedicated object-storage location for shared environments is a forward pointer.
responsible_role: platform_operations_on_call
next_action_if_failed: 'Do not proceed with any planned destructive migration or risky
  deployment if the pre-migration backup required by rollback_strategy.database in
  production-deployment-strategy.md cannot be produced and verified. Escalate to
  incident-response-runbook.md if backup failure occurs outside a planned maintenance
  window (possible disk, permissions or database corruption issue).

  '
related_runbooks:
- restore-runbook.md
- rollback-incident-handoff-runbook.md
- evidence-collection-runbook.md
known_gaps_and_forward_pointers:
- gap: No scheduled/automated backup job exists yet; this runbook is a manual, on-demand
    procedure.
  forward_pointer: COM-MOD-012-BE-001 closed without implementing BCM-PLT-009 workflow
    orchestration (deliberately deferred, registered as TD-BE-017); a future dedicated
    workflow-engine backlog item owns automating this runbook's execution.
- gap: No object-storage or off-host retention target is configured; artifacts remain
    on the local filesystem.
  forward_pointer: future infrastructure-provisioning backlog item.
- gap: Backup currently covers the entire shared database (all tenants); there is
    no per-tenant export/backup path yet.
  forward_pointer: BCM-PLT-010 Open Data Ingestion and Migration already provides
    an export mechanism for individual tenants that can be evaluated for this purpose.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
