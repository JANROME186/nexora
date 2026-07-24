# COM-MOD-013-QA-001 Compliance Workflow and Evidence Retention Validation

**Artifact ID**: HOP-QA-COM-MOD-013-QA-001
**Status**: passed
**Backlog Item**: COM-MOD-013-QA-001
**Module**: COM-MOD-013 Advanced Quality and Compliance
**Date**: 2026-07-24

---

## 1. Scope

Integrated validation of COM-MOD-013 after COM-MOD-013-DEF/BE-001/FE-001: backend + employee-portal
workflow validation (External Quality Controls, CAPA Management, Audit Management, Audit Trail
export, Document Management/compliance evidence, Quality Event Intake), IAM permission/menu
enforcement, es-MX/en-US localization, security and quality gates, dependency and Trivy scans,
capability traceability, technical-debt honesty check, and closure readiness for
`COM-MOD-013-CLOSEOUT`.

Confirmed `COM-MOD-013-DEF`, `COM-MOD-013-BE-001` and `COM-MOD-013-FE-001` are closed, and that
`MVP-MOD-006`, `COM-MOD-010` and `COM-MOD-012` (this module's dependencies) are `module_closed`.

---

## 2. Major Finding: TD-DB-005 (found and closed this iteration)

**COM-MOD-013's backend was silently using in-memory storage instead of PostgreSQL.**

A clean `mvn -Pquality -Dhop.local-db-tests=true clean verify` measured backend line coverage at
**82.57%**, below the registry's recorded 84.14% floor. Rather than accepting a lower "corrected"
baseline, the regression was investigated: breaking down `jacoco.csv` by package surfaced
`externalqualitycompliance.adapter.out.jdbc` at **0% coverage (0/199 lines)**.

Two compounding root causes:

1. **Missing schema registration** — `application-local.yml`'s `spring.sql.init.schema-locations`
   list registers every other module's `schema.sql` but never included
   `db/external-quality-and-compliance/schema.sql`, so the tables backing BCM-QLT-002/006/007
   were never created in local PostgreSQL.
2. **Inverted `@Profile` wiring** — the 4 real JDBC repositories were annotated
   `@Profile("!local & !test")` and their in-memory counterparts `@Profile("local | test")` — the
   *inverse* of the convention used by every other already-validated module (e.g. inventoryquality's
   JDBC repos use `@Profile("local")`). This made the real JDBC adapters unreachable under the
   "local" profile regardless of cause 1, masking it entirely.

**Combined effect**: every External Quality Control, CAPA, Audit Management and Quality Event
Intake record created through the running local backend was stored only in a process-local
in-memory map — lost on every restart — directly contradicting this module's own acceptance
criterion, "Compliance evidence is traceable, searchable and retained."

**Fix**: registered the missing schema-locations entry; corrected the 4 JDBC classes to
`@Profile("local")` and their in-memory counterparts to `@Profile("!local")`.

**Verification**: the pre-existing, unmodified `ExternalQualityComplianceLocalDatabaseTest` now
passes against real `hop-local-postgres` (it failed with `relation "external_quality_evaluations"
does not exist` when only the profile fix was applied, confirming both causes were real and both
were required). Full backend suite re-run clean — 381 tests, 0 failures/errors/skipped.
`externalqualitycompliance.adapter.out.jdbc` coverage rose from 0/199 to 146/199 (73.4%). Overall
backend line coverage rose to **84.24%**, above both the 80% final-closure target and the
previously recorded 84.14% floor. See `TD-DB-005-quality-compliance-persistence-never-wired.yaml`.

---

## 3. Other Defects Found and Fixed

| Finding | Fix |
|---|---|
| Hardcoded Spanish string `"Estado"` in `ComplianceEvidenceScreen.tsx`'s status column header | Added `complianceEvidence.status` to es-MX/en-US catalogs; screen now reads `labels.status` (materially reduces `TD-I18N-002`) |
| `ComplianceEvidenceScreen`'s main function was 129 lines (over the 120-line ESLint threshold) | Extracted `DocumentsSection` sub-component; lint warnings for the file dropped to 0 (materially reduces `TD-FE-010`; portal-wide warnings 51 → 50) |
| SpotBugs High `DM_DEFAULT_ENCODING` — `DocumentManagementController.parseUuidOrGenerate()` relied on default charset | Explicit `UTF_8` charset |
| SpotBugs High `NM_SAME_SIMPLE_NAME_AS_SUPERCLASS` — domain exception shadowed its own superclass's simple name | Renamed to `ExternalQualityDomainException` (1 definition + 5 domain-class + 3 service + 1 test usage updated) |
| SpotBugs Medium `CT_CONSTRUCTOR_THROW` × 5 (`AuditFinding`, `AuditSchedule`, `CapaInvestigation`, `ExternalQualityEvaluation`, `QualityEventIntake`) | Marked all 5 classes `final` (none subclassed anywhere) |

SpotBugs/FindSecBugs total findings dropped from 70 to 63 as a result.

---

## 4. Technical Debt Dispositioned (Not Fixed)

- **3× SpotBugs Medium `DE_MIGHT_IGNORE`** (best-effort auto-CAPA-creation `catch (Exception
  ignored)` in `AuditManagementService`/`ExternalQualityService`/`QualityEventIntakeService`) —
  **accepted risk**: intentional, must not block the primary save, consistent with the existing
  `AuditComplianceService.exportAuditEvents` convention.
- **Synthetic tenant IDs in 5 controllers** — **registered as new technical debt `TD-IAM-004`**.
  All 5 controllers assign `new TenantId(UUID.randomUUID().toString())` instead of the
  authenticated request's real tenant. Deny-by-default authorization itself is unaffected (all 5
  endpoint prefixes are registered in `EndpointPermissionRegistry`); the gap is tenant
  *attribution*, not access control. A correct fix needs a Spring Modulith module-boundary
  decision (a shared tenant-context port) out of narrow QA-validation safe-scope; deferred with a
  documented remediation path.
- **73 Checkstyle / 570 PMD / 2 CPD / 61 remaining SpotBugs findings** — tracked under the existing
  `TD-BE-002` (already `materially_reduced`, `gradual_when_backend_code_is_touched`); non-blocking
  (`failOnViolation=false` by design).

`TD-I18N-002` and `TD-FE-010` remain **materially_reduced** (further reduced this iteration), not
falsely closed — both still have applicable scope outside COM-MOD-013.

---

## 5. Capability Traceability

All 5 capability packages (BCM-QLT-002/006/007, BCM-PLT-007/008) re-validated: `openapi-source.yaml`
operations match controllers, `permissions.yaml` matches `EndpointPermissionRegistry`/
`ROLE_PERMISSION_CATALOG`, and `ui-model.yaml` screens match `App.tsx`/`AppShell.tsx`. Each
`traceability.yaml`'s `backlog_items` block confirmed DEF/BE-001/FE-001 closed with
`next_validation: COM-MOD-013-QA-001`.

## 6. IAM, Menu and i18n Validation

- **Backend**: all 5 endpoint prefixes registered in `EndpointPermissionRegistry`; deny-by-default
  enforced.
- **Frontend**: `QUALITY_MANAGER` role (plus `ADMIN`) is the only role granted the 5 new screen
  permissions; `AppShell.tsx` filters navigation via `permissions.has(SCREEN_TO_PERMISSION[...])`.
  Unauthorized roles never see these tabs.
- **i18n**: es-MX/en-US key parity confirmed via TypeScript literal widening; 1 hardcoded string
  found and fixed (above).

---

## 7. Quality Gate Results

### Backend

| Check | Result |
|---|---|
| Unit/integration tests | **381 passed**, 0 failures/errors/skipped |
| Line coverage | **84.24%** (floor 84.14%, no regression; see TD-DB-005 note) |
| Checkstyle | 73 findings, non-blocking, tracked under TD-BE-002 |
| PMD / CPD | 570 violations / 2 duplications, non-blocking, tracked under TD-BE-002 |
| SpotBugs / FindSecBugs | 63 findings (down from 70), 0 remaining High severity |
| OWASP Dependency-Check | 72 dependencies, **0 vulnerable**; local DB freshness 2026-07-20, not refreshed this session |
| SBOM (CycloneDX) | 110 components |
| duplicate-finder | 0 conflicts |
| Trivy fs (backend) | 0 vulnerabilities / 0 secrets / 0 misconfigurations |

### Employee Portal

| Check | Result |
|---|---|
| Typecheck | Passed |
| Lint | 0 errors, 50 warnings (down from 51) |
| Tests | 187 passed, 60 files, 0 failures |
| Coverage | **89.75%** (previous 89.74%, no regression) |
| Build | Passed |
| Duplication (jscpd) | Passed |
| Format check | Passed |
| License check | MIT 5, UNLICENSED 1 (own package) |
| npm audit | **0 vulnerabilities** |
| Trivy fs (employee-portal) | 0 vulnerabilities / 0 secrets / 0 misconfigurations |

### Repo-Wide

- Trivy fs (backend, doctor-portal, employee-portal, patient-portal, public-website): **0
  vulnerabilities / 0 secrets / 0 misconfigurations**.
- YAML parse: 1,263 files, 0 errors.
- Agent-agnostic scan: 0 real hits.

---

## 8. Decision

**Status**: closed
**Next backlog item**: `COM-MOD-013-CLOSEOUT`

All closure criteria met: capabilities validated, no unresolved vulnerabilities of any severity,
coverage above the previous floor and target, at least one technical-debt item materially reduced
(two: TD-I18N-002 and TD-FE-010) plus one fully closed (TD-DB-005), no stale pointers after sweep,
repository clean after commit.
