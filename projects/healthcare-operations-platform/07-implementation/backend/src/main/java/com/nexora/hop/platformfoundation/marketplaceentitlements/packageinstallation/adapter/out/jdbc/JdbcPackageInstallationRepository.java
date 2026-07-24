package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallation;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallationRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcPackageInstallationRepository implements PackageInstallationRepository {

    private static final String SELECT_SQL = """
            select installation_id, tenant_id, package_id, entitlement_id, version, lifecycle_status,
                   rollback_checkpoint_version, created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.package_installations
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcPackageInstallationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PackageInstallation save(PackageInstallation installation) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.package_installations
                    (installation_id, tenant_id, package_id, entitlement_id, version, lifecycle_status,
                     rollback_checkpoint_version, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (installation_id) do update set
                    version = excluded.version, lifecycle_status = excluded.lifecycle_status,
                    rollback_checkpoint_version = excluded.rollback_checkpoint_version,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                installation.installationId(), installation.tenantId(), installation.packageId(),
                installation.entitlementId(), installation.version(), installation.lifecycleStatus(),
                installation.rollbackCheckpointVersion(), installation.audit().createdBy(),
                Timestamp.valueOf(installation.audit().createdAt()), installation.audit().updatedBy(),
                Timestamp.valueOf(installation.audit().updatedAt()));
        return installation;
    }

    @Override
    public Optional<PackageInstallation> findById(String installationId) {
        return jdbcTemplate.query(SELECT_SQL + " where installation_id = ?",
                JdbcPackageInstallationRepository::map, installationId).stream().findFirst();
    }

    @Override
    public List<PackageInstallation> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?",
                JdbcPackageInstallationRepository::map, tenantId);
    }

    @Override
    public List<PackageInstallation> findByTenantIdAndPackageId(String tenantId, String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and package_id = ?",
                JdbcPackageInstallationRepository::map, tenantId, packageId);
    }

    private static PackageInstallation map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PackageInstallation(
                resultSet.getString("installation_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("package_id"),
                resultSet.getString("entitlement_id"),
                resultSet.getString("version"),
                resultSet.getString("lifecycle_status"),
                resultSet.getString("rollback_checkpoint_version"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
