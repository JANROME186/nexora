package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.adapter.out.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientConsent;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientEmergencyContact;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

@Repository
@Profile("local")
class JdbcPatientRepository implements PatientRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPatientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Patient save(Patient patient) {
        String givenName = nameFieldOrNull(patient.name(), PersonName::givenName);
        String familyName = nameFieldOrNull(patient.name(), PersonName::familyName);
        jdbcTemplate.update("""
                insert into people.patients (
                    patient_id, tenant_id, laboratory_id, patient_code,
                    given_name, middle_name, family_name, second_family_name, preferred_name,
                    normalized_given_name, normalized_family_name,
                    birth_date, sex_at_birth,
                    primary_document_type, primary_document_number, primary_document_issuing_country,
                    primary_document_issued_at, primary_document_expires_at,
                    address_country, address_state, address_city, address_postal_code, address_street,
                    preferred_locale, status, merged_into_patient_id, version, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (patient_id) do update set
                    patient_code = excluded.patient_code,
                    given_name = excluded.given_name,
                    middle_name = excluded.middle_name,
                    family_name = excluded.family_name,
                    second_family_name = excluded.second_family_name,
                    preferred_name = excluded.preferred_name,
                    normalized_given_name = excluded.normalized_given_name,
                    normalized_family_name = excluded.normalized_family_name,
                    birth_date = excluded.birth_date,
                    sex_at_birth = excluded.sex_at_birth,
                    primary_document_type = excluded.primary_document_type,
                    primary_document_number = excluded.primary_document_number,
                    primary_document_issuing_country = excluded.primary_document_issuing_country,
                    primary_document_issued_at = excluded.primary_document_issued_at,
                    primary_document_expires_at = excluded.primary_document_expires_at,
                    address_country = excluded.address_country,
                    address_state = excluded.address_state,
                    address_city = excluded.address_city,
                    address_postal_code = excluded.address_postal_code,
                    address_street = excluded.address_street,
                    preferred_locale = excluded.preferred_locale,
                    status = excluded.status,
                    merged_into_patient_id = excluded.merged_into_patient_id,
                    version = excluded.version,
                    updated_at = excluded.updated_at
                """,
                patient.patientId(), patient.tenantId(), patient.laboratoryId(), patient.patientCode(),
                givenName,
                nameFieldOrNull(patient.name(), PersonName::middleName),
                familyName,
                nameFieldOrNull(patient.name(), PersonName::secondFamilyName),
                nameFieldOrNull(patient.name(), PersonName::preferredName),
                PeopleValidation.normalizeNaturalKeyToken(givenName),
                PeopleValidation.normalizeNaturalKeyToken(familyName),
                sqlDate(patient.birthDate()),
                patient.sexAtBirth(),
                docFieldOrNull(patient.primaryDocument(), PersonDocument::documentType),
                docFieldOrNull(patient.primaryDocument(), PersonDocument::documentNumber),
                docFieldOrNull(patient.primaryDocument(), PersonDocument::issuingCountry),
                sqlDate(patient.primaryDocument() == null ? null : patient.primaryDocument().issuedAt()),
                sqlDate(patient.primaryDocument() == null ? null : patient.primaryDocument().expiresAt()),
                addressField(patient.address(), PersonAddress::country),
                addressField(patient.address(), PersonAddress::state),
                addressField(patient.address(), PersonAddress::city),
                addressField(patient.address(), PersonAddress::postalCode),
                addressField(patient.address(), PersonAddress::street),
                patient.preferredLocale(),
                patient.status(),
                patient.mergedIntoPatientId(),
                patient.version(),
                Timestamp.from(patient.createdAt()),
                Timestamp.from(patient.updatedAt()));
        return patient;
    }

    @Override
    public Optional<Patient> findById(String patientId) {
        return jdbcTemplate.query("select * from people.patients where patient_id = ?",
                JdbcPatientRepository::mapPatient, patientId).stream().findFirst();
    }

    @Override
    public List<Patient> findByLaboratoryId(String laboratoryId) {
        return jdbcTemplate.query("select * from people.patients where laboratory_id = ?",
                JdbcPatientRepository::mapPatient, laboratoryId);
    }

    @Override
    public List<Patient> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        return jdbcTemplate.query("""
                select * from people.patients
                where tenant_id = ?
                  and (? is null or normalized_family_name = ?)
                  and (? is null or normalized_given_name = ?)
                  and (? is null or birth_date = ?)
                """,
                JdbcPatientRepository::mapPatient,
                tenantId,
                normalizedFamilyName, normalizedFamilyName,
                normalizedGivenName, normalizedGivenName,
                sqlDate(birthDate), sqlDate(birthDate));
    }

    @Override
    public boolean existsByPatientCode(String tenantId, String patientCode, String excludePatientId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from people.patients
                where tenant_id = ? and patient_code = ? and patient_id <> ?
                """, Integer.class, tenantId, patientCode, excludePatientId == null ? "" : excludePatientId);
        return count != null && count > 0;
    }

    @Override
    public void saveRepresentative(PatientRepresentative representative) {
        jdbcTemplate.update("""
                insert into people.patient_representatives (
                    representative_id, patient_id, relationship,
                    given_name, middle_name, family_name, second_family_name,
                    document_type, document_number,
                    authorization_from, authorization_to, status)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (representative_id) do update set
                    status = excluded.status,
                    authorization_to = excluded.authorization_to
                """,
                representative.representativeId(), representative.patientId(), representative.relationship(),
                nameFieldOrNull(representative.representativeName(), PersonName::givenName),
                nameFieldOrNull(representative.representativeName(), PersonName::middleName),
                nameFieldOrNull(representative.representativeName(), PersonName::familyName),
                nameFieldOrNull(representative.representativeName(), PersonName::secondFamilyName),
                docFieldOrNull(representative.representativeDocument(), PersonDocument::documentType),
                docFieldOrNull(representative.representativeDocument(), PersonDocument::documentNumber),
                sqlDate(representative.authorizationFrom()),
                sqlDate(representative.authorizationTo()),
                representative.status());
    }

    @Override
    public List<PatientRepresentative> findRepresentatives(String patientId) {
        return jdbcTemplate.query("select * from people.patient_representatives where patient_id = ?",
                JdbcPatientRepository::mapRepresentative, patientId);
    }

    @Override
    public Optional<PatientRepresentative> findRepresentativeById(String representativeId) {
        return jdbcTemplate.query("select * from people.patient_representatives where representative_id = ?",
                JdbcPatientRepository::mapRepresentative, representativeId).stream().findFirst();
    }

    @Override
    public void saveConsent(PatientConsent consent) {
        jdbcTemplate.update("""
                insert into people.patient_consents (
                    consent_id, patient_id, consent_type, granted, granted_by,
                    granted_at, revoked_at, evidence_reference)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                consent.consentId(), consent.patientId(), consent.consentType(), consent.granted(),
                consent.grantedBy(), Timestamp.from(consent.grantedAt()),
                consent.revokedAt() == null ? null : Timestamp.from(consent.revokedAt()),
                consent.evidenceReference());
    }

    @Override
    public List<PatientConsent> findConsents(String patientId) {
        return jdbcTemplate.query("select * from people.patient_consents where patient_id = ?",
                JdbcPatientRepository::mapConsent, patientId);
    }

    @Override
    public Optional<PatientConsent> findConsentById(String consentId) {
        return jdbcTemplate.query("select * from people.patient_consents where consent_id = ?",
                JdbcPatientRepository::mapConsent, consentId).stream().findFirst();
    }

    @Override
    public void saveDocument(PatientDocument document) {
        jdbcTemplate.update("""
                insert into people.patient_documents (
                    document_id, patient_id, category, file_reference, uploaded_at, expires_at)
                values (?, ?, ?, ?, ?, ?)
                """,
                document.documentId(), document.patientId(), document.category(),
                document.fileReference(), Timestamp.from(document.uploadedAt()), sqlDate(document.expiresAt()));
    }

    @Override
    public List<PatientDocument> findDocuments(String patientId) {
        return jdbcTemplate.query("select * from people.patient_documents where patient_id = ?",
                JdbcPatientRepository::mapDocument, patientId);
    }

    @Override
    public void deleteDocument(String documentId) {
        jdbcTemplate.update("delete from people.patient_documents where document_id = ?", documentId);
    }

    @Override
    public void saveEmergencyContact(PatientEmergencyContact emergencyContact) {
        jdbcTemplate.update("""
                insert into people.patient_emergency_contacts (
                    emergency_contact_id, patient_id, relationship,
                    given_name, middle_name, family_name, second_family_name,
                    phone_country_code, phone_national_number, preferred)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                emergencyContact.emergencyContactId(), emergencyContact.patientId(),
                emergencyContact.relationship(),
                nameFieldOrNull(emergencyContact.name(), PersonName::givenName),
                nameFieldOrNull(emergencyContact.name(), PersonName::middleName),
                nameFieldOrNull(emergencyContact.name(), PersonName::familyName),
                nameFieldOrNull(emergencyContact.name(), PersonName::secondFamilyName),
                emergencyContact.phoneCountryCode(), emergencyContact.phoneNationalNumber(),
                emergencyContact.preferred());
    }

    @Override
    public List<PatientEmergencyContact> findEmergencyContacts(String patientId) {
        return jdbcTemplate.query("select * from people.patient_emergency_contacts where patient_id = ?",
                JdbcPatientRepository::mapEmergencyContact, patientId);
    }

    // -- Result-set mappers --------------------------------------------------------------------

    private static Patient mapPatient(ResultSet resultSet, int rowNumber) throws SQLException {
        PersonName name = new PersonName(
                resultSet.getString("given_name"),
                resultSet.getString("middle_name"),
                resultSet.getString("family_name"),
                resultSet.getString("second_family_name"),
                resultSet.getString("preferred_name"));
        PersonDocument document = new PersonDocument(
                resultSet.getString("primary_document_type"),
                resultSet.getString("primary_document_number"),
                resultSet.getString("primary_document_issuing_country"),
                localDate(resultSet, "primary_document_issued_at"),
                localDate(resultSet, "primary_document_expires_at"));
        PersonAddress address = new PersonAddress(
                resultSet.getString("address_country"),
                resultSet.getString("address_state"),
                resultSet.getString("address_city"),
                null,
                resultSet.getString("address_postal_code"),
                resultSet.getString("address_street"),
                null,
                null);
        return new Patient(
                resultSet.getString("patient_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("patient_code"),
                name,
                localDate(resultSet, "birth_date"),
                resultSet.getString("sex_at_birth"),
                document,
                address,
                resultSet.getString("preferred_locale"),
                resultSet.getString("status"),
                resultSet.getString("merged_into_patient_id"),
                resultSet.getInt("version"),
                instant(resultSet, "created_at"),
                instant(resultSet, "updated_at"));
    }

    private static PatientRepresentative mapRepresentative(ResultSet resultSet, int rowNumber) throws SQLException {
        PersonName name = new PersonName(
                resultSet.getString("given_name"),
                resultSet.getString("middle_name"),
                resultSet.getString("family_name"),
                resultSet.getString("second_family_name"),
                null);
        PersonDocument document = new PersonDocument(
                resultSet.getString("document_type"),
                resultSet.getString("document_number"),
                null, null, null);
        return new PatientRepresentative(
                resultSet.getString("representative_id"),
                resultSet.getString("patient_id"),
                resultSet.getString("relationship"),
                name,
                document,
                localDate(resultSet, "authorization_from"),
                localDate(resultSet, "authorization_to"),
                resultSet.getString("status"));
    }

    private static PatientConsent mapConsent(ResultSet resultSet, int rowNumber) throws SQLException {
        Timestamp revoked = resultSet.getTimestamp("revoked_at");
        return new PatientConsent(
                resultSet.getString("consent_id"),
                resultSet.getString("patient_id"),
                resultSet.getString("consent_type"),
                resultSet.getBoolean("granted"),
                resultSet.getString("granted_by"),
                resultSet.getTimestamp("granted_at").toInstant(),
                revoked == null ? null : revoked.toInstant(),
                resultSet.getString("evidence_reference"));
    }

    private static PatientDocument mapDocument(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PatientDocument(
                resultSet.getString("document_id"),
                resultSet.getString("patient_id"),
                resultSet.getString("category"),
                resultSet.getString("file_reference"),
                resultSet.getTimestamp("uploaded_at").toInstant(),
                localDate(resultSet, "expires_at"));
    }

    private static PatientEmergencyContact mapEmergencyContact(ResultSet resultSet, int rowNumber) throws SQLException {
        PersonName name = new PersonName(
                resultSet.getString("given_name"),
                resultSet.getString("middle_name"),
                resultSet.getString("family_name"),
                resultSet.getString("second_family_name"),
                null);
        return new PatientEmergencyContact(
                resultSet.getString("emergency_contact_id"),
                resultSet.getString("patient_id"),
                resultSet.getString("relationship"),
                name,
                resultSet.getString("phone_country_code"),
                resultSet.getString("phone_national_number"),
                resultSet.getBoolean("preferred"));
    }

    // -- Small helpers -------------------------------------------------------------------------

    private static <T> String nameFieldOrNull(PersonName name, java.util.function.Function<PersonName, String> f) {
        return name == null ? null : f.apply(name);
    }

    private static String docFieldOrNull(PersonDocument document,
            java.util.function.Function<PersonDocument, String> f) {
        return document == null ? null : f.apply(document);
    }

    private static String addressField(PersonAddress address,
            java.util.function.Function<PersonAddress, String> f) {
        return address == null ? null : f.apply(address);
    }

    private static Date sqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private static LocalDate localDate(ResultSet resultSet, String columnName) throws SQLException {
        Date value = resultSet.getDate(columnName);
        return value == null ? null : value.toLocalDate();
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }

}
