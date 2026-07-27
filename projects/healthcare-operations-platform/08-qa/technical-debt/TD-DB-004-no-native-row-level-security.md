---
id: TD-DB-004
format: markdown_structured_payload
type: technical-debt-item
name: Tenant scoping is enforced by application-level WHERE clauses, not PostgreSQL
  native row-level security policies
version: 2.0.0
status: closed
---

# Tenant Scoping Is Enforced By Application Level Where Clauses, Not Postgresql Native Row Level Security Policies

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-DB-004
  type: technical-debt-item
  name: Tenant scoping is enforced by application-level WHERE clauses, not PostgreSQL
    native row-level security policies
  version: 2.0.0
  status: closed
  created_date: 2026-07-17
  updated_date: 2026-07-26
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/data-architecture/database-architecture.md
classification:
  category: database_defense_in_depth_gap
  affected_area: tenant_isolation
  affected_components:
  - 07-implementation/backend/src/main/resources/db/**
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: 'Every existing JdbcXxxRepository consistently parameterizes
    tenant_id in its queries (spot-checked across multiple modules); native RLS would
    be defense-in-depth, not a fix for a known correctness bug.

    '
current_state:
  issue: No PostgreSQL ROW LEVEL SECURITY policies exist; tenant isolation relies
    entirely on every repository query correctly including a tenant_id predicate.
  compensating_control:
  - Consistent, code-reviewed WHERE tenant_id = ? pattern across all existing repositories.
  - 'COM-MOD-012-OPS-002 added an executable, mandatory operational compensating control:
    09-operations/runbooks/tenant-impact-triage-runbook.md requires a cross-tenant
    leakage check (grouped tenant_id row counts against a known-good baseline) during
    every incident, restore and higher-risk deployment, escalating any anomaly to
    P1 regardless of the originally reported severity. This is defense-in-depth at
    the operational layer, not a database-layer fix; TD-DB-004''s acceptance criteria
    (native RLS) remain open.'
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - ALTER TABLE ... ENABLE ROW LEVEL SECURITY plus CREATE POLICY statements per tenant-scoped
    table, set via a session variable per connection.
remediation:
  strategy: release_readiness_hardening_backlog_item
  owner: backend_platform_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: release_readiness_hardening_backlog_item
  dependencies_or_prerequisites:
  - TD-IAM-001 (a real authenticated principal is needed to set the RLS session tenant
    variable safely) -- closed by HOP-ENT-FOUND-001, prerequisite satisfied.
  incremental_remediation_triggers:
  - Approaching release/GA readiness gates (GA-003 security and compliance gate).
  acceptance_criteria:
  - RLS policies enforce tenant isolation at the database layer as defense-in-depth
    alongside existing application-level filtering.
  owner_or_responsible_role: backend_platform_team
disposition_history:
- backlog_item: COM-MOD-012-OPS-002
  date: 2026-07-22
  disposition: materially_reduced
  reason: Added the tenant-impact-triage-runbook.md operational compensating control
    described in current_state.compensating_control. Native RLS (this item's acceptance_criteria)
    remains open and is unchanged in scope; TD-IAM-001's dependency is now satisfied,
    so RLS is no longer blocked and can be scheduled as its own backend code-changing
    backlog item.
- backlog_item: HOP-HARD-DATA-001
  date: 2026-07-26
  disposition: closed
  reason: 'Implemented native PostgreSQL RLS as this item''s own acceptance_criteria specifies:
    a new db/final-hardening/schema.sql walks information_schema for every base table with a
    tenant_id column (68 tables at time of writing, across every module, discovered
    dynamically so future tables are covered automatically) and applies ENABLE/FORCE ROW LEVEL
    SECURITY plus a tenant_id-matching policy. A new TenantSessionDataSource
    (application root package) stamps every borrowed connection with the authenticated
    request''s tenant id (from CurrentTenantContext, already used elsewhere for TD-IAM-004) as
    Postgres session GUCs. One real-world obstacle required a design decision beyond the
    original target_state: the local docker-compose Postgres bootstrap user (hop) is a
    superuser that owns every table, and superusers unconditionally bypass RLS with no override
    -- Postgres also refuses to ever strip SUPERUSER from the bootstrap role. The fix is a
    second, unprivileged, non-owner, NOLOGIN role (hop_app) that TenantSessionDataSource
    switches the connection to via SET ROLE for the duration of any authenticated request (a
    superuser may SET ROLE to any role without an explicit grant); connections outside a
    request (schema bootstrapping, background jobs, repository tests using JdbcTemplate
    directly) are left as hop unchanged. The policy also carries an explicit bypass GUC
    (app.rls_bypass) set true for the ADMIN role specifically, because the platform''s ADMIN
    persona legitimately acts across tenant boundaries (tenant provisioning, cross-tenant
    support assistance) -- confirmed by tracing OrganizationManagementLocalDatabaseTest and
    CatalogTestConfigurationLocalDatabaseTest, which both provision a tenant and then manage
    resources under that tenant while authenticated as the fixture ADMIN, whose own session
    tenant is a different value ("tenant-local"); a strict same-tenant-only policy would have
    broken that legitimate cross-tenant flow (and, by the identical pattern, essentially every
    *LocalDatabaseTest suite in the codebase). Proven functionally (not just "applied without
    SQL error") by TenantSessionDataSourceLocalDatabaseTest: a non-admin session only sees its
    own tenant''s organization.laboratories rows even with a query that carries no
    "where tenant_id = ?" predicate at all, while an ADMIN/bypass session still sees rows
    across tenants. This remains explicit defense-in-depth, per this item''s own acceptance
    criteria wording ("alongside existing application-level filtering"), not a replacement for
    it: application-level tenant_id predicates are unchanged and still the primary correctness
    mechanism; RLS now additionally protects against a repository query that forgets its own
    tenant_id predicate. Full mvn -Pquality clean verify with -Dhop.local-db-tests=true (real
    Postgres) passed with no regressions across every existing *LocalDatabaseTest suite,
    confirming the ADMIN-bypass design does not break any already-established cross-tenant
    admin flow.'
```
