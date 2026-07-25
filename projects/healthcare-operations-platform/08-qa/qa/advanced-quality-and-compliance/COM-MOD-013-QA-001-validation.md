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

1. **Missing schema registration** — `application-local.properties`'s `spring.sql.init.schema-locations`
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
backend line coverage rose to 84.24% at that point (later 84.25% after the DAST fix in Section 3a),
above both the 80% final-closure target and the previously recorded 84.14% floor. See
`TD-DB-005-quality-compliance-persistence-never-wired.md`.

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

## 3a. DAST (OWASP ZAP) — required for this validation-level backlog item

Backend and employee-portal are both runnable surfaces, so DAST was executed for real, not marked
`not_applicable`. Port `8080` was occupied by an unrelated pre-existing process on this shared
machine (same documented conflict as prior sessions); the backend was started on `server.port=8090`
for this session only. `vite.config.ts`'s dev-proxy target, previously hardcoded to
`localhost:8080` with no override mechanism, now reads an optional `HOP_BACKEND_URL` environment
variable (falls back to the existing `http://localhost:8080` default when unset) — a small,
backward-compatible fix so the portal could be pointed at the 8090 backend for this session and any
future one with the same conflict.

| Scan | Target | First run | Fix | Re-scan |
|---|---|---|---|---|
| `zap-api-scan.py` | `http://host.docker.internal:8090/v3/api-docs` (939 URLs — full backend, all COM-MOD-013 endpoints included) | FAIL-NEW 0, **WARN-NEW 2** (Buffer Overflow: `POST /api/documents` returned 500 when a client abruptly disconnected mid multipart upload) | `GlobalExceptionHandler.handleMultipartException()` added, mapping `MultipartException` → 400; regression test added (`TD-QA-007`, closed) | **FAIL-NEW 0, WARN-NEW 0, PASS 118** |
| `zap-baseline.py -j` (Ajax Spider) | `http://host.docker.internal:5173` (125 URLs — employee-portal) | FAIL-NEW 0, WARN-NEW 6 | none needed | all 6 map to the already-registered `TD-FE-005` (CSP/COEP, deferred to production hosting) or dev-server-only artifacts (Vite HMR token, dev-mode source comments, dev-asset caching, an informational scan-tuning hint) |

Reports: `zap-backend-api.html/json`, `zap-employee-portal.html/json` under
`08-qa/security-quality/COM-MOD-013-QA-001/`.

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

All 5 capability packages (BCM-QLT-002/006/007, BCM-PLT-007/008) re-validated: `openapi-source.md`
operations match controllers, `permissions.md` matches `EndpointPermissionRegistry`/
`ROLE_PERMISSION_CATALOG`, and `ui-model.md` screens match `App.tsx`/`AppShell.tsx`. Each
`traceability.md`'s `backlog_items` block confirmed DEF/BE-001/FE-001 closed with
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
| Unit/integration tests | **382 passed**, 0 failures/errors/skipped |
| Line coverage | **84.25%** (floor 84.14%, no regression; see TD-DB-005 and TD-QA-007 notes) |
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

### DAST

- Backend ZAP API scan (939 URLs): **FAIL-NEW 0, WARN-NEW 0** after fixing `TD-QA-007`.
- Employee-portal ZAP baseline scan (125 URLs, Ajax Spider): **FAIL-NEW 0**, 6 WARN-NEW all
  dispositioned (TD-FE-005 or dev-server-only artifacts).

### Repo-Wide

- Trivy fs (backend, doctor-portal, employee-portal, patient-portal, public-website): **0
  vulnerabilities / 0 secrets / 0 misconfigurations**.
- YAML parse: 1,263 files, 0 errors.
- Agent-agnostic scan: 0 real hits.

---

## 8. Decision

**Status**: closed
**Next backlog item**: `COM-MOD-013-CLOSEOUT`

All closure criteria met: capabilities validated, DAST executed against both runnable surfaces (no
`not_applicable` disposition), no unresolved vulnerabilities of any severity, coverage above the
previous floor and target, at least one technical-debt item materially reduced (two: TD-I18N-002
and TD-FE-010) plus two fully closed (TD-DB-005, TD-QA-007), no stale pointers after sweep,
repository clean after commit.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-013-QA-001
  type: qa-validation-evidence
  name: COM-MOD-013-QA-001 Compliance Workflow and Evidence Retention Validation
  version: 1.0.0
  status: passed
  captured_on: 2026-07-24
backlog_item:
  id: COM-MOD-013-QA-001
  module: COM-MOD-013
  module_name: Advanced Quality and Compliance
  status: closed
  scope: 'Integrated validation of COM-MOD-013 after COM-MOD-013-DEF/BE-001/FE-001:
    backend + employee-portal workflow validation (External Quality Controls, CAPA
    Management, Audit Management, Audit Trail export, Document Management/compliance
    evidence, Quality Event Intake), IAM permission/menu enforcement, es-MX/en-US
    localization, security and quality gates, dependency and Trivy scans, capability
    traceability, technical-debt honesty check, and closure readiness for COM-MOD-013-CLOSEOUT.'
capabilities:
- BCM-QLT-002 External Quality Controls
- BCM-QLT-006 CAPA Management
- BCM-QLT-007 Audit Management
- BCM-PLT-007 Audit Trail (extended)
- BCM-PLT-008 Document Management (extended)
preflight:
  loaded_sources:
  - AGENT_BOOTSTRAP.md
  - PROJECT_STATE.md
  - SOURCE_OF_TRUTH.md
  - nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
  - nexora-framework/02-standards/standards/agent-agnostic-standard.md
  - nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  - nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  - projects/healthcare-operations-platform/PROJECT_STATE.md
  - projects/healthcare-operations-platform/SOURCE_OF_TRUTH.md
  - projects/healthcare-operations-platform/03-architecture/technology-architecture/local-toolchain-inventory.md
  - projects/healthcare-operations-platform/08-qa/technical-debt/technical-debt-index.md
  - projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  - projects/healthcare-operations-platform/09-operations/runbooks/local-solution-runbook.md
  - 01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/
  - 01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/
  - 01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/
  - 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  - 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  - 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-DEF-validation.md
  - 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-BE-001-validation.md
  - 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-FE-001-validation.md
  environment_check:
    java: 21.0.7 LTS (Oracle)
    maven: 3.9.11
    node: 24.8.0
    npm: 11.6.0
    docker: 29.6.1, hop-local-postgres/hop-local-redis/hop-local-otel-collector reachable
    trivy: 0.72.0
    conclusion: all_required_tooling_present_no_support_request_needed
  closure_prerequisite_check:
    com_mod_013_def_status: closed (08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-DEF-validation.md)
    com_mod_013_be_001_status: closed (08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-BE-001-validation.md)
    com_mod_013_fe_001_status: closed (08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-FE-001-validation.md)
    dependency_modules_status: MVP-MOD-006, COM-MOD-010 and COM-MOD-012 all module_closed
      per capability-package-index.md.
  debt_first_review:
    reviewed: true
    applicable_open_or_reducible_items_before_work:
    - TD-I18N-002
    - TD-FE-010
    - TD-BE-002
    action: Materially reduced TD-I18N-002 (removed a real hardcoded "Estado" string
      in ComplianceEvidenceScreen.tsx, externalized to es-MX/en-US) and TD-FE-010
      (extracted DocumentsSection from ComplianceEvidenceScreen.tsx, bringing its
      main function under the 120-line ESLint threshold; 51 -> 50 warnings). A far
      larger, previously-undetected defect was found and fully closed during this
      item's own coverage-regression investigation (see TD-DB-005 below), exceeding
      the minimum debt-first requirement for this iteration.
runtime_environment_note: No port workaround was required this session; validation
  ran via direct Maven/npm/Trivy command execution against the already-running hop-local-postgres/hop-local-redis/hop-local-otel-collector
  containers (no full application boot needed for this item's scope).
major_finding_and_fix:
  id: TD-DB-005
  title: COM-MOD-013 backend silently used in-memory storage instead of PostgreSQL
  how_found: A clean `mvn -Pquality -Dhop.local-db-tests=true clean verify` run measured
    backend line coverage at 82.57%, below the registry's recorded 84.14% floor. Rather
    than accepting a lower "corrected" baseline (the pattern used by prior items when
    a jacoco.exec measurement-inflation bug was found), the regression was investigated
    by breaking down jacoco.csv by package, which surfaced `externalqualitycompliance.adapter.out.jdbc`
    at 0% coverage (0/199 lines).
  root_cause_1: '`application-local.properties`''s `spring.sql.init.schema-locations` list
    registers every other module''s schema.sql but never included `db/external-quality-and-compliance/schema.sql`,
    so the tables backing BCM-QLT-002/006/007 (external_quality_evaluations, capa_investigations,
    audit_schedules, audit_findings, quality_event_intakes) were never created in
    local PostgreSQL.'
  root_cause_2: The 4 real JDBC repository implementations were annotated `@Profile("!local
    & !test")` and their in-memory counterparts `@Profile("local | test")` -- the
    inverse of the convention used by every other already-validated module (e.g. inventoryquality's
    Jdbc*Repository classes use `@Profile("local")`). This made the real JDBC adapters
    unreachable under the "local" profile regardless of root cause 1, masking it entirely.
  combined_effect: Every External Quality Control, CAPA, Audit Management and Quality
    Event Intake record created through the running local backend was silently stored
    in a process-local in-memory map, never in PostgreSQL -- lost on every restart
    -- directly contradicting COM-MOD-013's own acceptance criterion "Compliance evidence
    is traceable, searchable and retained."
  fix: Added the missing schema-locations entry; corrected the 4 Jdbc*Repository classes
    to `@Profile("local")` and their 4 in-memory counterparts to `@Profile("!local")`.
  verification: 'Re-ran the pre-existing, unmodified ExternalQualityComplianceLocalDatabaseTest
    against real hop-local-postgres: both tests now pass, exercising real INSERT/SELECT
    SQL against the newly-created tables (previously failed with "relation external_quality_evaluations
    does not exist" when the profile fix alone was applied without the schema fix,
    confirming both root causes were real and both were needed). Full backend suite
    re-run clean: 381 tests, 0 failures/errors/skipped. externalqualitycompliance.adapter.out.jdbc
    line coverage rose from 0/199 (0%) to 146/199 (73.4%). Overall backend line coverage
    rose from the clean-rebuild 82.57% to 84.24%, now above both the 80% final-closure
    target and the previously recorded 84.14% floor.'
  status: closed
  evidence_file: 08-qa/technical-debt/TD-DB-005-quality-compliance-persistence-never-wired.md
other_defects_found_and_fixed:
- id: hardcoded-string
  finding: ComplianceEvidenceScreen.tsx's evidenceColumns() hardcoded the status column
    header as the literal Spanish string "Estado" instead of a locale key, despite
    COM-MOD-013-FE-001's own evidence claiming "complete i18n message catalogs (es-MX
    / en-US)".
  fix: Added `complianceEvidence.status` to both es-MX.ts ("Estado") and en-US.ts
    ("Status"); ComplianceEvidenceScreen.tsx now reads `labels.status`.
  debt_contribution: materially reduces TD-I18N-002
- id: td-fe-010-function-size
  finding: ComplianceEvidenceScreen's main function was 129 lines, over the enterprise
    quality profile's 120-line max-lines-per-function ESLint threshold.
  fix: Extracted the compliance-documents list (heading, load button, status banner,
    empty state, DataTable) into a new DocumentsSection sub-component, mirroring the
    established COM-MOD-010-FE-001 decomposition pattern.
  verification: npm run lint warnings for this file dropped to 0; portal-wide warnings
    51 -> 50.
  debt_contribution: materially reduces TD-FE-010
- id: spotbugs-dm-default-encoding
  finding: 'SpotBugs High: DocumentManagementController.parseUuidOrGenerate() called
    String.getBytes() relying on the platform default charset (DM_DEFAULT_ENCODING).'
  fix: explicit `val.getBytes(java.nio.charset.StandardCharsets.UTF_8)`.
- id: spotbugs-nm-same-simple-name
  finding: 'SpotBugs High: externalqualitycompliance.domain.ExternalQualityComplianceException
    shadowed the simple name of its superclass externalqualitycompliance.ExternalQualityComplianceException
    (NM_SAME_SIMPLE_NAME_AS_SUPERCLASS), a confusing/error-prone naming collision.'
  fix: Renamed the domain-package subclass to ExternalQualityDomainException across
    its 1 definition, 5 same-package domain-class usages, 3 application-service usages
    and 1 test file.
- id: spotbugs-ct-constructor-throw
  finding: 'SpotBugs Medium (5 instances): AuditFinding, AuditSchedule, CapaInvestigation,
    ExternalQualityEvaluation and QualityEventIntake constructors validate arguments
    and throw before full initialization (CT_CONSTRUCTOR_THROW / potential finalizer-attack
    surface).'
  fix: Marked all 5 classes `final` (confirmed none are subclassed anywhere in the
    backend), the standard SpotBugs-recognized mitigation for this finding.
- id: dast-multipart-exception-500
  finding: 'OWASP ZAP API scan Buffer Overflow (Medium) probe: an abrupt client disconnect
    mid multipart upload to POST /api/documents surfaced an unhandled MultipartException
    as HTTP 500.'
  fix: Added GlobalExceptionHandler.handleMultipartException() mapping MultipartException
    (incl. MaxUploadSizeExceededException) to 400; added a regression test. Confirmed
    by a clean ZAP re-scan (0 FAIL-NEW, 0 WARN-NEW).
  technical_debt: TD-QA-007, closed same iteration.
technical_debt_dispositioned_not_fixed:
- finding: 'SpotBugs Medium DE_MIGHT_IGNORE (3 instances): AuditManagementService.recordAuditFinding,
    ExternalQualityService.scoreEvaluation and QualityEventIntakeService.ingestEvent
    each wrap a best-effort automatic CAPA-creation call in `catch (Exception ignored)
    {}`.'
  disposition: accepted_risk
  reason: Intentional best-effort side effect (auto-opening a CAPA investigation)
    that must not block the primary audit-finding/evaluation/event save if it fails;
    consistent with the existing `catch (Exception ignored)` convention already used
    by AuditComplianceService.exportAuditEvents for its own best-effort document-upload
    step. Non-blocking per default_thresholds.sast_policy.medium (fail_unless_dispositioned_by_module_risk);
    no unhandled exception reaches the client in either path (verified against controller
    test expectations).
- finding: 5 controllers (ExternalQualityController, CapaManagementController, AuditManagementController,
    QualityEventIntakeController, DocumentManagementController) assign a synthetic
    random TenantId instead of the authenticated request's real tenant.
  disposition: registered_as_new_technical_debt
  id: TD-IAM-004
  reason: A correct fix requires a Spring Modulith module-boundary decision (a shared
    tenant-context port, most naturally sharedkernel-owned) that does not exist yet
    and is out of narrow QA-validation safe-scope; deny-by-default authorization itself
    is unaffected (all 5 endpoint prefixes are registered in EndpointPermissionRegistry).
    See 08-qa/technical-debt/TD-IAM-004-quality-compliance-controllers-synthetic-tenant.md.
- finding: 73 Checkstyle findings (mostly 140-char LineLength, a few AvoidStarImport/UnusedImports),
    570 PMD violations, 2 CPD duplications, and 61 remaining SpotBugs findings (Low/Medium,
    pre-existing and repo-wide) with failOnViolation=false.
  disposition: tracked_under_existing_TD_BE_002
  reason: TD-BE-002 ("Configure backend Java/Maven static analysis and SAST toolchain")
    is already status materially_reduced with an explicit gradual_when_backend_code_is_touched
    remediation strategy; these non-blocking findings (checkstyle/pmd fail_on_violation
    is false by design) are consistent with that existing disposition, not a new gap.
capability_traceability_validation:
  method: Re-verified all 5 capability packages' traceability.md files reference
    COM-MOD-013-DEF/ BE-001/FE-001 as closed and COM-MOD-013-QA-001 as next_validation;
    cross-checked openapi-source.md operations against controllers, permissions.md
    against EndpointPermissionRegistry/ROLE_PERMISSION_CATALOG, and ui-model.md
    screens against App.tsx/AppShell.tsx.
  results:
  - capability_id: BCM-QLT-002
    status: validated
    detail: ExternalQualityController exposes listExternalQualityEvaluations/ createExternalQualityEvaluation/scoreExternalQualityEvaluation,
      matching openapi-source.md; SCREEN_EXTERNAL_QUALITY_CONTROLS wired end to
      end (backend EndpointPermissionRegistry, frontend SCREEN_TO_PERMISSION/QUALITY_MANAGER
      role).
  - capability_id: BCM-QLT-006
    status: validated
    detail: CapaManagementController exposes the full CUS-CAP lifecycle (create, RCA,
      approve, verify); end-to-end round trip re-verified live against real PostgreSQL
      (ExternalQualityComplianceLocalDatabaseTest).
  - capability_id: BCM-QLT-007
    status: validated
    detail: AuditManagementController exposes create/get/list/recordFinding/close;
      TD-BE-016's audit-trail export closure (COM-MOD-013-BE-001) re-confirmed still
      closed.
  - capability_id: BCM-PLT-007
    status: validated
    detail: AuditComplianceService.searchEventsFiltered/exportAuditEvents confirmed
      functional (category, complianceCorrelationId, qualityInvestigationId, date-range
      filters); export creates a real StoredDocument via DocumentManagementService.
  - capability_id: BCM-PLT-008
    status: validated
    detail: DocumentManagementController exposes upload/metadata/download/legal-hold/evidence-package;
      the DM_DEFAULT_ENCODING finding in its uploadDocument path was found and fixed
      by this item.
iam_and_menu_validation:
  backend:
    method: Confirmed all 5 endpoint prefixes (/api/quality/external-controls, /api/quality/capa,
      /api/quality/audits, /api/quality/events, /api/documents) are registered in
      EndpointPermissionRegistry, so HopAuthorizationInterceptor enforces deny-by-default
      authorization for every sensitive action in this module (unaffected by the TD-IAM-004
      tenant-attribution finding, which is about data attribution, not access control).
    result: passed
  frontend:
    method: Confirmed permissions.ts defines SCREEN_EXTERNAL_QUALITY_CONTROLS/SCREEN_CAPA_MANAGEMENT/
      SCREEN_AUDIT_MANAGEMENT/SCREEN_COMPLIANCE_EVIDENCE/SCREEN_QUALITY_EVENT_INTAKE
      and grants them (plus SCREEN_AUDIT_EVENTS) exclusively to the QUALITY_MANAGER
      role (and ADMIN, which is granted every permission by construction); AppShell.tsx
      filters nav tabs via `permissions.has(SCREEN_TO_PERMISSION[tab.key])`, so unauthorized
      roles never see these tabs.
    result: passed
    note: The employee-portal has no production login/session mechanism yet (documented,
      pre-existing, portal-wide limitation in SessionContext.tsx, not specific to
      COM-MOD-013); this was already a known state before this item and is unchanged
      by it.
i18n_validation:
  method: Confirmed es-MX/en-US key parity for all COM-MOD-013 screens via TypeScript
    literal widening (MessageCatalog type); manually reviewed all 5 screen source
    files plus AuditEventsScreen.tsx for hard-coded visible text.
  finding: 1 hardcoded string found and fixed (see other_defects_found_and_fixed above).
  result: passed_after_fix
dast_validation:
  status: passed
  scope: 'Real OWASP ZAP DAST against a running backend and employee-portal, covering
    all COM-MOD-013 runnable surfaces: /api/quality/external-controls, /api/quality/capa,
    /api/quality/audits, /api/quality/events, /api/documents, /api/audit/events/export,
    and the 5 COM-MOD-013 employee-portal screens (part of the client-routed SPA shell
    scanned by the baseline crawler).'
  environment_setup: Infrastructure already running (hop-local-postgres/hop-local-redis/hop-local-otel-collector).
    Backend started via `mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments=-Dserver.port=8090`
    (port 8080 occupied by an unrelated pre-existing process on this shared machine;
    session-only workaround, no runbook/config port change). Employee-portal started
    via `HOP_BACKEND_URL=http://localhost:8090 npm run dev -- host 127.0.0.1`; vite.config.ts's
    dev-proxy target, previously hardcoded to localhost:8080 with no override, now
    reads an optional HOP_BACKEND_URL env var (falls back to the existing http://localhost:8080
    default when unset) -- a small, backward-compatible fix enabling this and future
    sessions to point the portal at an alternate backend port without editing source.
  backend_api_scan:
    tool: OWASP ZAP (Docker ghcr.io/zaproxy/zaproxy:stable) zap-api-scan.py
    command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/COM-MOD-013-QA-001:/zap/wrk"
      ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8090/v3/api-docs
      -f openapi -r zap-backend-api.html -J zap-backend-api.json
    target: full backend OpenAPI surface, 939 URLs imported from 379 operations
    first_run: FAIL-NEW 0, WARN-NEW 2, PASS 117
    finding: 'Buffer Overflow (Medium) active-scan probe against POST /api/documents
      (BCM-PLT-008) abruptly closed the TCP connection mid multipart-body transmission;
      Spring''s multipart parser surfaced this as an unhandled org.springframework.web.multipart.MultipartException,
      which GlobalExceptionHandler had no mapping for, so it fell through to the default
      500 handler. Reproduced manually: a plain oversized multipart upload already
      returned 400 correctly (an existing IllegalArgumentException path), but an abrupt
      mid-stream disconnect specifically triggered the unmapped MultipartException
      path.'
    fix: Added GlobalExceptionHandler.handleMultipartException(), mapping MultipartException
      (which also covers MaxUploadSizeExceededException) to 400 with the same body
      shape as the existing invalid-parameter handler. Added GlobalExceptionHandlerTest.mapsMultipartExceptionToBadRequestBody
      (382 backend tests total, 0 failures). Backend restarted with the fix.
    rescan: FAIL-NEW 0, WARN-NEW 0, PASS 118
    technical_debt: TD-QA-007, registered and closed same iteration.
    report_files:
    - 08-qa/security-quality/COM-MOD-013-QA-001/zap-backend-api.html
    - 08-qa/security-quality/COM-MOD-013-QA-001/zap-backend-api.json
  employee_portal_baseline_scan:
    tool: OWASP ZAP (Docker ghcr.io/zaproxy/zaproxy:stable) zap-baseline.py with Ajax
      Spider (-j)
    command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/COM-MOD-013-QA-001:/zap/wrk"
      ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:5173
      -r zap-employee-portal.html -J zap-employee-portal.json -m 2 -j
    target: employee-portal dev server, 125 URLs (spider + Ajax spider, 2-minute max
      duration each)
    result: FAIL-NEW 0, WARN-NEW 6, PASS 61
    findings_disposition: Content Security Policy Header Not Set [10038] and Cross-Origin-Embedder-Policy
      Header Missing [90004] both match the already-registered TD-FE-005 (intentionally
      deferred to the production hosting layer; Vite's dev-server eval-based HMR requires
      relaxed CSP/COEP, documented in vite.config.ts). The remaining 4 (Information
      Disclosure - Sensitive Information in URL [10024] for a Vite HMR websocket handshake
      token; Information Disclosure - Suspicious Comments [10027] for Vite/React bundled
      dev-mode source comments; Storable and Cacheable Content [10049] for dev-asset
      cache headers; Modern Web Application [10109], an informational scan-tuning
      hint, not a vulnerability) are dev-server-only artifacts inherent to Vite's
      dev mode with no production relevance and no COM-MOD-013-specific content. No
      new debt registered for these.
    report_files:
    - 08-qa/security-quality/COM-MOD-013-QA-001/zap-employee-portal.html
    - 08-qa/security-quality/COM-MOD-013-QA-001/zap-employee-portal.json
quality_gates:
  backend:
    tool: Maven Enforcer, Surefire, JaCoCo, Checkstyle, PMD/CPD, SpotBugs/FindSecBugs,
      OWASP Dependency-Check, CycloneDX, duplicate-finder
    commands:
    - mvn --settings .mvn/settings.xml -Pquality -Dhop.local-db-tests=true clean verify
    - mvn --settings .mvn/settings.xml -Pquality checkstyle:check pmd:check pmd:cpd-check
      spotbugs:check duplicate-finder:check org.owasp:dependency-check-maven:check
    status: passed
    tests_run: 382
    failures: 0
    errors: 0
    skipped: 0
    line_coverage_percent: 84.25
    previous_recorded_floor_percent: 84.14
    clean_rebuild_figure_before_td_db_005_fix_percent: 82.57
    coverage_floor_met: true
    coverage_regression: false
    coverage_note: The registry's recorded 84.14% floor was not reproducible from
      a clean rebuild at the start of this item (measured 82.57%); investigating that
      gap led to finding and closing TD-DB-005. After that fix, the reproducible clean-rebuild
      figure was 84.24%. A follow-up DAST pass found and fixed one further defect
      (TD-QA-007, a MultipartException handling gap), adding one regression test and
      raising coverage to 84.25% (382 tests), above both the 80% final-closure target
      and the previously recorded 84.14% floor. Future backend iterations must not
      regress below 84.25%.
    checkstyle:
      findings_total: 73
      fail_on_violation: false
      disposition: tracked_under_existing_TD_BE_002
    pmd_and_cpd:
      pmd_findings_total: 570
      cpd_duplications: 2
      disposition: tracked_under_existing_TD_BE_002
    spotbugs_findsecbugs:
      findings_total_before_this_item: 70
      findings_total_after_this_item: 63
      high_severity_fixed: 2
      medium_severity_fixed: 5
      medium_severity_dispositioned_accepted: 3
      remaining_findings_disposition: pre_existing_repo_wide_tracked_under_TD_BE_002
    owasp_dependency_check:
      total_dependencies: 72
      vulnerable_dependencies: 0
      local_advisory_database_path: C:/Documents/Proyectos/Laboratorio/dependency-check-data
      local_advisory_database_freshness_date: '2026-07-20'
      auto_update: false
      note: Not refreshed this session, per the local-vulnerability-database policy.
    sbom:
      tool: CycloneDX Maven Plugin
      components: 110
    duplicate_finder:
      status: passed
      conflicts_found: 0
    trivy_filesystem_scan_backend:
      tool: trivy 0.72.0
      scanners:
      - vuln
      - secret
      - misconfig
      severities:
      - UNKNOWN
      - LOW
      - MEDIUM
      - HIGH
      - CRITICAL
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
  employee_portal:
    tool: TypeScript, ESLint, Vitest/V8 coverage, Vite, jscpd, Prettier, license-checker,
      npm audit
    commands:
    - npm run typecheck
    - npm run lint
    - npm run test:coverage
    - npm run build
    - npm run duplication
    - npm run format:check
    - npm run license:check
    - npm run audit:all
    status: passed
    typecheck: passed
    lint:
      errors: 0
      warnings_before_this_item: 51
      warnings_after_this_item: 50
      disposition: remaining_warnings_tracked_under_TD_FE_010_and_TD_I18N_002
    tests:
      test_files: 60
      tests_run: 187
      failures: 0
    coverage:
      line_coverage_percent: 89.75
      previous_baseline_percent: 89.74
      coverage_regression: false
    build: passed
    duplication_jscpd: passed
    format_check: passed
    license_check:
      mit: 5
      unlicensed: 1
      unlicensed_note: the employee-portal package itself (private, no third-party
        risk)
    npm_audit:
      vulnerabilities: 0
    trivy_filesystem_scan_employee_portal:
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
  repo_wide:
    trivy_filesystem_scan:
      tool: trivy 0.72.0
      scanners:
      - vuln
      - secret
      - misconfig
      severities:
      - UNKNOWN
      - LOW
      - MEDIUM
      - HIGH
      - CRITICAL
      targets_scanned:
      - backend/pom.xml
      - doctor-portal
      - employee-portal
      - patient-portal
      - public-website
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
    yaml_parse:
      files_parsed: 1263
      errors: 0
    agent_agnostic_scan:
      files_checked: all files touched by this backlog item
      real_hits: 0
frontend_coverage_preservation:
  mobile_typescript_foundation_line_coverage_percent: 99.21
  patient_portal_line_coverage_percent: 94.11
  doctor_portal_line_coverage_percent: 96.28
  public_website_typescript_web_line_coverage_percent: 98.61
  note: COM-MOD-013-QA-001 touched only backend and employee-portal; mobile/patient-portal/doctor-portal/
    public-website were not changed and no source file in those stacks was touched.
    Previously measured line coverage per stack is re-affirmed unchanged.
closure_criteria:
  all_5_capabilities_validated: true
  dast_executed_against_runnable_backend_and_employee_portal_surfaces: true
  no_vulnerabilities_of_any_severity_open_without_disposition: true
  coverage_not_regressed_and_above_previous_floor: true
  technical_debt_reduced: true
  at_least_one_debt_item_resolved_or_materially_reduced: true
  no_stale_pointers_after_sweep: true
  git_clean_after_commit: true
  agent_agnostic: true
  td_i18n_002_and_td_fe_010_status_honest: true
decision:
  backlog_item_status: closed
  ready_for_next_backlog_item: COM-MOD-013-CLOSEOUT
  next_backlog_item_name: Module closeout and registry update
  committed: true
```
