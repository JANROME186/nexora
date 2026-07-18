# MVP-MOD-007-CLOSEOUT — Security & Quality Evidence

Backlog item: `MVP-MOD-007-CLOSEOUT` — Module closeout and registry update. Status: **passed**.

## Open-source-first

This closeout's only production code change is the `TD-BE-010` fix (wiring an existing internal
module read port, `laboratoryworkflow SampleReadPort`, into `frontdeskcaredelivery`). No new
runtime dependency, proprietary platform dependency or named-agent requirement was introduced. The
employee-portal test addition (`laboratoryOperationsApi.test.ts`) uses only existing test
dependencies (vitest) already present in `package.json`.

## Checks

| Check | Result |
|---|---|
| Backend tests (211, `mvn -Pquality "-Dhop.local-db-tests=true" clean verify`) | passed |
| Frontend/mobile tests (employee 89, patient 1, doctor 1, mobile 31) | passed |
| SAST / static analysis (SpotBugs, PMD/CPD, ESLint) | passed, no new findings in touched files |
| Dependency vulnerability scan (OWASP Dependency-Check, Trivy, `npm audit` x4) | passed, 0 vulnerabilities |
| Secrets scan (Trivy, integrated) | passed, 0 secrets |
| Coverage | passed — backend **78.51%** (floor 78.42%), employee-portal **85.50%** (regression to 84.03% found and fixed), mobile **98.87%** (re-confirmed), patient-portal **41.93%** and doctor-portal **40.62%** (first measurement) |
| Message externalization / i18n review | passed — no new runtime messages |
| DAST | covered by `MVP-MOD-007-QA-001` and `HOP-QA-ALIGN-004` |
| Container/IaC scan | not applicable |

## Backend static analysis detail

`spotbugs:spotbugs`, `pmd:pmd` and `pmd:cpd` were explicitly invoked (these plugins are declared
in this project's Maven quality profile but not bound to any lifecycle phase). SpotBugs reported
15 pre-existing findings across the whole codebase (7 SECURITY-category:
`CRLF_INJECTION_LOGS`, `SERVLET_HEADER` ×3, `XSS_SERVLET`, `IMPROPER_UNICODE`); none are in
`frontdeskcaredelivery/diagnosticordermanagement` or `laboratoryworkflow/shared`, the two packages
this closeout touched (verified by targeted grep). PMD reported only pre-existing design/style
findings in `DiagnosticOrderManagementService.java` (excessive imports, coupling, duplicate
literals, cyclomatic complexity, etc.), unrelated to the `cancel()` method this closeout modified.
Both are tracked under the already-open `TD-BE-002`; nothing new was introduced.

## Dependency vulnerability scan detail

OWASP Dependency-Check (`mvn -Pquality org.owasp:dependency-check-maven:check`) scanned 55 backend
dependencies and found **0 vulnerabilities**. Two individual CVE records failed to import into the
local NVD cache during the database-update phase (a known dependency-check-maven data-quality
issue — their reference URLs exceed the H2 database's column length) but this did not prevent the
dependency-analysis phase from completing and producing a clean report. The integrated Trivy scan
(`backend/pom.xml`, and the `doctor-portal`/`employee-portal`/`patient-portal` `package-lock.json`
files) also found 0 vulnerabilities, 0 secrets and 0 misconfigurations.

## Registry gaps found and corrected during this closeout

1. **Employee-portal coverage regression**: `MVP-MOD-007-PORTAL-001` expanded
   `laboratoryOperationsApi.ts`/`laboratoryResultMapper.ts` without a dedicated unit test, dropping
   coverage from the 84.44% floor to 84.03%. Added `laboratoryOperationsApi.test.ts`, restoring it
   to **85.50%**.
2. **Patient-portal/doctor-portal coverage never measured**: measured for the first time
   (**41.93%**/**40.62%**); registered `TD-FE-008`/`TD-FE-009`.
3. **Stale mobile coverage baseline**: `technical-debt-index.yaml` still read 97.15% instead of the
   98.87% `MVP-MOD-007-APP-001` had already measured; corrected.

## Technical debt

- **Closed by this backlog item**: `TD-BE-010` (real code change — see the QA evidence for detail).
- **Registered by this backlog item**: `TD-FE-008`, `TD-FE-009`.
- **Materially reduced, unchanged this iteration**: `TD-BE-002`, `TD-BE-003`, `TD-BE-004`,
  `TD-FE-003`, `TD-APP-001`, `TD-I18N-002`, `TD-IAM-002`.
- **Open, unrelated to this module**: `TD-STACK-001`, `TD-BE-005`–`TD-BE-008`, `TD-FE-002`,
  `TD-DEF-002`, `TD-FE-005`, `TD-FE-006`, `TD-DB-002`–`TD-DB-004`, `TD-UX-001`–`TD-UX-003`,
  `TD-STACK-002`, `TD-STACK-003` (17 items project-wide).
- **Blocking**: none.

## Commercial readiness disclosure

**HOP is not commercially complete and not GA-ready.** `MVP-MOD-008` and later releases remain
planned. 19 technical-debt items remain open project-wide (final closure requires zero). Backend
(78.51%), patient-portal (41.93%) and doctor-portal (40.62%) coverage remain below the 80%
final-closure target; employee-portal (85.50%) and mobile (98.87%) already meet it but must not
regress.

## Readiness

Security/quality status: **passed**. Ready for next backlog item: **`MVP-MOD-008-DEF`** —
Integration and Migration Readiness capability package models.
