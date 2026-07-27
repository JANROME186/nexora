-- HOP-HARD-DATA-001 / TD-DB-004: native PostgreSQL row-level-security as a database-layer
-- defense-in-depth complement to the existing application-level "WHERE tenant_id = ?" pattern
-- (every JdbcXxxRepository already parameterizes tenant_id correctly; RLS exists to keep
-- protecting tenant isolation even if a future query forgets that predicate).
--
-- Runs last (schema-locations[15], after every module's own schema.sql) and walks
-- information_schema for every base table that has a tenant_id column, so it automatically
-- covers new tenant-scoped tables added by future modules without needing this file edited again.
-- Tables that only carry tenant_id transitively through a parent (link/child tables with no
-- tenant_id column of their own, e.g. catalog.price_entries) are not covered directly; they remain
-- protected only by the existing application-level joins, unchanged from before this item.
--
-- Session GUCs (app.current_tenant_id, app.rls_bypass) are set per borrowed connection by
-- TenantSessionDataSource from the authenticated request's CurrentTenantContext. The policy is
-- deliberately permissive (does not restrict rows) whenever:
--   * app.rls_bypass = 'true'  -- the ADMIN role, which is expected to act across tenants
--     (tenant provisioning, cross-tenant support assistance) -- application-level authorization
--     already decided this request may reach the endpoint; RLS is not the access-control layer.
--   * app.current_tenant_id is unset/empty -- no authenticated request is in flight (background
--     jobs, this very schema.sql bootstrap, or a repository/unit test calling JdbcTemplate
--     directly outside a request), so there is no tenant claim to restrict against.
--   * the row's own tenant_id is NULL -- a small number of tables (e.g. audit.audit_events) model
--     tenant_id as optional for system-level rows not owned by any single tenant.
-- Otherwise a row is only visible/writable when its tenant_id matches the session's tenant.
--
-- PostgreSQL superusers always bypass RLS, FORCE included, with no documented override, and the
-- local docker-compose Postgres image's bootstrap POSTGRES_USER (hop) both is a superuser and
-- owns every table below -- so without a second step, every policy created below would be
-- silently inert for the app's own connections. Postgres also refuses to ever strip SUPERUSER
-- from the bootstrap role ("the bootstrap user must have the SUPERUSER attribute"), so hop cannot
-- be fixed directly. Instead, create a plain, unprivileged, non-owner role (hop_app, NOLOGIN --
-- it is only ever reached via SET ROLE, never a direct connection) and have
-- TenantSessionDataSource SET ROLE hop_app for the duration of any connection borrowed inside an
-- authenticated request. A superuser may SET ROLE to any role without an explicit GRANT (superusers
-- are implicitly members of every role), and once switched, permission/RLS checks use hop_app's
-- (non-super, non-owner) privileges for that session -- exactly the "impersonate a restricted
-- role from an admin connection" pattern Postgres documents SET ROLE for. Schema bootstrapping
-- (this very script) and any connection outside a request run as hop unchanged, since
-- TenantSessionDataSource only switches role when CurrentTenantContext is populated.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'hop_app') THEN
        CREATE ROLE hop_app NOLOGIN;
    END IF;
END $$;

DO $$
DECLARE
    schema_rec RECORD;
BEGIN
    FOR schema_rec IN
        SELECT schema_name FROM information_schema.schemata
        WHERE schema_name NOT IN ('pg_catalog', 'information_schema', 'pg_toast')
    LOOP
        EXECUTE format('GRANT USAGE ON SCHEMA %I TO hop_app', schema_rec.schema_name);
        EXECUTE format(
            'GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA %I TO hop_app',
            schema_rec.schema_name);
    END LOOP;
END $$;

DO $$
DECLARE
    rec RECORD;
    policy_predicate CONSTANT text :=
        'current_setting(''app.rls_bypass'', true) = ''true'''
        || ' OR current_setting(''app.current_tenant_id'', true) IS NULL'
        || ' OR current_setting(''app.current_tenant_id'', true) = '''''
        || ' OR tenant_id IS NULL'
        || ' OR tenant_id = current_setting(''app.current_tenant_id'', true)';
BEGIN
    FOR rec IN
        SELECT DISTINCT c.table_schema, c.table_name
        FROM information_schema.columns c
        JOIN information_schema.tables t
            ON t.table_schema = c.table_schema
            AND t.table_name = c.table_name
            AND t.table_type = 'BASE TABLE'
        WHERE c.column_name = 'tenant_id'
            AND c.table_schema NOT IN ('pg_catalog', 'information_schema')
        ORDER BY c.table_schema, c.table_name
    LOOP
        EXECUTE format('ALTER TABLE %I.%I ENABLE ROW LEVEL SECURITY', rec.table_schema, rec.table_name);
        EXECUTE format('ALTER TABLE %I.%I FORCE ROW LEVEL SECURITY', rec.table_schema, rec.table_name);
        EXECUTE format('DROP POLICY IF EXISTS hop_tenant_isolation ON %I.%I', rec.table_schema, rec.table_name);
        EXECUTE format(
            'CREATE POLICY hop_tenant_isolation ON %I.%I USING (%s) WITH CHECK (%s)',
            rec.table_schema, rec.table_name, policy_predicate, policy_predicate);
    END LOOP;
END $$;
