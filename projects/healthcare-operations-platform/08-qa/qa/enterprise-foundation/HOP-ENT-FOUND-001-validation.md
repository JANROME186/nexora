# HOP-ENT-FOUND-001 Enterprise Product Foundation Alignment Validation

Machine-readable source: `HOP-ENT-FOUND-001-validation.yaml`.

## Objective

Align HOP with the enterprise product foundation standard across localization, IAM/dynamic menus,
login/session baseline, database deliverables, UX/UI, code documentation, persistence,
contract-first review, technical debt and coverage policy before resuming
`MVP-MOD-007-PORTAL-001`.

## Execution model

Two scoped implementation passes (backend; frontend+mobile) were run, then **personally
re-verified from a clean state** rather than accepted on the implementing agents' self-reports.
This caught and fixed two real defects — see below.

## Debt-first action

Reviewed the technical-debt index before feature work. Closed **TD-BE-009** (branch version
placeholder — `Branch` now carries a real `version` field). Materially reduced **TD-I18N-002**
(full i18n adoption — a real backend `MessageSource` baseline and frontend/mobile locale-keyed
catalogs with a working language switch now exist end to end). `TD-QA-004` was reviewed but left
untouched — reproducing it safely needs a live malformed-parameter request and carries regression
risk without that reproduction.

## Two defects found and fixed during validation

1. **Coverage measurement gap**: the first `mvn test` run (no local-DB flag) showed backend
   coverage at 76.11%, below the 76.99% floor. Root cause: every `Jdbc*Repository` class is
   skipped entirely without `-Dhop.local-db-tests=true`; the new `branches.version` logic added
   lines to an already-0%-covered class, mechanically dragging the aggregate down. Re-running with
   the local-DB flag (the methodology every prior authoritative measurement in this project used)
   surfaced defect 2.
2. **Stale duplicate schema file**: with local-DB tests enabled, two integration tests failed with
   `BadSqlGrammarException` — the live local Postgres container didn't have the new `version`
   column. Root cause: `runtime/local/postgres/init/001-create-platform-foundation-schemas.sql` is
   a hand-maintained duplicate of the authoritative `schema.sql`, mounted as a Docker init script
   that runs (and silently wins) before Spring Boot's own schema initializer. Resynced the
   duplicate, reset the local Postgres volume, reran — all 182 tests passed. Registered as
   **TD-STACK-004** so this class of risk stays tracked.

## Real code delivered

- **Backend**: `Branch`/`BranchSnapshot` versioning (TD-BE-009), actor-attributed role assignment
  (removed a hardcoded `"system"`), Spring `MessageSource` i18n baseline on `identityaccess`, a
  27-code `PermissionCode`/`RolePermissionCatalog`/`AuthorizationService` IAM domain model, and new
  `organization.countries/locales/currencies` reference tables with seed data.
- **Employee portal**: locale-keyed `es-MX`/`en-US` catalogs with a working `AppShell` language
  switch, permission-filtered navigation backed by a documented local-dev session fixture, and CSS
  design tokens.
- **Mobile app**: the same locale-keyed catalog pattern and a mirrored permission model applied to
  the route list.

A screen-count discrepancy in the original task briefs ("26 screens") was caught during
validation — `AppShell.TABS` actually has **27** entries, and both implementations independently
used the correct 27. All architecture docs were corrected from 26 to 27.

## Validation results

| Gate | Result |
|---|---|
| Backend `mvn -Pquality -Dhop.local-db-tests=true verify` | **182 tests, 0 failures/errors, 0 skipped**, coverage 76.99% → **77.32%** |
| Employee portal `npm run quality` | **31 files / 86 tests**, coverage 83.98% → **84.42%**, all 7 sub-gates passed |
| Employee portal `npm audit` | 0 vulnerabilities |
| Mobile app `npm run quality` | **6 files / 15 tests**, all passed (no coverage threshold configured — TD-APP-002) |
| Trivy filesystem scan | 0 vulnerabilities, 0 secrets, 0 misconfigurations |
| YAML parse (874 files) | 0 errors (2 fixed during this pass) |
| Agent-agnostic scan | 1 match, confirmed false positive (`cursor: pointer`) |

## Readiness

**Closed.** All 11 required foundation areas are addressed with real code and honest, explicitly
registered gaps (12 new technical-debt items, none silent). Both changed stacks improved coverage
with zero regression; the backend's shortfall against the 3-5 point target is explicitly justified.
Next backlog item: `MVP-MOD-007-PORTAL-001`.
