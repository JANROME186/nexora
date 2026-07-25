package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfiguration;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfigurationRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class JdbcDicomAdapterConfigurationRepository implements DicomAdapterConfigurationRepository {

    private static final String SELECT_SQL = """
            select configuration_id, tenant_id, ae_title, host, port, modality_type,
                   connection_status, created_by, created_at, updated_by, updated_at
            from imaging_operations.dicom_adapter_configurations
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcDicomAdapterConfigurationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DicomAdapterConfiguration save(DicomAdapterConfiguration config) {
        jdbcTemplate.update("""
                insert into imaging_operations.dicom_adapter_configurations
                    (configuration_id, tenant_id, ae_title, host, port, modality_type,
                     connection_status, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (configuration_id) do update set
                    host = excluded.host, port = excluded.port,
                    modality_type = excluded.modality_type,
                    connection_status = excluded.connection_status,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                config.configurationId(), config.tenantId(), config.aeTitle(), config.host(),
                config.port(), config.modalityType(), config.connectionStatus(), config.createdBy(),
                Timestamp.from(config.createdAt()), config.updatedBy(), Timestamp.from(config.updatedAt()));
        return config;
    }

    @Override
    public Optional<DicomAdapterConfiguration> findById(String tenantId, String configurationId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and configuration_id = ?",
                JdbcDicomAdapterConfigurationRepository::map, tenantId, configurationId).stream().findFirst();
    }

    @Override
    public Optional<DicomAdapterConfiguration> findByAeTitle(String tenantId, String aeTitle) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and lower(ae_title) = lower(?)",
                JdbcDicomAdapterConfigurationRepository::map, tenantId, aeTitle).stream().findFirst();
    }

    @Override
    public List<DicomAdapterConfiguration> findAllByTenant(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?",
                JdbcDicomAdapterConfigurationRepository::map, tenantId);
    }

    private static DicomAdapterConfiguration map(ResultSet rs, int rowNum) throws SQLException {
        return new DicomAdapterConfiguration(
                rs.getString("configuration_id"),
                rs.getString("tenant_id"),
                rs.getString("ae_title"),
                rs.getString("host"),
                rs.getInt("port"),
                rs.getString("modality_type"),
                rs.getString("connection_status"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
