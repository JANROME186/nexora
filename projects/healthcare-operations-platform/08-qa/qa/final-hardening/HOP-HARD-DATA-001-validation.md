---
id: HOP-HARD-DATA-001-validation
type: qa-validation-evidence
status: validated
backlog_item: HOP-HARD-DATA-001
---

# HOP-HARD-DATA-001 Validation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-DATA-001-validation
  type: qa-validation-evidence
  status: validated
  backlog_item: HOP-HARD-DATA-001
  module_id: HOP-FINAL-HARDENING
summary:
  decision: validated_all_mapped_items_closed
  implemented:
  - 'TD-DB-002: audited catalog.diagnostic_services/test_definitions/panel_definitions/analyte_definitions
    and confirmed bilingual name_en/name_es columns plus a shared LocalizedText(en, es) domain
    value object already exist end-to-end (schema, commands, services, controllers) since the
    original MVP-MOD-002-BE-001 compile; the original debt description was stale. Closed with
    evidence, no code change required.'
  - 'TD-DB-003: added domain.ReferenceDataRepository (Country/LocaleOption/CurrencyOption records),
    JdbcReferenceDataRepository (adapter/out/jdbc, @Profile("local")), InMemoryReferenceDataRepository
    (@Profile("!local")), ReferenceDataService and a new ReferenceDataController exposing
    GET /api/organization/reference-data/countries, /locales and /currencies, registered in
    EndpointPermissionRegistry (SCREEN_TENANTS, BCM-ORG-001).'
  - 'TD-DB-004: added native PostgreSQL row-level security. db/final-hardening/schema.sql walks
    information_schema for every base table with a tenant_id column (68 tables at time of writing,
    discovered dynamically) and applies ENABLE/FORCE ROW LEVEL SECURITY plus a tenant-matching
    policy; a new TenantSessionDataSource stamps every connection borrowed inside an authenticated
    request with the session tenant id and switches the connection to a new, unprivileged,
    non-owner hop_app role via SET ROLE (required because the local Postgres bootstrap superuser
    hop can never have SUPERUSER revoked, and superusers always bypass RLS); an explicit
    app.rls_bypass GUC preserves the ADMIN role''s legitimate cross-tenant operations
    (tenant provisioning, support assistance).'
  - 'TD-STACK-002: produced 03-architecture/technology-architecture/ADR-0001-jpa-hibernate-persistence-deferral.md,
    a dedicated architecture decision record formalizing the prior JPA/Hibernate evaluation and
    deferral, satisfying the acceptance criterion that an ADR exist ahead of any future migration
    attempt.'
technical_debt_result:
  closed:
  - TD-DB-002
  - TD-DB-003
  - TD-DB-004
  - TD-STACK-002
  materially_reduced: []
tests:
  backend_quality_verify:
    command: mvn --settings .mvn/settings.xml -Pquality '-Dhop.local-db-tests=true' clean verify
    status: passed
    tests_run: 569
    failures: 0
    errors: 0
    skipped: 0
    line_coverage_percent: 84.77
    previous_backend_floor_percent: 84.69
    coverage_result: improved_above_floor_and_above_final_target
  added_or_extended_tests:
  - ReferenceDataApiTest
  - ReferenceDataLocalDatabaseTest
  - TenantSessionDataSourceLocalDatabaseTest
  - EndpointPermissionRegistryTest (new reference-data case)
  regression_check:
  - Ran the full mvn test suite (569 tests) with -Dhop.local-db-tests=true before and after
    wiring TenantSessionDataSource/native RLS; zero regressions across every existing
    *LocalDatabaseTest suite (OrganizationManagementLocalDatabaseTest, CatalogTestConfigurationLocalDatabaseTest
    and 18 others), confirming the ADMIN-bypass RLS design does not break any pre-existing
    cross-tenant admin flow (e.g. provisioning a tenant then managing its resources while
    authenticated as the fixture ADMIN whose own session tenant differs).
quality_gates:
  maven_enforcer: passed
  surefire: passed
  jacoco: passed
  cyclonedx_sbom: passed
  trivy_backend_filesystem: passed_zero_vulnerabilities
  git_diff_check: passed
  note: 'checkstyle/pmd/spotbugs/dependency-check are configured in the -Pquality profile but
    are not resolvable in this offline sandbox (pre-existing environment limitation, not
    introduced by this item; see 09-operations/runbooks/local-solution-runbook.md known_limitations).
    mvn test/JaCoCo, Trivy filesystem scan and CycloneDX SBOM generation remain the authoritative
    backend gates in this environment, consistent with every prior HOP-FINAL-HARDENING closure.'
residual_debt:
  note: No residual debt remains under this item's own mapped_items; all four (TD-DB-002,
    TD-DB-003, TD-DB-004, TD-STACK-002) are closed. TD-DB-004's own closure explicitly documents
    that RLS is additive defense-in-depth alongside unchanged application-level tenant_id
    filtering, per its acceptance criteria wording, not a claim that application-level
    filtering is no longer needed.
  tracked_under: []
next_backlog_item: HOP-HARD-FE-001
```
