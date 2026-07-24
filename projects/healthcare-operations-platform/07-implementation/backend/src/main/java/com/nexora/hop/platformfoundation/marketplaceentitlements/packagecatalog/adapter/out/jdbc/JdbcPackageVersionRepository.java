package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcPackageVersionRepository implements PackageVersionRepository {

    private static final String SELECT_SQL = """
            select version_id, package_id, version, lifecycle_status, compatibility_approved,
                   security_review_approved, support_model_approved, telemetry_model_approved,
                   created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.package_versions
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcPackageVersionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PackageVersion save(PackageVersion packageVersion) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.package_versions
                    (version_id, package_id, version, lifecycle_status, compatibility_approved,
                     security_review_approved, support_model_approved, telemetry_model_approved,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (version_id) do update set
                    lifecycle_status = excluded.lifecycle_status,
                    compatibility_approved = excluded.compatibility_approved,
                    security_review_approved = excluded.security_review_approved,
                    support_model_approved = excluded.support_model_approved,
                    telemetry_model_approved = excluded.telemetry_model_approved,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                packageVersion.versionId(), packageVersion.packageId(), packageVersion.version(),
                packageVersion.lifecycleStatus(), packageVersion.compatibilityApproved(),
                packageVersion.securityReviewApproved(), packageVersion.supportModelApproved(),
                packageVersion.telemetryModelApproved(), packageVersion.audit().createdBy(),
                Timestamp.valueOf(packageVersion.audit().createdAt()), packageVersion.audit().updatedBy(),
                Timestamp.valueOf(packageVersion.audit().updatedAt()));
        return packageVersion;
    }

    @Override
    public Optional<PackageVersion> findByPackageIdAndVersion(String packageId, String version) {
        return jdbcTemplate.query(SELECT_SQL + " where package_id = ? and version = ?",
                JdbcPackageVersionRepository::map, packageId, version).stream().findFirst();
    }

    @Override
    public List<PackageVersion> findByPackageId(String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where package_id = ?", JdbcPackageVersionRepository::map, packageId);
    }

    private static PackageVersion map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PackageVersion(
                resultSet.getString("version_id"),
                resultSet.getString("package_id"),
                resultSet.getString("version"),
                resultSet.getString("lifecycle_status"),
                resultSet.getBoolean("compatibility_approved"),
                resultSet.getBoolean("security_review_approved"),
                resultSet.getBoolean("support_model_approved"),
                resultSet.getBoolean("telemetry_model_approved"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
