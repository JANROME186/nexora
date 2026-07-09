package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticServiceRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.ServiceComponentLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

@Repository
@Profile("local")
class JdbcDiagnosticServiceRepository implements DiagnosticServiceRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcDiagnosticServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DiagnosticService save(DiagnosticService service) {
        jdbcTemplate.update("""
                insert into catalog.diagnostic_services
                    (service_id, tenant_id, laboratory_id, code, name_en, name_es, category_id, service_type,
                     status, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (service_id) do update set
                    code = excluded.code,
                    name_en = excluded.name_en,
                    name_es = excluded.name_es,
                    category_id = excluded.category_id,
                    service_type = excluded.service_type,
                    status = excluded.status,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                service.serviceId(), service.tenantId(), service.laboratoryId(), service.code(),
                service.name().en(), service.name().es(), service.categoryId(), service.serviceType(),
                service.status(), service.version(),
                Timestamp.from(service.createdAt()), Timestamp.from(service.updatedAt()));
        return service;
    }

    @Override
    public Optional<DiagnosticService> findById(String serviceId) {
        return jdbcTemplate.query("""
                select service_id, tenant_id, laboratory_id, code, name_en, name_es, category_id, service_type,
                       status, version, created_at, updated_at
                from catalog.diagnostic_services
                where service_id = ?
                """, JdbcDiagnosticServiceRepository::mapService, serviceId).stream().findFirst();
    }

    @Override
    public List<DiagnosticService> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("""
                select service_id, tenant_id, laboratory_id, code, name_en, name_es, category_id, service_type,
                       status, version, created_at, updated_at
                from catalog.diagnostic_services
                where laboratory_id = ?
                """, JdbcDiagnosticServiceRepository::mapService, laboratoryId);
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeServiceId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from catalog.diagnostic_services
                where laboratory_id = ? and code = ? and service_id <> ?
                """, Integer.class, laboratoryId, code, excludeServiceId == null ? "" : excludeServiceId);
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void replaceComponentLinks(String serviceId, List<ServiceComponentLink> links) {
        jdbcTemplate.update("delete from catalog.diagnostic_service_component_links where service_id = ?", serviceId);
        for (ServiceComponentLink link : links) {
            jdbcTemplate.update("""
                    insert into catalog.diagnostic_service_component_links
                        (link_id, service_id, component_type, component_ref_id, display_order)
                    values (?, ?, ?, ?, ?)
                    """, link.linkId(), serviceId, link.componentType(), link.componentRefId(), link.displayOrder());
        }
    }

    @Override
    public List<ServiceComponentLink> findComponentLinks(String serviceId) {
        return jdbcTemplate.query("""
                select link_id, service_id, component_type, component_ref_id, display_order
                from catalog.diagnostic_service_component_links
                where service_id = ?
                """, JdbcDiagnosticServiceRepository::mapLink, serviceId);
    }

    private static DiagnosticService mapService(ResultSet resultSet, int rowNumber) throws SQLException {
        return new DiagnosticService(
                resultSet.getString("service_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("code"),
                new LocalizedText(resultSet.getString("name_en"), resultSet.getString("name_es")),
                resultSet.getString("category_id"),
                resultSet.getString("service_type"),
                resultSet.getString("status"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static ServiceComponentLink mapLink(ResultSet resultSet, int rowNumber) throws SQLException {
        int displayOrder = resultSet.getInt("display_order");
        return new ServiceComponentLink(
                resultSet.getString("link_id"),
                resultSet.getString("service_id"),
                resultSet.getString("component_type"),
                resultSet.getString("component_ref_id"),
                resultSet.wasNull() ? null : displayOrder);
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
