# COM-MOD-012-OPS-002 Security Quality Evidence

## Scope

No code, dependency, runtime or executable infrastructure changed. Operations-definition change
only (10 runbook pairs plus an index README under `09-operations/runbooks/`).

## Checks

Tests, SAST, dependency scan, DAST and container/IaC scan are not applicable — no code, dependency
or runnable surface changed. Secrets scan passed: no credential, token, password or private key
literal was introduced; the example database credentials referenced (`hop`/`hop`) are the
pre-existing, already-committed local-only defaults from `.env.example`, not new secrets. Coverage
floors are preserved unchanged since no application code changed.

## Security design controls defined

- Least-privilege operational permissions declared per runbook (`platform:backup:execute`,
  `platform:restore:execute`, `platform:rollback:execute`, `platform:incident:manage`,
  `platform:tenant:read`, `platform:incident:review`, `metrics:read`, `health:read`, `audit:read`).
- Staged approval required for any live restore or production rollback.
- Narrowest-effective-mitigation-first escalation order: feature flag, then rollback, then restore.
- Mandatory cross-tenant leakage check as a defense-in-depth compensating control pending native
  Row Level Security (materially reduces `TD-DB-004`).
- Audit trail (`GET /api/audit/events`) established as the authoritative incident timeline source.
- Evidence bundle retention aligned to `BCM-PLT-007`'s audit retention policy.
- Blameless post-incident review with mandatory tracking of durable follow-ups.
- Open-source-first backup/restore tooling: `pg_dump`/`pg_restore` locally (already bundled with the
  existing PostgreSQL image); pgBackRest recommended, not adopted, for production — no proprietary
  dependency introduced.

## Open-source-first review

No new tooling dependency was added. `pg_dump`/`pg_restore` are already part of the existing
`postgres:16-alpine` image. pgBackRest (MIT license) is documented as a future production backup
recommendation only, not adopted this backlog item — no ADR is required because no dependency was
actually introduced.

## Closure

Security quality status: passed. Ready for next backlog item: `COM-MOD-012-BE-001`.
