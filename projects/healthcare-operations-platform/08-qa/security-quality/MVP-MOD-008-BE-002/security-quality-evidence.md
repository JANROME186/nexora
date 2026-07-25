# MVP-MOD-008-BE-002 Security & Quality Evidence

Status: **passed** · Date: 2026-07-19 · Machine-readable source: [security-quality-evidence.md](security-quality-evidence.md)

## Summary

All open-source, dependency, secret, static-analysis and coverage gates passed for the integration
retry/dead-letter, API deprecation/rate-limit and migration checkpoint custom rules. Backend line
coverage improved from the 80.08% floor to **80.49%**. TD-BE-013 (XLSX row-level parsing) was closed
as the required debt-first action before feature work began.

## New dependency: Apache POI

`org.apache.poi:poi-ooxml:5.4.1` was added, explicitly named by both
`bcm-plt-010-open-data-ingestion-and-migration/generation-plan.md` and TD-BE-013's own
`target_state.preferred_open_source_tooling`. 0 vulnerabilities (OWASP Dependency-Check). Adding it
introduced a `commons-io` version conflict across three transitive paths (commons-csv, poi, poi's own
commons-compress); resolved by pinning `commons-io:2.20.0` in `dependencyManagement`, the same
technique this `pom.xml` already uses for its `jackson-databind` CVE pin.

## Quality gate results

| Gate | Result |
|---|---|
| Maven Enforcer | passed |
| Surefire (unit + Modulith + local-DB) | passed — 265 tests, 0 failures/errors/skipped |
| JaCoCo | 80.49% line coverage (floor was 80.08%) |
| CycloneDX | SBOM generated |
| OWASP Dependency-Check | 0 vulnerabilities |
| Trivy (`vuln,secret,misconfig`) | 0 findings (repo root, `.m2` cache excluded) |
| PMD | 354 findings — repo-wide, non-blocking, TD-BE-002 |
| CPD | 1 — unchanged from baseline |
| SpotBugs / Find Security Bugs | 23 findings — repo-wide, non-blocking, TD-BE-002; no new CORRECTNESS/SECURITY finding in new code |
| Duplicate Finder | passed |
| Spotless | pre-existing repo-wide debt, unchanged (not this backlog item's regression) |

## Secure-code review highlights

- **Bounded retry / dead-letter**: a hard 5-attempt ceiling with exponential backoff prevents retry
  storms against `IntegrationAdapterPort`; exhausted messages are deterministically dead-lettered.
- **Rate-limit enforcement**: unknown/revoked partner keys are rejected before further processing;
  an in-memory fixed-window counter throttles per-key traffic with bounded memory (one counter per
  active key, replaced each window).
- **Domain-command boundary preserved**: `MigrationDomainCommandPort` remains the only interaction
  point for import execution; `INV-MIG-003` (never write directly to a business aggregate) holds by
  construction, re-verified by `PlatformFoundationModulithTest`.
- **Structured errors**: every new error code is a named constant matching each capability's
  `openapi-source.md` error model, now carrying a `messageKey` alongside `code`.

## Debt decision

- **Closed**: TD-BE-013 (XLSX row-level parsing).
- **Opened**: TD-BE-014 (migration domain-command port has no real cross-module wiring yet),
  TD-BE-015 (rate-limit enforcement scoped to partner-API-key-bearing requests only).
- **Reduced**: TD-I18N-002 (added `messageKey` alongside `code` with full es-MX/en-US catalog
  coverage for BCM-PLT-004/005/010 error codes).

Ready for next backlog item: **MVP-MOD-008-FE-001**.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-008-BE-002-SECURITY-QUALITY
type: security-quality-evidence
backlog_item: MVP-MOD-008-BE-002
module: MVP-MOD-008
status: passed
date: 2026-07-19
executor: agent
summary: Backend build, tests, coverage, dependency-vulnerability and secret/misconfiguration
  scans all passed cleanly for the integration retry/dead-letter, API deprecation/rate-limit
  and migration checkpoint custom rules. Backend coverage improved from the 80.08%
  floor to 80.49%. Repo-wide static-analysis findings (PMD, SpotBugs) remain registered
  under the pre-existing TD-BE-002 gradual-remediation debt item, non-blocking. TD-BE-013
  was closed as the debt-first action.
open_source_first_check:
  new_dependency_added: org.apache.poi:poi-ooxml:5.4.1
  justification: Explicitly named by bcm-plt-010-open-data-ingestion-and-migration/generation-plan.md
    (CUS-MIG-010-01) and by TD-BE-013's target_state as the preferred open-source
    tooling for XLSX row-level parsing. Apache-2.0 licensed, actively maintained.
    poi-ooxml alone (without poi-ooxml-full/poi-scratchpad) is sufficient for reading
    worksheet rows, keeping the transitive footprint to poi, xmlbeans, commons-compress
    and curvesapi.
  vulnerabilities_found: 0
  license_check: passed (Apache-2.0, compatible)
  dependency_convergence_note: poi-ooxml's transitive commons-io (2.18.0, plus 2.16.1
    via its own commons-compress dependency) conflicted with commons-csv's transitive
    commons-io (2.20.0), failing maven-enforcer's dependencyConvergence rule. Resolved
    by pinning commons-io 2.20.0 (the newest of the three) in dependencyManagement,
    mirroring the existing jackson-databind CVE-pin pattern already used in this pom.xml.
    No functional behavior depends on the pinned version beyond normal I/O utility
    classes.
  alternative_libraries_considered: None newly evaluated this iteration; Apache POI
    was already the tool TD-BE-013 named as the preferred remediation, carried over
    from MVP-MOD-008-BE-001's own evaluation.
checks:
- tool: Maven Enforcer
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: Java 21, Maven >=3.9, dependency convergence rules passed after the commons-io
    pin above.
- tool: Surefire (unit + Modulith boundary + local-database)
  status: passed
  tests_run: 265
  failures: 0
  errors: 0
  skipped: 0
- tool: JaCoCo
  status: passed
  line_coverage_percent: 80.49
  previous_baseline_percent: 80.08
  final_closure_target_percent: 80
  notes: Backend coverage improved, remaining above the stack's 80% final-closure
    target.
- tool: CycloneDX
  status: passed
  evidence_command: mvn -Pquality org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
  notes: SBOM generated under target outputs (not committed, per repository convention
    of not versioning target/).
- tool: OWASP Dependency-Check
  status: passed
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  vulnerabilities_found: 0
  cvss_threshold: 0
  notes: Explicitly verified 0 vulnerabilities for the new org.apache.poi:poi-ooxml:5.4.1
    dependency tree and the whole project.
- tool: Trivy (fs, vuln+secret+misconfig)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig --skip-dirs projects/healthcare-operations-platform/07-implementation/backend/.m2
    .
  scope: repository_root
  findings: 0
  notes: 0 vulnerabilities, 0 secrets, 0 misconfigurations across backend pom.xml
    and all frontend package-lock.json files. The gitignored local Maven repository
    cache (07-implementation/backend/.m2/) was excluded via --skip-dirs after an initial
    run timed out walking its large dependency-jar tree; this cache is never committed
    and carries no source content to scan.
- tool: PMD
  status: findings_registered
  findings: 354
  debt: TD-BE-002
  notes: Repo-wide, non-blocking (quality.failOnViolation=false); grown from 344 at
    MVP-MOD-008-BE-001 with the new adapter/interceptor/test files; gradual remediation
    per TD-BE-002.
- tool: CPD
  status: passed
  findings: 1
  notes: Unchanged from pre-existing baseline; not attributable to the new code.
- tool: SpotBugs and Find Security Bugs
  status: findings_registered
  findings: 23
  debt: TD-BE-002
  notes: Repo-wide; a small number of style-category findings appear in the new classes,
    consistent with the pattern already accepted at MVP-MOD-008-BE-001. No new CORRECTNESS
    or high-priority SECURITY finding in the new code. Pre-existing SECURITY-category
    findings are in other, unrelated modules, unchanged by this backlog item.
- tool: Duplicate Finder
  status: passed
  evidence_command: mvn -Pquality org.basepom.maven:duplicate-finder-maven-plugin:check
- tool: Spotless (Google Java Format)
  status: repo_wide_debt_confirmed_unchanged
  evidence_command: mvn -Pquality com.diffplug.spotless:spotless-maven-plugin:check
  notes: Fails against 577+ pre-existing files repo-wide; not bound to the mvn verify
    lifecycle in pom.xml (no <executions> block), consistent with the other static-analysis
    tools' non-blocking convention. Not a regression introduced by this backlog item.
secure_code_review:
  bounded_retry_and_dead_letter: IntegrationManagementService.retryMessage enforces
    a hard MAX_RETRY_COUNT ceiling (5) with exponential backoff between attempts,
    preventing unbounded retry storms against the IntegrationAdapterPort; an exhausted
    message is deterministically dead-lettered rather than silently retried forever.
  rate_limit_enforcement: PartnerApiKeyRateLimitInterceptor rejects requests from
    an unknown or revoked partner key before any further processing (401), and throttles
    requests exceeding their configured requests-per-minute policy (429) via an in-memory
    fixed-window counter with no unbounded growth (one counter per active partner
    key id, replaced each new window).
  domain_command_boundary_preserved: MigrationDomainCommandPort is the only interaction
    point commitImport/retryImportExecution use to advance an import; its local deterministic
    implementation never touches any other module's repository, storage, or aggregate,
    preserving INV-MIG-003 by construction, verified by PlatformFoundationModulithTest's
    continued pass.
  structured_error_codes: Every new error code (INTEGRATION_MESSAGE_DEAD_LETTERED,
    INTEGRATION_RETRY_NOT_YET_DUE, API_DEPRECATION_WINDOW_NOT_ELAPSED, MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED,
    MIGRATION_DOMAIN_COMMAND_FAILED) is a named constant matching each capability's
    openapi-source.md error_model, with a first-class `messageKey` added alongside
    `code` on every response.
  input_validation: jakarta.validation and server-side enum/state-machine checks unchanged
    and extended consistently for every new command.
decision:
  security_quality_status: passed
  closed_debt:
  - TD-BE-013
  created_debt:
  - TD-BE-014
  - TD-BE-015
  reduced_debt:
  - TD-I18N-002
  ready_for_next_backlog_item: MVP-MOD-008-FE-001
  next_iteration_requirement: MVP-MOD-008-FE-001 should continue debt-first practice;
    TD-BE-002 (repo-wide static-analysis burn-down) remains the most broadly relevant
    candidate if the employee-portal iteration also touches backend code, otherwise
    a frontend-scoped debt item should be prioritized instead.
```
