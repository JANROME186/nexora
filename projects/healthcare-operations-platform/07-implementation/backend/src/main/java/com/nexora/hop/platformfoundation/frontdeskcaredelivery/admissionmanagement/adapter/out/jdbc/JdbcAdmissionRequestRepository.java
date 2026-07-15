package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionCatalogSelection;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequestRepository;

@Repository
@Profile("local")
class JdbcAdmissionRequestRepository implements AdmissionRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcAdmissionRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AdmissionRequest save(AdmissionRequest admission) {
        jdbcTemplate.update("""
                insert into care_delivery.admission_requests (
                    admission_id, tenant_id, laboratory_id, branch_id, visit_id, patient_id, doctor_id,
                    clinical_notes_draft, consent_confirmed, sample_requirements_acknowledged,
                    admission_status, created_order_id, rejection_reason, actor_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (admission_id) do update set
                    clinical_notes_draft = excluded.clinical_notes_draft,
                    consent_confirmed = excluded.consent_confirmed,
                    sample_requirements_acknowledged = excluded.sample_requirements_acknowledged,
                    admission_status = excluded.admission_status,
                    created_order_id = excluded.created_order_id,
                    rejection_reason = excluded.rejection_reason,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                admission.admissionId(), admission.tenantId(), admission.laboratoryId(), admission.branchId(),
                admission.visitId(), admission.patientId(), admission.doctorId(), admission.clinicalNotesDraft(),
                admission.consentConfirmed(), admission.sampleRequirementsAcknowledged(),
                admission.admissionStatus(), admission.createdOrderId(), admission.rejectionReason(),
                admission.actorId(), admission.version(), Timestamp.from(admission.createdAt()),
                Timestamp.from(admission.updatedAt()));
        return admission;
    }

    @Override
    public Optional<AdmissionRequest> findById(String admissionId) {
        return jdbcTemplate.query("select * from care_delivery.admission_requests where admission_id = ?",
                JdbcAdmissionRequestRepository::map, admissionId).stream().findFirst();
    }

    @Override
    public List<AdmissionRequest> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from care_delivery.admission_requests where tenant_id = ?",
                JdbcAdmissionRequestRepository::map, tenantId);
    }

    @Override
    public AdmissionCatalogSelection saveSelection(AdmissionCatalogSelection selection) {
        jdbcTemplate.update("""
                insert into care_delivery.admission_catalog_selections (
                    selection_id, admission_id, test_definition_id, catalog_item_kind, quantity)
                values (?, ?, ?, ?, ?)
                on conflict (selection_id) do nothing
                """,
                selection.selectionId(), selection.admissionId(), selection.testDefinitionId(),
                selection.catalogItemKind(), selection.quantity());
        return selection;
    }

    @Override
    public List<AdmissionCatalogSelection> findSelections(String admissionId) {
        return jdbcTemplate.query(
                "select * from care_delivery.admission_catalog_selections where admission_id = ?",
                (resultSet, rowNumber) -> new AdmissionCatalogSelection(
                        resultSet.getString("selection_id"), resultSet.getString("admission_id"),
                        resultSet.getString("test_definition_id"), resultSet.getString("catalog_item_kind"),
                        resultSet.getInt("quantity")),
                admissionId);
    }

    private static AdmissionRequest map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new AdmissionRequest(
                resultSet.getString("admission_id"), resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"), resultSet.getString("branch_id"),
                resultSet.getString("visit_id"), resultSet.getString("patient_id"), resultSet.getString("doctor_id"),
                resultSet.getString("clinical_notes_draft"), resultSet.getBoolean("consent_confirmed"),
                resultSet.getBoolean("sample_requirements_acknowledged"), resultSet.getString("admission_status"),
                resultSet.getString("created_order_id"), resultSet.getString("rejection_reason"),
                resultSet.getString("actor_id"), resultSet.getInt("version"),
                resultSet.getTimestamp("created_at").toInstant(), resultSet.getTimestamp("updated_at").toInstant());
    }
}
