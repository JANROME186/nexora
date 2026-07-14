# MVP-MOD-003-QA-001 — Security & Quality Evidence

Status: **passed_with_execution_limitation**
Machine-readable evidence: `security-quality-evidence.yaml`

## Summary

Fifteen quality gates were assessed for the integrated MVP-MOD-003 validation. Eight passed
directly in this sandbox (TypeScript static analysis, full YAML parse, secrets scan,
agent-agnostic scan, stale-pointer scan, the OpenAPI-to-controller contract cross-check, the
BE-002 custom-rule source review, and the FE-001 API/UI coverage source review — the latter two
with disclosed non-blocking findings). Two backend Maven gates could not run because this sandbox
has no Maven and only Java 11 (exception **EX-001**). Four frontend gates (test, coverage, build,
audit) could not run because the sandbox's network allowlist blocks the native rollup binary and
the audit endpoint (exception **EX-002**), the same constraint already documented and resolved on
an unrestricted machine for FE-001.

## Gate results

| Gate | Result |
|---|---|
| QG-001 Backend Maven tests (no DB) | Not executed — EX-001 |
| QG-002 Backend Maven tests (Postgres) | Not executed — EX-001 |
| QG-003 Employee portal typecheck | Passed |
| QG-004 Employee portal test suite | Not executed — EX-002 |
| QG-005 Employee portal coverage | Not executed — EX-002 |
| QG-006 Employee portal build | Not executed — EX-002 |
| QG-007 Employee portal dependency audit | Not executed — EX-002 |
| QG-008 Full project YAML parse | Passed (481 files) |
| QG-009 Secrets scan | Passed |
| QG-010 Agent-agnostic scan | Passed |
| QG-011 Stale-pointer scan | Passed after fix |
| QG-012 OpenAPI-to-controller contract check | Passed (42/42) |
| QG-013 BE-002 custom-rule source review | Passed with findings (TD-BE-007, TD-BE-008) |
| QG-014 FE-001 API/UI coverage source review | Passed with findings (TD-FE-002) |
| QG-015 git diff --check | Passed, pre-existing repo-wide noise unrelated to this item |

## Exceptions

**EX-001** (QG-001, QG-002) — no Maven executable and only Java 11 available in this sandbox,
against the runbook's Java 21/Maven 3.9.x/Docker prerequisites. Compensating control: backend
correctness validated by direct source-level review against the modeled contracts and rules
(QG-012, QG-013) rather than re-running the already-passing BE-001/BE-002 Maven evidence (58
tests, 0 failures, with and without local Postgres).

**EX-002** (QG-004–QG-007) — network allowlist blocks the Linux-native `@rollup/rollup-linux-x64-gnu`
binary and the npm audit endpoint. Compensating control: `npm run typecheck` passed; FE-001's own
already-passing 18-test/coverage/build/audit results (from that backlog item's own follow-up
validation on an unrestricted machine) are accepted as current ground truth; the two newly added
FE-001 test files were read in full and spot-checked against actual source.

## Technical debt registered

- **TD-BE-007** — no proactive credential-expiration scheduler; only a reactive check exists.
  Medium severity, non-blocking (referring-eligibility is still computed correctly in real time).
- **TD-BE-008** — read-model document/credential masking is fixed, not tenant-configurable.
  Medium severity, non-blocking (no raw data-exposure regression).
- **TD-FE-002** — patient/doctor update, patient documents and doctor specialty assignment UI not
  yet built. Low severity, non-blocking (backend already supports all of it; already disclosed as
  out-of-scope in FE-001's own evidence).

## Recommendation

Proceed to **MVP-MOD-003-CLOSEOUT**. Re-run the backend Maven suite and the employee-portal
`npm test`/`coverage`/`build`/`audit` gates in an unrestricted environment as an early step of
that backlog item, and update QG-001/QG-002/QG-004–QG-007 and EX-001/EX-002 here once that run
completes. None of the three newly registered technical debt items block closeout.
