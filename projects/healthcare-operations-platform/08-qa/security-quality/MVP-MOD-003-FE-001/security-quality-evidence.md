# MVP-MOD-003-FE-001 — Security & Quality Evidence

Status: **passed_with_execution_limitation**
Machine-readable evidence: `security-quality-evidence.yaml`

## Summary

Eleven quality gates were assessed for the employee-portal UI compiled for MVP-MOD-003-FE-001.
Seven passed directly in this sandbox (TypeScript static analysis, secrets scan, agent-agnostic
scan, open-source-first dependency review, input-validation/injection review, authorization/scope
review, YAML parse). Four gates (test suite, coverage, build, dependency audit) could not execute
because this sandbox's network allowlist blocks the Linux-native esbuild/rollup binaries and the
npm audit endpoint — recorded as exception **EX-001**, with compensating controls applied
(passing `tsc --noEmit`, manual test-to-component and API-to-backend-contract cross-verification).

## Gate results

| Gate | Result |
|---|---|
| QG-001 TypeScript static analysis | Passed |
| QG-002 Unit/UI test suite | Not executed — EX-001 |
| QG-003 Coverage threshold | Not executed — EX-001 |
| QG-004 Production build | Not executed — EX-001 |
| QG-005 Dependency vulnerability audit | Not executed — EX-001 |
| QG-006 Secrets scan | Passed |
| QG-007 Agent-agnostic scan | Passed |
| QG-008 Open-source-first dependency review | Passed |
| QG-009 Input validation / injection surface review | Passed |
| QG-010 Authorization/scope review | Passed |
| QG-011 YAML parse of touched files | Passed |

## Exception EX-001

**Gates affected:** QG-002, QG-003, QG-004, QG-005

**Reason:** the sandbox network allowlist returns `403 Forbidden` / "Connection blocked by network
allowlist" when Vite/Vitest try to download the Linux-native `esbuild`/`@rollup/rollup-linux-x64-gnu`
binaries, and when `npm audit` calls the registry's audit endpoint. The committed `node_modules`
only has Windows-native binaries. This is an environment limitation of this sandbox, not a defect
in the delivered UI code.

**Compensating controls:**

1. `npm run typecheck` passed with 0 errors — validates types, JSX correctness, hook usage and
   every DTO field reference across all new/changed files.
2. Every new test's element queries were manually cross-checked against the literal text rendered
   by the corresponding component (label `htmlFor` targets, button/dialog accessible names,
   status-banner copy).
3. Every API client call's URL, method and payload shape was manually cross-checked against the
   real backend controller/record definitions read from source.
4. Secrets scan, agent-agnostic scan, dependency-change review and YAML parse all executed
   successfully in this sandbox and passed.

**Expiry:** on first execution of `npm test` / `npm run build` / `npm audit` in an unrestricted
environment (developer machine or CI), during or before MVP-MOD-003-QA-001.

## Technical debt registered

- **TD-FE-001** — the employee-portal toolchain cannot execute inside network-restricted sandboxes
  lacking a Linux-native esbuild/rollup binary or unrestricted registry access. Severity: low
  (does not affect production behavior; developer machines and standard CI runners are
  unaffected).

## Recommendation

Proceed to **MVP-MOD-003-QA-001**. Re-run `npm test`, `npm run test:coverage`, `npm run build` and
`npm audit --audit-level=high` in an unrestricted environment as an early step of that backlog
item, and update QG-002..QG-005 and EX-001 here once that run completes.
