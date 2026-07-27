---
id: HOP-HARD-DATA-001-security-quality-evidence
type: security-quality-evidence
status: validated
backlog_item: HOP-HARD-DATA-001
---

# HOP-HARD-DATA-001 Security Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-DATA-001-security-quality-evidence
  type: security-quality-evidence
  status: validated
  backlog_item: HOP-HARD-DATA-001
  module_id: HOP-FINAL-HARDENING
tools:
  maven_quality_verify:
    command: mvn --settings .mvn/settings.xml -Pquality '-Dhop.local-db-tests=true' clean verify
    status: passed
    tests_run: 569
    failures: 0
    errors: 0
    skipped: 0
    coverage_line_percent: 84.77
  trivy:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs target --skip-dirs .m2 --exit-code 0 --format table .
    working_directory: 07-implementation/backend
    status: passed
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  cyclonedx:
    status: generated
    output: 07-implementation/backend/target/platform-foundation-backend-0.1.0-SNAPSHOT-cyclonedx.json
  git_diff_check:
    status: passed
rls_functional_verification:
  intent: Native row-level security (TD-DB-004) is a genuine access-control mechanism, not just
    SQL that applied without error; verified functionally, not only by absence of errors.
  test: TenantSessionDataSourceLocalDatabaseTest
  findings:
  - A non-admin session (CurrentTenantContext bypass=false) reading organization.laboratories
    with no "where tenant_id = ?" predicate at all only sees rows belonging to its own session
    tenant.
  - An ADMIN/bypass session (bypass=true) reading the same table with the same query sees rows
    across tenants, preserving the platform's existing cross-tenant provisioning/support-assistance
    capability.
  - Manually verified at the database layer (psql) before wiring the Java side, that the hop_app
    role is rejected by RLS's WITH CHECK when inserting a row for a tenant that does not match its
    own session tenant setting (app.current_tenant_id) unless app.rls_bypass is also set true.
  - Confirmed the local docker-compose bootstrap role (hop) is a PostgreSQL superuser and always
    bypasses RLS regardless of FORCE, and that PostgreSQL refuses ALTER ROLE ... NOSUPERUSER on
    the bootstrap role; this is why a second, unprivileged hop_app role plus SET ROLE (not GUCs
    alone) was required for the policy to have any real effect on the application's own
    connections.
security_decision:
  no_new_critical_or_high_dependency_vulnerabilities_detected: true
  trivy_all_severities_clean_for_backend_source_scan: true
  new_endpoint_authorization_enforced: true
  new_endpoint_authorization_note: GET /api/organization/reference-data/{countries,locales,currencies}
    is registered in EndpointPermissionRegistry (SCREEN_TENANTS, BCM-ORG-001); an unregistered
    endpoint bypasses HopAuthorizationInterceptor entirely, so registration itself is the
    security-relevant control here, not merely documentation.
  residual_static_analysis_debt_blocks_final_project_closure: true
  residual_static_analysis_debt_blocks_this_slice_closure: false
  rationale: HOP-HARD-DATA-001 closed all four of its mapped technical-debt items (TD-DB-002,
    TD-DB-003, TD-DB-004, TD-STACK-002), adding a real database-layer defense-in-depth control
    (native RLS via a least-privilege role) without weakening or replacing existing
    application-level tenant_id filtering, and without introducing any new dependency
    vulnerability, secret or misconfiguration finding. checkstyle/pmd/spotbugs/dependency-check
    remain unresolvable offline in this sandbox (pre-existing, tracked at the project level, not
    introduced by this item).
next_backlog_item: HOP-HARD-FE-001
```
