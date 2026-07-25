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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-008-BE-001-SECURITY-QUALITY
type: security-quality-evidence
backlog_item: MVP-MOD-008-BE-001
module: MVP-MOD-008
status: passed
date: 2026-07-18
executor: agent
summary: Backend build, tests, coverage, dependency-vulnerability and secret/misconfiguration
  scans all passed cleanly for the new BCM-PLT-004/BCM-PLT-005/BCM-PLT-010 backend
  compilation. Backend coverage reached the 80% final-closure target. Repo-wide static-analysis
  findings (PMD, SpotBugs) are registered under the pre-existing TD-BE-002 gradual-remediation
  debt item, non-blocking.
open_source_first_check:
  new_dependency_added: org.apache.commons:commons-csv:1.14.1
  justification: Explicitly named by bcm-plt-010-open-data-ingestion-and-migration/generation-plan.md
    (CUS-MIG-010-01) as the designated open-source CSV parser. Apache-2.0 licensed,
    actively maintained, minimal transitive footprint (no additional runtime dependencies
    pulled in).
  vulnerabilities_found: 0
  license_check: passed (Apache-2.0, compatible)
  alternative_libraries_considered: Apache POI (for xlsx) was evaluated and deliberately
    deferred (TD-BE-013) given its much larger transitive dependency footprint relative
    to the near-term provider-format demand. SnakeYAML (manifest.yaml parsing) and
    Jackson (json/ndjson parsing) reuse existing transitive dependencies; no new dependency
    was added for either.
checks:
- tool: Maven Enforcer
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: Java 21, Maven >=3.9, dependency convergence rules passed.
- tool: Surefire (unit + Modulith boundary + local-database)
  status: passed
  tests_run: 239
  failures: 0
  errors: 0
  skipped: 0
- tool: JaCoCo
  status: passed
  line_coverage_percent: 80.08
  previous_baseline_percent: 78.51
  final_closure_target_percent: 80
  notes: Backend reached the 80% final-closure target for this stack.
- tool: CycloneDX
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: SBOM generated under target outputs (not committed, per repository convention
    of not versioning target/).
- tool: OWASP Dependency-Check
  status: passed
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  vulnerabilities_found: 0
  cvss_threshold: 0
  notes: Explicitly verified 0 vulnerabilities for the new commons-csv 1.14.1 dependency
    and the whole project.
- tool: Trivy (fs, vuln+secret+misconfig)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig .
  scope: repository_root
  findings: 0
  notes: 0 vulnerabilities, 0 secrets, 0 misconfigurations repository-wide.
- tool: PMD
  status: findings_registered
  findings: 344
  debt: TD-BE-002
  notes: Repo-wide, non-blocking (quality.failOnViolation=false); gradual remediation
    per TD-BE-002.
- tool: CPD
  status: passed
  findings: 1
  notes: Unchanged from pre-existing baseline; not attributable to the new modules.
- tool: SpotBugs and Find Security Bugs
  status: findings_registered
  findings: 21
  debt: TD-BE-002
  notes: Repo-wide; a small number of style-category findings appear in the two new
    parser classes (ManifestParser, ImportFileParser). No new CORRECTNESS or high-priority
    SECURITY finding in the new modules. Pre-existing SECURITY-category findings (CRLF_INJECTION_LOGS,
    IMPROPER_UNICODE, UNSAFE_HASH_EQUALS, SERVLET_HEADER, XSS_SERVLET) are in other,
    unrelated modules, unchanged by this backlog item.
- tool: Duplicate Finder
  status: passed
  evidence_command: mvn -Pquality org.basepom.maven:duplicate-finder-maven-plugin:check
- tool: Spotless (Google Java Format)
  status: repo_wide_debt_confirmed_unchanged
  evidence_command: mvn -Pquality com.diffplug.spotless:spotless-maven-plugin:check
  notes: Fails against 566+ pre-existing files repo-wide; not bound to the mvn verify
    lifecycle in pom.xml (no <executions> block), consistent with the other static-analysis
    tools' non-blocking convention. Not a regression introduced by this backlog item.
secure_code_review:
  server_side_authorization: All 3 new base paths registered in EndpointPermissionRegistry
    against 3 new PermissionCode values, verified against the real risk that unmapped
    paths bypass enforcement entirely.
  tenant_isolation: Explicit tenantId parameter threading (controller -> service ->
    repository), matching the codebase's existing convention; no new gap introduced
    relative to the pre-existing pattern.
  input_validation: jakarta.validation on every request DTO; server-side enum/range
    checks in every service method.
  file_upload_safety: Import package checksum-verified against manifest before parsing;
    archived via the existing LocalFilesystemDocumentAdapter's path-traversal-safe
    storage-key resolution (no new upload/ storage code path introduced).
  structured_error_codes: First-class `code` field on every new error response, matching
    each capability's openapi-source.md error_model.
decision:
  security_quality_status: passed
  created_debt:
  - TD-BE-013
  reduced_debt:
  - TD-STACK-003
  - TD-I18N-002
  ready_for_next_backlog_item: MVP-MOD-008-BE-002
  next_iteration_requirement: MVP-MOD-008-BE-002 must address at least one relevant
    open technical debt item before feature implementation; TD-BE-013 (xlsx parsing)
    is a candidate if migration parsing is touched, or continued TD-BE-002 static-analysis
    burn-down if integration/api-management/migration code is touched again.
```
