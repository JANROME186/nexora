package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcTenantEntitlementRepository implements TenantEntitlementRepository {

    private static final String SELECT_SQL = """
            select entitlement_id, tenant_id, package_id, offer_id, status, granted_at, expires_at,
                   revoked_reason, created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.tenant_entitlements
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcTenantEntitlementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TenantEntitlement save(TenantEntitlement entitlement) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.tenant_entitlements
                    (entitlement_id, tenant_id, package_id, offer_id, status, granted_at, expires_at,
                     revoked_reason, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (entitlement_id) do update set
                    status = excluded.status, expires_at = excluded.expires_at,
                    revoked_reason = excluded.revoked_reason,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                entitlement.entitlementId(), entitlement.tenantId(), entitlement.packageId(), entitlement.offerId(),
                entitlement.status(), Timestamp.valueOf(entitlement.grantedAt()),
                entitlement.expiresAt() == null ? null : Timestamp.valueOf(entitlement.expiresAt()),
                entitlement.revokedReason(), entitlement.audit().createdBy(),
                Timestamp.valueOf(entitlement.audit().createdAt()), entitlement.audit().updatedBy(),
                Timestamp.valueOf(entitlement.audit().updatedAt()));
        return entitlement;
    }

    @Override
    public Optional<TenantEntitlement> findById(String entitlementId) {
        return jdbcTemplate.query(SELECT_SQL + " where entitlement_id = ?",
                JdbcTenantEntitlementRepository::map, entitlementId).stream().findFirst();
    }

    @Override
    public List<TenantEntitlement> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?", JdbcTenantEntitlementRepository::map, tenantId);
    }

    @Override
    public List<TenantEntitlement> findByTenantIdAndPackageId(String tenantId, String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and package_id = ?",
                JdbcTenantEntitlementRepository::map, tenantId, packageId);
    }

    private static TenantEntitlement map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new TenantEntitlement(
                resultSet.getString("entitlement_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("package_id"),
                resultSet.getString("offer_id"),
                resultSet.getString("status"),
                localDateTime(resultSet, "granted_at"),
                resultSet.getTimestamp("expires_at") == null ? null : localDateTime(resultSet, "expires_at"),
                resultSet.getString("revoked_reason"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
