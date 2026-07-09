package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.adapter.out.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRequest;

@Repository
@Profile("local")
class JdbcPatientRegistrationRepository implements PatientRegistrationRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPatientRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PatientRegistrationRequest save(PatientRegistrationRequest registration) {
        jdbcTemplate.update("""
                insert into people.patient_registrations (
                    registration_request_id, tenant_id, laboratory_id, branch_id, intake_channel,
                    candidate_patient_id, registration_kind,
                    normalized_family_name, normalized_given_name, birth_date,
                    draft_given_name, draft_family_name, draft_document_type, draft_document_number,
                    draft_patient_code, outcome, outcome_patient_id, actor_id, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (registration_request_id) do update set
                    outcome = excluded.outcome,
                    outcome_patient_id = excluded.outcome_patient_id,
                    candidate_patient_id = excluded.candidate_patient_id,
                    updated_at = excluded.updated_at
                """,
                registration.registrationRequestId(), registration.tenantId(),
                registration.laboratoryId(), registration.branchId(), registration.intakeChannel(),
                registration.candidatePatientId(), registration.registrationKind(),
                registration.normalizedFamilyName(), registration.normalizedGivenName(),
                sqlDate(registration.birthDate()),
                registration.draftGivenName(), registration.draftFamilyName(),
                registration.draftDocumentType(), registration.draftDocumentNumber(),
                registration.draftPatientCode(), registration.outcome(),
                registration.outcomePatientId(), registration.actorId(),
                Timestamp.from(registration.createdAt()), Timestamp.from(registration.updatedAt()));
        return registration;
    }

    @Override
    public Optional<PatientRegistrationRequest> findById(String registrationRequestId) {
        return jdbcTemplate.query(
                "select * from people.patient_registrations where registration_request_id = ?",
                JdbcPatientRegistrationRepository::map, registrationRequestId).stream().findFirst();
    }

    @Override
    public List<PatientRegistrationRequest> findByTenantId(String tenantId) {
        return jdbcTemplate.query("select * from people.patient_registrations where tenant_id = ?",
                JdbcPatientRegistrationRepository::map, tenantId);
    }

    private static PatientRegistrationRequest map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PatientRegistrationRequest(
                resultSet.getString("registration_request_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("branch_id"),
                resultSet.getString("intake_channel"),
                resultSet.getString("candidate_patient_id"),
                resultSet.getString("registration_kind"),
                resultSet.getString("normalized_family_name"),
                resultSet.getString("normalized_given_name"),
                localDate(resultSet, "birth_date"),
                resultSet.getString("draft_given_name"),
                resultSet.getString("draft_family_name"),
                resultSet.getString("draft_document_type"),
                resultSet.getString("draft_document_number"),
                resultSet.getString("draft_patient_code"),
                resultSet.getString("outcome"),
                resultSet.getString("outcome_patient_id"),
                resultSet.getString("actor_id"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant());
    }

    private static Date sqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }
}
