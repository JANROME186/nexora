package com.nexora.hop.platformfoundation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;

/**
 * TD-DB-004: proves the native row-level-security policy created by {@code
 * db/final-hardening/schema.sql} actually restricts rows per session tenant through {@link
 * TenantSessionDataSource}, not just that the SQL applied without error.
 */
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class TenantSessionDataSourceLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clearTenantContext() {
        CurrentTenantContext.clear();
    }

    @Test
    void nonAdminSessionOnlySeesItsOwnTenantsLaboratoriesEvenWithoutAWhereClause() {
        String tenantA = createTenant();
        String tenantB = createTenant();
        String labA = createLaboratory(tenantA);
        String labB = createLaboratory(tenantB);

        CurrentTenantContext.set(tenantA, false);
        assertThat(labIdsVisibleWithNoTenantPredicate()).contains(labA).doesNotContain(labB);

        CurrentTenantContext.set(tenantB, false);
        assertThat(labIdsVisibleWithNoTenantPredicate()).contains(labB).doesNotContain(labA);
    }

    @Test
    void adminBypassSessionSeesLaboratoriesAcrossTenants() {
        String tenantA = createTenant();
        String tenantB = createTenant();
        String labA = createLaboratory(tenantA);
        String labB = createLaboratory(tenantB);

        CurrentTenantContext.set(tenantA, true);
        assertThat(labIdsVisibleWithNoTenantPredicate()).contains(labA, labB);
    }

    private java.util.List<String> labIdsVisibleWithNoTenantPredicate() {
        // Deliberately has no "where tenant_id = ?" predicate: visibility must come from RLS alone.
        return jdbcTemplate.queryForList("select laboratory_id from organization.laboratories", String.class);
    }

    private String createTenant() {
        CurrentTenantContext.set("bootstrap", true);
        String tenantId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        jdbcTemplate.update("""
                insert into organization.tenants
                    (tenant_id, code, legal_name, trade_name, tax_id, status, tier, isolation_strategy, created_at, updated_at)
                values (?, ?, ?, '', '', 'active', 'STARTER', 'DISCRIMINATOR_WITH_RLS', ?, ?)
                """,
                tenantId, tenantId, "RLS Test Tenant", java.sql.Timestamp.from(now), java.sql.Timestamp.from(now));
        return tenantId;
    }

    private String createLaboratory(String tenantId) {
        CurrentTenantContext.set(tenantId, true);
        String laboratoryId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        jdbcTemplate.update("""
                insert into organization.laboratories (laboratory_id, tenant_id, name, status, created_at, updated_at)
                values (?, ?, 'RLS Test Lab', 'active', ?, ?)
                """,
                laboratoryId, tenantId, java.sql.Timestamp.from(now), java.sql.Timestamp.from(now));
        return laboratoryId;
    }
}
