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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-002-FE-001-001
  type: qa-validation-evidence
  name: MVP-MOD-002-FE-001 Diagnostic Catalog Employee Portal Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-002-FE-001-validation.md
  machine_readable: MVP-MOD-002-FE-001-validation.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-002-FE-001
  module: MVP-MOD-002 Diagnostic Catalog
  release: REL-001
  execution_flow_stage: compile_ui
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  implementation_root: 07-implementation/employee-portal/
  objective: 'Compile the employee portal outputs for the Diagnostic Catalog baseline
    so operators can access catalog administration screens for services, tests, panels,
    analytes, patient preparations, reference ranges, samples and price lists from
    the existing HOP employee portal shell.

    '
change_detection:
  last_analyzed_business_requirement_version: v0.68.0
  current_business_requirement_version: v0.68.0
  impact_pending: false
implemented_outputs:
- id: FE-001
  path: 07-implementation/employee-portal/src/api/catalogApi.ts
  description: Diagnostic Catalog API client bound to /api/catalog endpoints.
- id: FE-002
  path: 07-implementation/employee-portal/src/api/types.ts
  description: Diagnostic Catalog TypeScript DTOs for the eight MVP-MOD-002 capability
    packages.
- id: FE-003
  path: 07-implementation/employee-portal/src/components/screens/DiagnosticCatalogScreen.tsx
  description: Employee portal screen for list, create and publish actions across
    catalog areas.
- id: FE-004
  path: 07-implementation/employee-portal/src/components/layout/AppShell.tsx
  description: Diagnostic Catalog navigation tab added to the employee administration
    shell.
- id: FE-005
  path: 07-implementation/employee-portal/src/test/DiagnosticCatalogScreen.test.tsx
  description: UI test for scoped diagnostic service creation.
- id: FE-006
  path: 07-implementation/employee-portal/vite.config.ts
  description: Coverage quality gate configured with the open source Vitest V8 provider.
capability_coverage:
- capability: BCM-SVC-001
  name: Diagnostic Service Catalog
  ui_support: create, list, publish
- capability: BCM-SVC-002
  name: Test Catalog
  ui_support: create, list, publish
- capability: BCM-SVC-003
  name: Panel Catalog
  ui_support: create, list, publish
- capability: BCM-SVC-004
  name: Analyte Catalog
  ui_support: create, list, publish
- capability: BCM-SVC-005
  name: Patient Preparation Management
  ui_support: create, list, publish
- capability: BCM-SVC-006
  name: Reference Range Management
  ui_support: create, list, publish
- capability: BCM-SVC-007
  name: Sample Catalog
  ui_support: create sample type, list sample types and requirements, publish explicit
    sample row target
- capability: BCM-SVC-009
  name: Price List Management
  ui_support: create, list, publish
validations:
- id: VAL-001
  name: TypeScript static analysis
  method: npm run typecheck
  working_directory: 07-implementation/employee-portal
  result: passed
- id: VAL-002
  name: Employee portal unit and smoke tests
  method: npm test
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: 5 test files passed, 8 tests passed.
- id: VAL-003
  name: Coverage gate
  method: npm run test:coverage
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: 'V8 coverage provider enabled. Current baseline: 68.7% statements/lines,
    85.83% branches, 35.43% functions. Active thresholds: 65% statements/lines, 80%
    branches, 35% functions.

    '
- id: VAL-004
  name: Production build
  method: npm run build
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: Vite production bundle generated successfully.
- id: VAL-005
  name: Dependency vulnerability audit
  method: npm audit --audit-level=high
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: found 0 vulnerabilities.
- id: VAL-006
  name: Secrets scan
  method: rg -n -i "(api[_-]?key|secret|password|passwd|token|private[_-]?key|client[_-]?secret)"
    src package.json vite.config.ts
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: No matches found.
- id: VAL-007
  name: Open-source-first dependency check
  method: npm ls --all --json --depth=0 plus package license review from node_modules
    package metadata
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: React, React DOM, Vite, TypeScript, Vitest, V8 coverage provider, jsdom
    and Testing Library dependencies are public npm packages under MIT or Apache-2.0
    licenses.
documented_boundaries:
- The Diagnostic Catalog screen is a baseline operator administration surface. Advanced
  workflows such as guided preparation assignment, price entry maintenance and effective-context
  lookup will be refined in later capability-specific UX backlog items.
- DAST was not executed for this backlog because the backend and web app were not
  started together as a full runnable environment during this UI compilation task.
  The security-quality evidence records this as not applicable for MVP-MOD-002-FE-001
  and required for integrated release gates.
- Coverage thresholds are established at the current employee portal baseline and
  must increase incrementally as additional screens receive focused tests.
blocking_gaps: []
readiness:
  mvp_mod_002_fe_001_status: closed
  ready_for_next_backlog_item: MVP-MOD-002-QA-001
  next_backlog_item_name: Validate MVP-MOD-002 generated outputs, contracts, rules,
    UI and quality evidence
  rationale: 'The employee portal now exposes Diagnostic Catalog administration navigation
    and baseline list, create and publish actions for all MVP-MOD-002 catalog areas.
    Static analysis, tests, coverage, build, dependency audit, secrets scan and open-source-first
    dependency checks passed.

    '
```
