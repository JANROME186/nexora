package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackage;
import com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain.ImagingDeliveryPackageRepository;
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
public class JdbcImagingDeliveryPackageRepository implements ImagingDeliveryPackageRepository {

    private static final String SELECT_SQL = """
            select package_id, tenant_id, study_id, patient_id, delivery_format,
                   delivery_status, portal_access_token, expires_at, created_by, created_at,
                   updated_by, updated_at
            from imaging_operations.imaging_delivery_packages
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcImagingDeliveryPackageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImagingDeliveryPackage save(ImagingDeliveryPackage deliveryPackage) {
        jdbcTemplate.update("""
                insert into imaging_operations.imaging_delivery_packages
                    (package_id, tenant_id, study_id, patient_id, delivery_format,
                     delivery_status, portal_access_token, expires_at, created_by, created_at,
                     updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (package_id) do update set
                    delivery_format = excluded.delivery_format,
                    delivery_status = excluded.delivery_status,
                    portal_access_token = excluded.portal_access_token,
                    expires_at = excluded.expires_at,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                deliveryPackage.packageId(), deliveryPackage.tenantId(), deliveryPackage.studyId(),
                deliveryPackage.patientId(), deliveryPackage.deliveryFormat(), deliveryPackage.deliveryStatus(),
                deliveryPackage.portalAccessToken(),
                deliveryPackage.expiresAt() != null ? Timestamp.from(deliveryPackage.expiresAt()) : null,
                deliveryPackage.createdBy(), Timestamp.from(deliveryPackage.createdAt()),
                deliveryPackage.updatedBy(), Timestamp.from(deliveryPackage.updatedAt()));
        return deliveryPackage;
    }

    @Override
    public Optional<ImagingDeliveryPackage> findById(String tenantId, String packageId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and package_id = ?",
                JdbcImagingDeliveryPackageRepository::map, tenantId, packageId).stream().findFirst();
    }

    @Override
    public List<ImagingDeliveryPackage> findByStudyId(String tenantId, String studyId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and study_id = ?",
                JdbcImagingDeliveryPackageRepository::map, tenantId, studyId);
    }

    @Override
    public List<ImagingDeliveryPackage> findByTenantAndPatient(String tenantId, String patientId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and patient_id = ?",
                JdbcImagingDeliveryPackageRepository::map, tenantId, patientId);
    }

    private static ImagingDeliveryPackage map(ResultSet rs, int rowNum) throws SQLException {
        Timestamp expiresAtTs = rs.getTimestamp("expires_at");
        return new ImagingDeliveryPackage(
                rs.getString("package_id"),
                rs.getString("tenant_id"),
                rs.getString("study_id"),
                rs.getString("patient_id"),
                rs.getString("delivery_format"),
                rs.getString("delivery_status"),
                rs.getString("portal_access_token"),
                expiresAtTs != null ? expiresAtTs.toInstant() : null,
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
