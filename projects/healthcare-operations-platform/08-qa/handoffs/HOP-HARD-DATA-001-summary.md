---
id: HOP-HARD-DATA-001-summary
type: backlog-handoff
status: closed
backlog_item: HOP-HARD-DATA-001
---

# HOP-HARD-DATA-001 Summary

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-DATA-001-summary
  type: backlog-handoff
  status: closed
  backlog_item: HOP-HARD-DATA-001
  module_id: HOP-FINAL-HARDENING
summary:
  closed_scope:
  - Diagnostic catalog bilingual-name audit closing TD-DB-002 with evidence (no code change
    required; already end-to-end since MVP-MOD-002-BE-001).
  - New country/locale/currency reference-data read API (ReferenceDataController, JDBC and
    in-memory repositories) closing TD-DB-003.
  - Native PostgreSQL row-level security across every tenant-scoped table via a new
    TenantSessionDataSource and least-privilege hop_app role, closing TD-DB-004 as
    defense-in-depth alongside unchanged application-level tenant_id filtering.
  - Dedicated architecture decision record for the JPA/Hibernate deferral, closing TD-STACK-002.
validation:
  qa_evidence: 08-qa/qa/final-hardening/HOP-HARD-DATA-001-validation.md
  security_quality_evidence: 08-qa/security-quality/HOP-HARD-DATA-001/security-quality-evidence.md
  backend_gate:
    command: mvn --settings .mvn/settings.xml -Pquality '-Dhop.local-db-tests=true' clean verify
    status: passed
    tests_run: 569
    line_coverage_percent: 84.77
  trivy_backend_filesystem:
    status: passed_zero_vulnerabilities
technical_debt:
  closed:
  - TD-DB-002
  - TD-DB-003
  - TD-DB-004
  - TD-STACK-002
  materially_reduced: []
notable_findings_for_next_owner:
- 'Native RLS is real, not cosmetic: proven functionally by TenantSessionDataSourceLocalDatabaseTest
    (a non-admin session sees only its own tenant''s rows even with no WHERE-clause predicate at
    all; an ADMIN/bypass session still sees across tenants). The local docker-compose Postgres
    bootstrap role (hop) is a superuser that can never have SUPERUSER revoked (Postgres itself
    refuses that ALTER ROLE), so the real per-request enforcement role is a second, unprivileged
    hop_app role reached via SET ROLE, not hop itself -- a manual `docker exec ... psql -U hop`
    session bypasses RLS entirely unless you `SET ROLE hop_app;` first.'
- db/final-hardening/schema.sql is intentionally NOT registered in
  application-local.properties's spring.sql.init.schema-locations, because Spring's script
  splitter cannot parse PostgreSQL's $$ dollar-quoted DO blocks (it mis-splits on semicolons
  inside them). It is applied instead by FinalHardeningSchemaInitializer, an ApplicationRunner
  that sends the whole file as one JDBC statement after schema init completes. If a future
  backlog item needs to add more SQL to this file, keep that pattern -- do not move it back into
  schema-locations.
- TD-DB-002's original description ("single name column, no locale variant") was stale by the
  time this item picked it up; the catalog tables have carried name_en/name_es plus a shared
  LocalizedText domain value object since before this hardening module even started. Worth
  keeping in mind for any other technical-debt entry that looks similarly dated -- verify against
  the current codebase before assuming the description is still accurate.
next:
  backlog_item: HOP-HARD-FE-001
  focus: Employee portal quality, i18n, UX and missing workflow surfaces.
```
