package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackageRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcMarketplacePackageRepository implements MarketplacePackageRepository {

    private static final String SELECT_SQL = """
            select package_id, code, name, category, capability_mappings_text, status,
                   created_by, created_at, updated_by, updated_at
            from marketplace_entitlements.marketplace_packages
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcMarketplacePackageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MarketplacePackage save(MarketplacePackage marketplacePackage) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.marketplace_packages
                    (package_id, code, name, category, capability_mappings_text, status,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (package_id) do update set
                    name = excluded.name, category = excluded.category,
                    capability_mappings_text = excluded.capability_mappings_text, status = excluded.status,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                marketplacePackage.packageId(), marketplacePackage.code(), marketplacePackage.name(),
                marketplacePackage.category(), String.join(",", marketplacePackage.capabilityMappings()),
                marketplacePackage.status(), marketplacePackage.audit().createdBy(),
                Timestamp.valueOf(marketplacePackage.audit().createdAt()), marketplacePackage.audit().updatedBy(),
                Timestamp.valueOf(marketplacePackage.audit().updatedAt()));
        return marketplacePackage;
    }

    @Override
    public Optional<MarketplacePackage> findById(String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where package_id = ?",
                JdbcMarketplacePackageRepository::map, packageId).stream().findFirst();
    }

    @Override
    public Optional<MarketplacePackage> findByCode(String code) {
        return jdbcTemplate.query(SELECT_SQL + " where code = ?",
                JdbcMarketplacePackageRepository::map, code).stream().findFirst();
    }

    @Override
    public List<MarketplacePackage> findByStatus(String status) {
        return jdbcTemplate.query(SELECT_SQL + " where status = ?", JdbcMarketplacePackageRepository::map, status);
    }

    @Override
    public List<MarketplacePackage> findAll() {
        return jdbcTemplate.query(SELECT_SQL, JdbcMarketplacePackageRepository::map);
    }

    private static MarketplacePackage map(ResultSet resultSet, int rowNumber) throws SQLException {
        String mappingsText = resultSet.getString("capability_mappings_text");
        List<String> mappings = mappingsText == null || mappingsText.isBlank()
                ? List.of()
                : Arrays.asList(mappingsText.split(","));
        return new MarketplacePackage(
                resultSet.getString("package_id"),
                resultSet.getString("code"),
                resultSet.getString("name"),
                resultSet.getString("category"),
                mappings,
                resultSet.getString("status"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
