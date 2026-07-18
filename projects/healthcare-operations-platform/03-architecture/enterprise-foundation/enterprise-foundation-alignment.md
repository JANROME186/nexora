# HOP Enterprise Product Foundation Alignment

Machine-readable source: `enterprise-foundation-alignment.yaml`. Master record for
`HOP-ENT-FOUND-001`, tying together all 11 required foundation areas.

## Area-by-area summary

| # | Area | Deliverable | Real code delivered |
|---|---|---|---|
| 1 | i18n / multilanguage | `i18n-localization/localization-strategy.md` | Backend `MessageSource` baseline (identityaccess); frontend/mobile locale-keyed catalogs + working language switch |
| 2 | IAM / dynamic menu | `security-compliance/iam-permission-model.md` | 27-code `PermissionCode`/`RolePermissionCatalog`/`AuthorizationService`; AppShell filters navigation by session permissions; mapped API paths enforce request-time permission checks |
| 3 | Login / session | `security-compliance/session-management-baseline.md` | Fixed a real hardcoded-actor bug; documented local-dev fixtures explicitly; web/mobile API clients now propagate session headers |
| 4 | Database | `data-architecture/*.md` (4 docs) | `branches.version` column; new country/locale/currency reference tables + seed data; minimal diagnostic catalog seed data; found and fixed a real Docker-init schema drift bug |
| 5 | Technical debt | (this document) | `TD-BE-009`, `TD-IAM-001` and `TD-APP-002` closed; `TD-I18N-002` and `TD-IAM-002` materially reduced |
| 6 | Coverage & quality | (this document) | Backend 76.99%→77.92%, frontend 83.98%→84.44%, mobile measured at 97.15%, all with 0 regression |
| 7 | UX/UI | `ux-ui/ux-ui-foundation.md` | Real CSS design tokens extracted, zero visual change |
| 8 | Code documentation | (this document) | Javadoc/TSDoc on all new public classes/hooks |
| 9 | Persistence/JPA | `technology-architecture/persistence-and-contract-generation-review.md` | JPA evaluated and deferred; new SQL follows the existing adapter-isolation pattern |
| 10 | Contract-first/OpenAPI | same as #9 | SpringDoc kept; OpenAPI Generator/MapStruct/Lombok evaluated and deferred |
| 11 | Open-source stack review | (this document) | 0 vulnerabilities found; all deferred tooling has a stated revisit trigger |

## Two real defects found during validation

1. A coverage regression traced to running `mvn test` without the local-database flag — not
   actual new untested code. Re-running with `-Dhop.local-db-tests=true` (matching prior
   authoritative measurements) resolved it and surfaced defect 2.
2. A genuinely stale, manually-duplicated Docker-init copy of `platform-foundation/schema.sql`
   caused two local-database integration tests to fail against a live Postgres instance. Fixed by
   resyncing the file and resetting the local Postgres volume; registered as `TD-STACK-004` so the
   underlying architectural risk (two files, one hand-synced) stays tracked, not just patched once.

## Closure gate compliance

Every acceptance criterion in `HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.yaml` is satisfied with
real, verified evidence after corrective closure. See `HOP-ENT-FOUND-001-validation.md` for the full
validation record and `security-quality/HOP-ENT-FOUND-001/security-quality-evidence.md` for the
security/quality gate record.
