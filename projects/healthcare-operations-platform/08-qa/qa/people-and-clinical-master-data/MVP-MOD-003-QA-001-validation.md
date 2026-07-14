# MVP-MOD-003-QA-001 — People and Clinical Master Data Integrated Module Validation

Status: **passed_with_execution_limitation**
Backlog item: MVP-MOD-003-QA-001
Module: MVP-MOD-003 People and Clinical Master Data
Machine-readable evidence: `MVP-MOD-003-QA-001-validation.yaml`

## Objective

Integrally validate MVP-MOD-003 (capability package models, full traceability, OpenAPI contracts,
BE-002 custom rules, FE-001 UI, existing QA/security evidence, the local runbook, backlog
pointers) without implementing new functionality and without starting MVP-MOD-003-CLOSEOUT.

## What was validated

Capability package models for BCM-PER-001, BCM-PER-002, BCM-PER-003 and BCM-ATT-002 were re-read
and remain internally consistent. Every OpenAPI operation declared in the 4 packages'
`openapi-source.yaml` maps one-to-one to a backend controller route (PersonController,
PatientController, DoctorController, PatientRegistrationController) — 42/42 operations, no gaps
in either direction. BE-002's custom-rule implementation claims were checked against the actual
backend Java source, and FE-001's UI claims were checked against the actual employee-portal
source, rather than re-trusting the prior evidence at face value.

## Findings

Two backend rule gaps were found and are now disclosed in `MVP-MOD-003-BE-002-validation.yaml`'s
`model_gaps_identified` (previously an empty list):

- **RN-005 (Doctor credential expiration)** — no scheduler proactively transitions expired
  credentials or flags doctors for re-verification; only a reactive check exists at verification
  time. Referring-eligibility is still computed correctly in real time, so no incorrect business
  outcome results today. Tracked as `TD-BE-007`.
- **RN-008 (read-model masking)** — document/credential number masking in `PatientSnapshot`/
  `DoctorSnapshot` is a fixed algorithm, not the tenant-configurable policy the rule describes.
  Document numbers are never shown unmasked, so there is no raw data-exposure regression. Tracked
  as `TD-BE-008`.

One frontend UI-completeness gap was found, already disclosed as out-of-scope in
`MVP-MOD-003-FE-001-validation.yaml` but now formally tracked: 11 of 42 modeled operations
(patient/doctor update, patient document management, doctor specialty assignment, plus patient
representative update) have no client function and no UI. The backend already supports all of
them as generatable, contract-tested endpoints, so this is a pure UI convenience gap. Tracked as
`TD-FE-002`.

Two known, previously-disclosed items were re-confirmed still accurate and required no new
action: `TD-BE-005` (doctor activation gated by eligibility query rather than a status field) and
`TD-BE-006` (patient registration commit not wrapped in a database transaction).

## Defect found and fixed

`PROJECT_STATE.yaml` was found truncated at 261 lines — a prior session's file patch had cut off
the file mid-list, losing the rest of the MVP-MOD-002 closeout record and the trailing
`execution_flow`/`ga_gates_defined` sections. This predates the MVP-MOD-003-FE-001 commit and had
gone unnoticed since. Reconstructed from the last known-good committed version and reverified as
valid YAML (300 lines).

A second, minor stale pointer was found and fixed: `capability-package-index.yaml`'s
`active_capability_package_group` block still read `package_status: modeled`,
`backlog_item: MVP-MOD-003-DEF`, `next_backlog_item: MVP-MOD-003-BE-001` — unchanged since the
DEF backlog item closed. Corrected to reflect the current validated state and
`next_backlog_item: MVP-MOD-003-CLOSEOUT`.

## Validations executed

| Check | Result |
|---|---|
| `mvn test` (no local DB) | Not executed — no Maven in this sandbox, only Java 11 present |
| `mvn test -Dhop.local-db-tests=true` | Not executed — same reason, plus no Docker |
| `npm run typecheck` | **Passed**, 0 errors |
| `npm test` | Not executed — sandbox network allowlist blocks native rollup binary |
| `npm run test:coverage` | Not executed — same reason |
| `npm run build` | Not executed — same reason |
| `npm audit --audit-level=high` | Not executed — network allowlist blocks audit endpoint |
| Full project YAML parse | **Passed**, 481 files, 0 failures |
| Secrets scan | **Passed**, no matches |
| Agent-agnostic scan | **Passed**, no matches |
| Stale-pointer scan | **Passed after fix** (capability-package-index.yaml corrected) |
| `git diff --check` | Pre-existing repo-wide CRLF/LF noise (~1471 files), confirmed via `-w`
  diff to be unrelated to any real content change; scoped `git diff --cached --check` is clean |

### Execution limitation

This sandbox has no Maven executable and no Docker, and only OpenJDK 11 (the runbook requires
Java 21 and Maven 3.9.x), so the backend test suite could not run here. The employee-portal's
`npm test`/`test:coverage`/`build`/`audit` remain blocked by the same network allowlist
restriction documented in `MVP-MOD-003-FE-001-validation.yaml` (its own follow-up validation later
resolved this on an unrestricted machine; that passing 18-test result is accepted as current
ground truth here rather than re-executed).

**Compensating controls:** backend custom-rule and contract correctness were validated by direct
line-level review of the Java source against each capability's `business-rules.yaml` and
`openapi-source.yaml`; frontend contract and UI coverage were validated by direct comparison of
`peopleApi.ts` and the screen components against `openapi-source.yaml` and `traceability.yaml`;
the two newly added FE-001 test files were read in full and spot-checked against actual source.
`npm run typecheck`, the full YAML parse, and the secrets/agent-agnostic/stale-pointer scans all
executed successfully.

**Recommended follow-up:** re-run `mvn test` (with and without `-Dhop.local-db-tests=true`) and
`npm test`/`npm run test:coverage`/`npm run build`/`npm audit` on a developer machine or CI with
Java 21, Maven and unrestricted registry access, before or during MVP-MOD-003-CLOSEOUT.

## Out of scope (confirmed not touched)

MVP-MOD-003-CLOSEOUT was not started. No capability package was redesigned;
`BUSINESS_REQUIREMENT.md` was not modified. No new capability, UI screen, API operation or
business-rule implementation was added — every finding above was either corrected as an
evidence/registry defect or registered as non-blocking technical debt.

## Readiness

`MVP-MOD-003-QA-001` closes as **closed_with_execution_limitation**. Recommended next backlog
item: **MVP-MOD-003-CLOSEOUT** (Module closeout and registry update).
