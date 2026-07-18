# MVP-MOD-008-BE-001 Security & Quality Evidence

Backend compilation of BCM-PLT-004 Integration Management, BCM-PLT-005 API Management and
BCM-PLT-010 Open Data Ingestion and Migration passed all quality gates.

## Passed

- `mvn -Pquality "-Dhop.local-db-tests=true" clean verify`
- Tests: 239 run, 0 failures, 0 errors, 0 skipped (includes Spring Modulith module-boundary
  verification and new local-database tests against real Postgres).
- JaCoCo line coverage: **80.08%**, reaching the 80% final-closure target (up from the 78.51%
  floor).
- `mvn -Pquality org.owasp:dependency-check-maven:check` — **0 vulnerabilities**, including the one
  new dependency this backlog item adds (`org.apache.commons:commons-csv:1.14.1`).
- `trivy fs --scanners vuln,secret,misconfig .` (repository root) — **0 vulnerabilities, 0 secrets,
  0 misconfigurations**.
- Maven Enforcer, CycloneDX SBOM generation and Duplicate Finder all passed through the quality
  lifecycle.

## Registered Debt

An explicit repo-wide static-analysis run found:

- PMD: 344 findings (repo-wide; up from 263 at MVP-MOD-007-BE-001 given ~90 new files).
- CPD: 1 duplicated-code finding (unchanged from the pre-existing baseline).
- SpotBugs: 21 findings (repo-wide); a small number of style-category findings appear in the two
  new parser classes (`ManifestParser`, `ImportFileParser`) — none is a new CORRECTNESS or
  high-priority SECURITY finding.
- Spotless: still fails against 566+ pre-existing files repo-wide (unchanged; this tooling has
  never been applied historically and is not bound to the `mvn verify` lifecycle).

All of the above are non-blocking (`quality.failOnViolation=false`) and registered under the
existing **`TD-BE-002`** gradual static-analysis remediation debt item — not new debt.

One new debt item was registered: **`TD-BE-013`** (XLSX row-level parsing not implemented for
migration ingestion — Apache POI deliberately deferred given its dependency footprint and no
immediate provider demand; xlsx packages are still accepted and archived, only row counting is
skipped).

Two debt items were reduced with real implementation (not modeling-only, unlike MVP-MOD-008-DEF):

- **`TD-STACK-003`** — compensating control extended (checked 1:1 controller-to-contract mapping
  for the 3 new modules); the OpenAPI-Generator introduction itself was deliberately deferred to
  avoid fragmenting the codebase across only 3 of 18 modules.
- **`TD-I18N-002`** — the first-class `code` error field modeled by MVP-MOD-008-DEF is now real,
  working code for the first time in HOP's backend.

Security/quality status: **passed**. Ready for next backlog item: **`MVP-MOD-008-BE-002`**.
