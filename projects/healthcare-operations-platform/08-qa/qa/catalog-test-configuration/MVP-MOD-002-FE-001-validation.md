# MVP-MOD-002-FE-001 - Diagnostic Catalog Employee Portal Validation

- Artifact: HOP-QA-MVP-MOD-002-FE-001-001
- Status: passed
- Backlog item: MVP-MOD-002-FE-001
- Module: MVP-MOD-002 Diagnostic Catalog
- Business requirement version: v0.68.0
- Implementation root: `07-implementation/employee-portal/`

## Objective

Compile the employee portal outputs for the Diagnostic Catalog baseline so operators can manage
catalog areas for services, tests, panels, analytes, patient preparations, reference ranges, samples
and price lists from the existing HOP employee portal shell.

## Implemented Outputs

| Output | Path | Description |
|---|---|---|
| API client | `07-implementation/employee-portal/src/api/catalogApi.ts` | Catalog API bindings for `/api/catalog`. |
| DTOs | `07-implementation/employee-portal/src/api/types.ts` | TypeScript types for the eight MVP-MOD-002 capability packages. |
| UI screen | `07-implementation/employee-portal/src/components/screens/DiagnosticCatalogScreen.tsx` | Baseline list/create/publish screen by catalog area. |
| Navigation | `07-implementation/employee-portal/src/components/layout/AppShell.tsx` | Diagnostic Catalog tab added to the employee portal shell. |
| Tests | `07-implementation/employee-portal/src/test/DiagnosticCatalogScreen.test.tsx` | Scoped diagnostic service creation test. |
| Coverage gate | `07-implementation/employee-portal/vite.config.ts` | Vitest V8 coverage thresholds enabled. |

## Capability Coverage

The screen provides baseline create/list/publish support for BCM-SVC-001, BCM-SVC-002, BCM-SVC-003,
BCM-SVC-004, BCM-SVC-005, BCM-SVC-006, BCM-SVC-007 and BCM-SVC-009. Sample publishing uses an
explicit row target (`sample-type` or `sample-requirement`) instead of relying on id prefixes.

## Validation Results

| Gate | Command | Result |
|---|---|---|
| Static analysis | `npm run typecheck` | passed |
| Unit/smoke tests | `npm test` | passed: 5 files, 8 tests |
| Coverage | `npm run test:coverage` | passed: 68.7% lines/statements, 85.83% branches, 35.43% functions |
| Build | `npm run build` | passed |
| Vulnerability audit | `npm audit --audit-level=high` | passed: 0 vulnerabilities |
| Secrets scan | `rg -n -i "(api[_-]?key\|secret\|password\|passwd\|token\|private[_-]?key\|client[_-]?secret)" src package.json vite.config.ts` | passed: no matches |
| Open-source-first check | `npm ls --all --json --depth=0` plus package metadata license review | passed |

## Boundaries

- This is a baseline operator administration surface. Guided preparation assignment, price entry
  maintenance and effective-context lookup remain later UX refinements.
- DAST was not executed in this backlog because the backend and web app were not started as a full
  integrated runtime for this UI compilation task. It remains required for integrated release gates.
- Coverage thresholds are baseline gates and should increase as more screens receive focused tests.

## Readiness

MVP-MOD-002-FE-001 is closed. The next backlog item is **MVP-MOD-002-QA-001**, validating the full
Diagnostic Catalog package: generated outputs, contracts, rules, UI and quality evidence.
