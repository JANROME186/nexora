# MVP-MOD-002-FE-001 - Open Source Security Quality Evidence

- Artifact: HOP-SQ-MVP-MOD-002-FE-001-001
- Status: passed
- Standard: `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
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
