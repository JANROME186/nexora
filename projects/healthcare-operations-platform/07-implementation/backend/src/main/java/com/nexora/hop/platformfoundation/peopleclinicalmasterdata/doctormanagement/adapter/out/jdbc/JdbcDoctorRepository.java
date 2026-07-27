package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.adapter.out.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.ProfessionalCredential;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.SpecialtyAssignment;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

@Repository
@Profile("local")
class JdbcDoctorRepository implements DoctorRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcDoctorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Doctor save(Doctor doctor) {
        String givenName = doctor.name() == null ? null : doctor.name().givenName();
        String familyName = doctor.name() == null ? null : doctor.name().familyName();
        jdbcTemplate.update("""
                insert into people.doctors (
                    doctor_id, tenant_id, laboratory_id, doctor_code,
                    given_name, middle_name, family_name, second_family_name,
                    normalized_given_name, normalized_family_name,
                    primary_document_type, primary_document_number, primary_document_issuing_country,
                    primary_document_issued_at, primary_document_expires_at,
                    address_country, address_city, address_street,
                    doctor_type, status, portal_status, portal_email,
                    version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (doctor_id) do update set
                    doctor_code = excluded.doctor_code,
                    given_name = excluded.given_name,
                    middle_name = excluded.middle_name,
                    family_name = excluded.family_name,
                    second_family_name = excluded.second_family_name,
                    normalized_given_name = excluded.normalized_given_name,
                    normalized_family_name = excluded.normalized_family_name,
                    primary_document_type = excluded.primary_document_type,
                    primary_document_number = excluded.primary_document_number,
                    primary_document_issuing_country = excluded.primary_document_issuing_country,
                    primary_document_issued_at = excluded.primary_document_issued_at,
                    primary_document_expires_at = excluded.primary_document_expires_at,
                    address_country = excluded.address_country,
                    address_city = excluded.address_city,
                    address_street = excluded.address_street,
                    doctor_type = excluded.doctor_type,
                    status = excluded.status,
                    portal_status = excluded.portal_status,
                    portal_email = excluded.portal_email,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                doctor.doctorId(), doctor.tenantId(), doctor.laboratoryId(), doctor.doctorCode(),
                givenName,
                doctor.name() == null ? null : doctor.name().middleName(),
                familyName,
                doctor.name() == null ? null : doctor.name().secondFamilyName(),
                PeopleValidation.normalizeNaturalKeyToken(givenName),
                PeopleValidation.normalizeNaturalKeyToken(familyName),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().documentType(),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().documentNumber(),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().issuingCountry(),
                sqlDate(doctor.primaryDocument() == null ? null : doctor.primaryDocument().issuedAt()),
                sqlDate(doctor.primaryDocument() == null ? null : doctor.primaryDocument().expiresAt()),
                doctor.address() == null ? null : doctor.address().country(),
                doctor.address() == null ? null : doctor.address().city(),
                doctor.address() == null ? null : doctor.address().street(),
                doctor.doctorType(), doctor.status(), doctor.portalStatus(), doctor.portalEmail(),
                doctor.version(), Timestamp.from(doctor.createdAt()), Timestamp.from(doctor.updatedAt()));
        return doctor;
    }

    @Override
    public Optional<Doctor> findById(String doctorId) {
        return jdbcTemplate.query("select * from people.doctors where doctor_id = ?",
                JdbcDoctorRepository::mapDoctor, doctorId).stream().findFirst();
    }

    @Override
    public List<Doctor> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("select * from people.doctors where laboratory_id = ?",
                JdbcDoctorRepository::mapDoctor, laboratoryId);
    }

    @Override
    public List<Doctor> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        // Doctors do not carry birthDate in the business model; the parameter stays in the port
        // for symmetry with Patient. See MVP-MOD-003-BE-001 QA evidence for the deferred plan.
        StringBuilder sql = new StringBuilder("""
                select * from people.doctors
                where tenant_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (normalizedFamilyName != null) {
            sql.append("  and normalized_family_name = ?\n");
            params.add(normalizedFamilyName);
        }
        if (normalizedGivenName != null) {
            sql.append("  and normalized_given_name = ?\n");
            params.add(normalizedGivenName);
        }
        return jdbcTemplate.query(sql.toString(), JdbcDoctorRepository::mapDoctor, params.toArray());
    }

    @Override
    public boolean existsByDoctorCode(String tenantId, String doctorCode, String excludeDoctorId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from people.doctors
                where tenant_id = ? and doctor_code = ? and doctor_id <> ?
                """, Integer.class, tenantId, doctorCode, excludeDoctorId == null ? "" : excludeDoctorId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByPrimaryDocument(String tenantId, String documentType, String documentNumber,
            String excludeDoctorId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from people.doctors
                where tenant_id = ? and primary_document_type = ? and primary_document_number = ?
                  and doctor_id <> ?
                """, Integer.class, tenantId, documentType, documentNumber,
                excludeDoctorId == null ? "" : excludeDoctorId);
        return count != null && count > 0;
    }

    @Override
    public void saveCredential(ProfessionalCredential credential) {
        jdbcTemplate.update("""
                insert into people.doctor_credentials (
                    credential_id, doctor_id, credential_type, credential_number, issuing_authority,
                    issuing_country, issued_at, expires_at, verification_status, verified_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (credential_id) do update set
                    verification_status = excluded.verification_status,
                    verified_at = excluded.verified_at
                """,
                credential.credentialId(), credential.doctorId(), credential.credentialType(),
                credential.credentialNumber(), credential.issuingAuthority(), credential.issuingCountry(),
                sqlDate(credential.issuedAt()), sqlDate(credential.expiresAt()),
                credential.verificationStatus(),
                credential.verifiedAt() == null ? null : Timestamp.from(credential.verifiedAt()));
    }

    @Override
    public List<ProfessionalCredential> findCredentials(String doctorId) {
        return jdbcTemplate.query("select * from people.doctor_credentials where doctor_id = ?",
                JdbcDoctorRepository::mapCredential, doctorId);
    }

    @Override
    public Optional<ProfessionalCredential> findCredentialById(String credentialId) {
        return jdbcTemplate.query("select * from people.doctor_credentials where credential_id = ?",
                JdbcDoctorRepository::mapCredential, credentialId).stream().findFirst();
    }

    @Override
    public List<ProfessionalCredential> findVerifiedCredentialsExpiringBefore(LocalDate asOfDate) {
        return jdbcTemplate.query("""
                select * from people.doctor_credentials
                where verification_status = ? and expires_at is not null and expires_at < ?
                """, JdbcDoctorRepository::mapCredential,
                ProfessionalCredential.STATUS_VERIFIED, sqlDate(asOfDate));
    }

    @Override
    public void saveSpecialty(SpecialtyAssignment specialty) {
        jdbcTemplate.update("""
                insert into people.doctor_specialty_assignments (
                    assignment_id, doctor_id, specialty_code, primary_flag)
                values (?, ?, ?, ?)
                on conflict (assignment_id) do update set
                    specialty_code = excluded.specialty_code,
                    primary_flag = excluded.primary_flag
                """,
                specialty.assignmentId(), specialty.doctorId(), specialty.specialtyCode(),
                specialty.primary());
    }

    @Override
    public List<SpecialtyAssignment> findSpecialties(String doctorId) {
        return jdbcTemplate.query("select * from people.doctor_specialty_assignments where doctor_id = ?",
                JdbcDoctorRepository::mapSpecialty, doctorId);
    }

    @Override
    public void deleteSpecialty(String assignmentId) {
        jdbcTemplate.update("delete from people.doctor_specialty_assignments where assignment_id = ?",
                assignmentId);
    }

    private static Doctor mapDoctor(ResultSet resultSet, int rowNumber) throws SQLException {
        PersonName name = new PersonName(
                resultSet.getString("given_name"),
                resultSet.getString("middle_name"),
                resultSet.getString("family_name"),
                resultSet.getString("second_family_name"),
                null);
        PersonDocument document = new PersonDocument(
                resultSet.getString("primary_document_type"),
                resultSet.getString("primary_document_number"),
                resultSet.getString("primary_document_issuing_country"),
                localDate(resultSet, "primary_document_issued_at"),
                localDate(resultSet, "primary_document_expires_at"));
        PersonAddress address = new PersonAddress(
                resultSet.getString("address_country"), null,
                resultSet.getString("address_city"), null, null,
                resultSet.getString("address_street"), null, null);
        return new Doctor(
                resultSet.getString("doctor_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("doctor_code"),
                name, document, address,
                resultSet.getString("doctor_type"),
                resultSet.getString("status"),
                resultSet.getString("portal_status"),
                resultSet.getString("portal_email"),
                resultSet.getInt("version"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant());
    }

    private static ProfessionalCredential mapCredential(ResultSet resultSet, int rowNumber) throws SQLException {
        Timestamp verifiedAt = resultSet.getTimestamp("verified_at");
        return new ProfessionalCredential(
                resultSet.getString("credential_id"),
                resultSet.getString("doctor_id"),
                resultSet.getString("credential_type"),
                resultSet.getString("credential_number"),
                resultSet.getString("issuing_authority"),
                resultSet.getString("issuing_country"),
                localDate(resultSet, "issued_at"),
                localDate(resultSet, "expires_at"),
                resultSet.getString("verification_status"),
                verifiedAt == null ? null : verifiedAt.toInstant());
    }

    private static SpecialtyAssignment mapSpecialty(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SpecialtyAssignment(
                resultSet.getString("assignment_id"),
                resultSet.getString("doctor_id"),
                resultSet.getString("specialty_code"),
                resultSet.getBoolean("primary_flag"));
    }

    private static Date sqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }
}
