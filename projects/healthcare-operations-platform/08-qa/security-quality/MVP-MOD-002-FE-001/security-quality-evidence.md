# MVP-MOD-002-FE-001 - Open Source Security Quality Evidence

- Artifact: HOP-SQ-MVP-MOD-002-FE-001-001
- Status: passed
- Standard: `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`
- Scope: `07-implementation/employee-portal/`

## Open-Source-First Validation

The Diagnostic Catalog employee portal uses public, self-hostable open source tooling and libraries:
React, React DOM, Vite, TypeScript, Vitest, Testing Library, jsdom and `@vitest/coverage-v8`.
No proprietary runtime dependency was introduced.

Validated package metadata shows MIT or Apache-2.0 licensing for the primary toolchain and runtime
dependencies. The coverage provider was pinned to `@vitest/coverage-v8@3.2.7` to match the installed
Vitest version and avoid forcing a major-version upgrade.

## Quality Gates

| Gate | Command | Result |
|---|---|---|
| Static analysis / SAST baseline | `npm run typecheck` | passed |
| Automated tests | `npm test` | passed: 5 files, 8 tests |
| Coverage | `npm run test:coverage` | passed |
| Dependency vulnerabilities | `npm audit --audit-level=high` | passed: 0 vulnerabilities |
| Secrets scan | `rg -n -i "(api[_-]?key\|secret\|password\|passwd\|token\|private[_-]?key\|client[_-]?secret)" src package.json vite.config.ts` | passed: no matches |
| Production build | `npm run build` | passed |
| DAST | not executed | not applicable for this backlog |
| Container/IaC scan | not executed | not applicable; no container/IaC assets changed |

Coverage baseline:

| Metric | Threshold | Measured |
|---|---:|---:|
| Lines | 65% | 68.7% |
| Statements | 65% | 68.7% |
| Branches | 80% | 85.83% |
| Functions | 35% | 35.43% |

## Notes

- DAST must run in MVP-MOD-002-QA-001 or release validation when backend and employee portal are
  started together.
- Coverage thresholds are intentionally baseline-level for the current portal. Future UI backlog
  items should raise them as focused tests are added.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-MVP-MOD-002-FE-001-001
  type: security-quality-evidence
  name: MVP-MOD-002-FE-001 Open Source Security Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-002-FE-001
  module: MVP-MOD-002 Diagnostic Catalog
  implementation_root: 07-implementation/employee-portal/
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  objective: Validate that the Diagnostic Catalog employee portal baseline follows
    open-source-first and quality gate expectations.
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_runtime_dependencies:
  - name: react
    version: 18.3.1
    license: MIT
  - name: react-dom
    version: 18.3.1
    license: MIT
  new_quality_dependency:
    name: '@vitest/coverage-v8'
    version: 3.2.7
    license: MIT
    purpose: Enable open source code coverage gate using the V8 provider aligned to
      Vitest 3.2.7.
  validated_toolchain:
  - name: Vite
    version: 6.4.3
    license: MIT
  - name: TypeScript
    version: 5.9.3
    license: Apache-2.0
  - name: Vitest
    version: 3.2.7
    license: MIT
  - name: Testing Library
    license: MIT
  - name: jsdom
    version: 25.0.1
    license: MIT
quality_gates:
- id: SQ-001
  name: Static analysis / SAST baseline
  command: npm run typecheck
  result: passed
  notes: TypeScript strict project check completed with no errors.
- id: SQ-002
  name: Automated tests
  command: npm test
  result: passed
  notes: 5 test files passed, 8 tests passed.
- id: SQ-003
  name: Coverage
  command: npm run test:coverage
  result: passed
  thresholds:
    lines: 65
    statements: 65
    branches: 80
    functions: 35
  measured:
    lines: 68.7
    statements: 68.7
    branches: 85.83
    functions: 35.43
  notes: Baseline thresholds are intentionally conservative and must increase incrementally.
- id: SQ-004
  name: Dependency vulnerability scan
  command: npm audit --audit-level=high
  result: passed
  notes: found 0 vulnerabilities.
- id: SQ-005
  name: Secrets scan
  command: rg -n -i "(api[_-]?key|secret|password|passwd|token|private[_-]?key|client[_-]?secret)"
    src package.json vite.config.ts
  result: passed
  notes: No matches found.
- id: SQ-006
  name: Production build
  command: npm run build
  result: passed
  notes: Vite production build completed successfully.
- id: SQ-007
  name: DAST
  command: not_executed
  result: not_applicable_for_backlog
  notes: 'DAST requires the backend and employee portal running together as an integrated
    surface. This backlog compiled the UI baseline and did not start the integrated
    runtime; DAST remains required for MVP-MOD-002-QA-001 or release validation.

    '
- id: SQ-008
  name: Container or IaC scan
  command: not_executed
  result: not_applicable_for_backlog
  notes: No container or IaC assets changed in MVP-MOD-002-FE-001.
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-002-QA-001
  next_required_focus:
  - Run integrated backend/frontend validation.
  - Execute DAST when the runnable web/API surface is available.
  - Raise coverage thresholds as focused tests are added.
```
