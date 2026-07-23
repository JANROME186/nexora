package com.nexora.hop.platformfoundation.platformconfiguration.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.platformconfiguration.domain.ConfigParameter;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.FeatureFlag;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.PlatformConfigurationRepository;

@Repository
@Profile("local")
class JdbcPlatformConfigurationRepository implements PlatformConfigurationRepository {

    private static final String TARGET_TENANTS_SEPARATOR = ",";

    private final JdbcTemplate jdbcTemplate;

    JdbcPlatformConfigurationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ConfigParameter> findAllConfigParameters() {
        return jdbcTemplate.query("""
                select config_key, value_type, raw_value, tenant_override_allowed, is_encrypted
                from platform_configuration.config_parameters
                order by config_key
                """, JdbcPlatformConfigurationRepository::mapConfigParameter);
    }

    @Override
    public List<FeatureFlag> findAllFeatureFlags() {
        return jdbcTemplate.query("""
                select flag_key, enabled_by_default, target_tenants, rollout_percentage, updated_at, updated_by
                from platform_configuration.feature_flags
                order by flag_key
                """, JdbcPlatformConfigurationRepository::mapFeatureFlag);
    }

    @Override
    public Optional<FeatureFlag> findFeatureFlagByKey(String flagKey) {
        return jdbcTemplate.query("""
                select flag_key, enabled_by_default, target_tenants, rollout_percentage, updated_at, updated_by
                from platform_configuration.feature_flags
                where flag_key = ?
                """, JdbcPlatformConfigurationRepository::mapFeatureFlag, flagKey).stream().findFirst();
    }

    @Override
    public FeatureFlag saveFeatureFlag(FeatureFlag flag) {
        jdbcTemplate.update("""
                insert into platform_configuration.feature_flags
                    (flag_key, enabled_by_default, target_tenants, rollout_percentage, updated_at, updated_by)
                values (?, ?, ?, ?, ?, ?)
                on conflict (flag_key) do update set
                    enabled_by_default = excluded.enabled_by_default,
                    target_tenants = excluded.target_tenants,
                    rollout_percentage = excluded.rollout_percentage,
                    updated_at = excluded.updated_at,
                    updated_by = excluded.updated_by
                """,
                flag.flagKey(),
                flag.enabledByDefault(),
                String.join(TARGET_TENANTS_SEPARATOR, flag.targetTenants()),
                flag.rolloutPercentage(),
                Timestamp.from(flag.updatedAt()),
                flag.updatedBy());
        return flag;
    }

    private static ConfigParameter mapConfigParameter(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ConfigParameter(
                resultSet.getString("config_key"),
                resultSet.getString("value_type"),
                resultSet.getString("raw_value"),
                resultSet.getBoolean("tenant_override_allowed"),
                resultSet.getBoolean("is_encrypted"));
    }

    private static FeatureFlag mapFeatureFlag(ResultSet resultSet, int rowNumber) throws SQLException {
        String targetTenants = resultSet.getString("target_tenants");
        List<String> parsed = targetTenants == null || targetTenants.isBlank()
                ? List.of()
                : Arrays.stream(targetTenants.split(TARGET_TENANTS_SEPARATOR)).filter(value -> !value.isBlank()).toList();
        return new FeatureFlag(
                resultSet.getString("flag_key"),
                resultSet.getBoolean("enabled_by_default"),
                parsed,
                resultSet.getInt("rollout_percentage"),
                resultSet.getTimestamp("updated_at").toInstant(),
                resultSet.getString("updated_by"));
    }
}
