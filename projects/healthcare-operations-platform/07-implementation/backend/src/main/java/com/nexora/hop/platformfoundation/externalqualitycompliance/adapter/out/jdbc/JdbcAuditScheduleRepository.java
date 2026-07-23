package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditScheduleRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!local & !test")
public class JdbcAuditScheduleRepository implements AuditScheduleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcAuditScheduleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AuditSchedule save(AuditSchedule audit) {
        String sql = """
                INSERT INTO audit_schedules (
                    audit_id, audit_code, tenant_id, title, category, standard_reference,
                    lead_auditor_id, planned_start_date, planned_end_date, status, created_by, created_at
                ) VALUES (
                    :auditId, :auditCode, :tenantId, :title, :category, :standardReference,
                    :leadAuditorId, :plannedStartDate, :plannedEndDate, :status, :createdBy, :createdAt
                ) ON CONFLICT (audit_id) DO UPDATE SET
                    status = EXCLUDED.status
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("auditId", audit.getAuditId().toString())
                .addValue("auditCode", audit.getAuditCode())
                .addValue("tenantId", audit.getTenantId() != null ? audit.getTenantId().value() : null)
                .addValue("title", audit.getTitle())
                .addValue("category", audit.getCategory())
                .addValue("standardReference", audit.getStandardReference())
                .addValue("leadAuditorId", audit.getLeadAuditorId() != null ? audit.getLeadAuditorId().toString() : null)
                .addValue("plannedStartDate", audit.getPlannedStartDate() != null ? java.sql.Date.valueOf(audit.getPlannedStartDate()) : null)
                .addValue("plannedEndDate", audit.getPlannedEndDate() != null ? java.sql.Date.valueOf(audit.getPlannedEndDate()) : null)
                .addValue("status", audit.getStatus().name())
                .addValue("createdBy", audit.getAudit() != null ? audit.getAudit().createdBy() : "system")
                .addValue("createdAt", audit.getAudit() != null && audit.getAudit().createdAt() != null ? java.sql.Timestamp.valueOf(audit.getAudit().createdAt()) : java.sql.Timestamp.from(Instant.now()));

        jdbcTemplate.update(sql, params);
        return audit;
    }

    @Override
    public Optional<AuditSchedule> findById(UUID id) {
        String sql = "SELECT * FROM audit_schedules WHERE audit_id = :id";
        List<AuditSchedule> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id.toString()), this::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public List<AuditSchedule> findAll(String category, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM audit_schedules WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (category != null && !category.isBlank()) {
            sql.append(" AND LOWER(category) = LOWER(:category)");
            params.addValue("category", category.trim());
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND LOWER(status) = LOWER(:status)");
            params.addValue("status", status.trim());
        }

        return jdbcTemplate.query(sql.toString(), params, this::mapRow);
    }

    private AuditSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        String tenantStr = rs.getString("tenant_id");
        String createdBy = rs.getString("created_by");

        return new AuditSchedule(
                UUID.fromString(rs.getString("audit_id")),
                rs.getString("audit_code"),
                new TenantId(tenantStr != null ? tenantStr : UUID.randomUUID().toString()),
                rs.getString("title"),
                rs.getString("category"),
                rs.getString("standard_reference"),
                rs.getString("lead_auditor_id") != null ? UUID.fromString(rs.getString("lead_auditor_id")) : UUID.randomUUID(),
                rs.getDate("planned_start_date") != null ? rs.getDate("planned_start_date").toLocalDate() : null,
                rs.getDate("planned_end_date") != null ? rs.getDate("planned_end_date").toLocalDate() : null,
                AuditSchedule.Status.fromString(rs.getString("status")),
                List.of(),
                new AuditMetadata(createdBy != null ? createdBy : "system", LocalDateTime.now(), createdBy != null ? createdBy : "system", LocalDateTime.now())
        );
    }
}
