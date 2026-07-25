---
id: HOP-STACK-MARKET-VALIDATION-001
format: markdown_structured_payload
type: client-stack-market-validation
name: HOP Client Stack Market Validation
version: 1.0.0
status: validated_at_mvp_mod_002_closeout
---

# Hop Client Stack Market Validation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-STACK-MARKET-VALIDATION-001
  type: client-stack-market-validation
  name: HOP Client Stack Market Validation
  version: 1.0.0
  status: validated_at_mvp_mod_002_closeout
  created_date: 2026-07-09
  last_refreshed_date: 2026-07-09
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
purpose: Validate the requester-proposed and current HOP stack against current open
  source market practice before release-oriented module closeout.
scope:
  project: Healthcare Operations Platform
  current_module: MVP-MOD-002
  refreshed_during_backlog_item: MVP-MOD-002-CLOSEOUT
  validation_mode: official_source_market_refresh_during_module_closeout
current_project_stack:
  backend:
    runtime: Java 21
    framework: Spring Boot 3.5.14
    modularity: Spring Modulith 1.4.5
    build: Maven
    database_driver: PostgreSQL JDBC 42.7.11
    embedded_runtime: Tomcat 10.1.55
    json_baseline: Jackson BOM 2.21.4
  frontend:
    runtime: Node.js ecosystem
    framework: React 18.3.1
    language: TypeScript 5.9.3
    build: Vite 6.x
    tests: Vitest 3.x
  mobile:
    baseline: TypeScript renderer-agnostic package prepared for future React Native
      or Expo binding
  database:
    relational: PostgreSQL 16 local runtime
  deployment:
    local: Docker Compose
official_sources_checked:
- source: react.dev/versions and endoflife.date/react
  checked_on: 2026-07-09
  finding: React latest stable is 19.1.8 (June 2026); React 18.3.1 remains the last
    18.x and is the recommended pre-19 baseline.
- source: vite.dev release blog and npm registry
  checked_on: 2026-07-09
  finding: Vite latest stable is 8.1 (Rolldown bundler); Vite 7 released mid-2025.
- source: devblogs.microsoft.com/typescript and typescriptlang.org
  checked_on: 2026-07-09
  finding: TypeScript 6.0 is GA (March 2026) as the bridge to 7.0; 7.0 is in release
    candidate.
- source: postgresql.org release notes and versioning policy
  checked_on: 2026-07-09
  finding: PostgreSQL 18 is the newest major (18.4 latest); PostgreSQL 16 is supported
    until Nov 2028.
- source: jdbc.postgresql.org changelogs and postgresql.org security news
  checked_on: 2026-07-09
  finding: PostgreSQL JDBC 42.7.12 is a security release (June 2026) fixing channelBinding=require
    enforcement; 42.7.11 predates it.
- source: spring.io / infoq Spring ecosystem news and docs.spring.io
  checked_on: 2026-07-09
  finding: Spring Boot 4.0/4.1 released (June 2026); 3.5.x line continues to ~3.5.16.
    Spring Modulith 2.0.6 pairs with Boot 4.x; 1.4.x pairs with Boot 3.5.x.
- source: adoptium / oracle Java lifecycle
  checked_on: 2026-07-09
  finding: Java 21 remains an actively supported LTS; Java 25 LTS is the newer LTS
    option.
known_evidence_from_repository:
- 08-qa/security-quality/MVP-MOD-002-QA-001/security-quality-evidence.md
- 08-qa/qa/catalog-test-configuration/MVP-MOD-002-QA-001-validation.md
- 08-qa/qa/catalog-test-configuration/MVP-MOD-002-CLOSEOUT.md
current_stable_or_lts_version_decisions:
- component: Java
  current: 21 (LTS)
  decision: keep_supported_lts
  rationale: Java 21 is an active LTS compatible with Spring Boot 3.5.x and all quality
    gates.
- component: Spring Boot
  current: 3.5.14
  decision: keep_on_supported_3_5_line
  rationale: 3.5.x is still supported; a 4.x migration is a coordinated change tracked
    in TD-STACK-001.
- component: Spring Modulith
  current: 1.4.5
  decision: keep_1_4_x
  rationale: 1.4.x is the line compatible with Spring Boot 3.5.x; 2.0.x requires Boot
    4.x.
- component: Maven
  current: 3.9.11
  decision: keep
  rationale: Current supported Maven 3.9.x line.
- component: Tomcat (embedded)
  current: 10.1.55
  decision: keep_boot_managed
  rationale: Version is aligned and managed by the Spring Boot 3.5.x BOM.
- component: Jackson
  current: 2.21.4
  decision: keep_boot_managed
  rationale: Managed through the Maven BOM override; no HIGH/CRITICAL findings.
- component: PostgreSQL JDBC
  current: 42.7.11
  decision: upgrade_now
  applied_to: 42.7.12
  rationale: Same-line security release; low-risk patch applied during closeout.
- component: React
  current: 18.3.1
  decision: keep_now_debt_to_evaluate_19
  rationale: Supported baseline; React 19 evaluation tracked in TD-STACK-001.
- component: TypeScript
  current: 5.9.3
  decision: keep_now_debt_to_evaluate_6_0
  rationale: 6.0 GA is API-compatible with 5.9; migration tracked in TD-STACK-001.
- component: Vite
  current: 6.x
  decision: keep_now_debt_to_evaluate_7_8
  rationale: 6.x builds pass; major upgrade tracked in TD-STACK-001.
- component: Vitest
  current: 3.x
  decision: keep
  rationale: 3.x is current and compatible with the project test setup.
- component: PostgreSQL
  current: 16
  decision: keep_supported
  rationale: 16 is supported until Nov 2028; PostgreSQL 18 evaluation tracked in TD-STACK-001.
- component: Docker Compose
  current: local runtime profile
  decision: keep
  rationale: Portable, standards-based local runtime; no change required.
- component: Mobile baseline
  current: renderer-agnostic TypeScript foundation
  decision: keep_foundation
  rationale: Final renderer selection deferred until native UI implementation begins.
immediate_changes_applied:
- change: Upgrade PostgreSQL JDBC driver 42.7.11 to 42.7.12
  reason: Official security release addressing channelBinding=require enforcement.
  file: 07-implementation/backend/pom.xml
  validation: Backend standard suite (42/0/0, 5 skipped) and PostgreSQL-backed suite
    (42/0/0) passed; Trivy 0 HIGH/CRITICAL.
market_validation_required:
  status: completed_for_mvp_mod_002_closeout
  required_during:
  - next dependency-changing backlog item
  - release readiness
  completed_checks:
  - Validated Java, Spring Boot, Spring Modulith, Tomcat, Jackson and PostgreSQL JDBC
    against official stable or LTS sources.
  - Validated React, Vite, TypeScript, Vitest and PostgreSQL against official stable
    sources and ecosystem compatibility.
  - Confirmed mobile stack direction remains a foundation-only baseline pending renderer
    selection.
  - Reviewed quality toolchain completeness for Java/Maven and frontend TypeScript
    and routed gaps to technical debt.
  - Registered technical debt for non-blocking gaps under 08-qa/technical-debt/.
blocking_findings: []
technical_debt_items_created_or_updated:
- TD-QA-001
- TD-QA-002
- TD-BE-001
- TD-BE-002
- TD-BE-003
- TD-BE-004
- TD-STACK-001
selected_stack_baseline:
  backend:
    runtime: Java 21 (LTS)
    framework: Spring Boot 3.5.14
    modularity: Spring Modulith 1.4.5
    build: Maven 3.9.x
    database_driver: PostgreSQL JDBC 42.7.12
    embedded_runtime: Tomcat 10.1.55
    json_baseline: Jackson BOM 2.21.4
  frontend:
    framework: React 18.3.1
    language: TypeScript 5.9.3
    build: Vite 6.x
    tests: Vitest 3.x
  mobile:
    baseline: renderer-agnostic TypeScript foundation
  database:
    relational: PostgreSQL 16
  deployment:
    local: Docker Compose
decision:
  ready_for_mvp_mod_002_closeout_execution: true
  market_refresh_completed: true
  ready_for_release_without_market_refresh: false
  rationale: 'Official-source market refresh completed. The current stack is composed
    of supported, actively maintained open-source versions with no HIGH/CRITICAL findings.
    The only immediate change was the PostgreSQL JDBC security patch. Beneficial major
    upgrades are non-blocking and tracked as gradual technical debt. Release readiness
    still requires SBOM, license and DAST gates (TD-BE-004, TD-QA-001).

    '
```
