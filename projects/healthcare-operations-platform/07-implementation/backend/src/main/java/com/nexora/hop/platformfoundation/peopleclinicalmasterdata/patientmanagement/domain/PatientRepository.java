package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for the Patient aggregate. Only the patient-management application layer may
 * depend on this port; downstream contexts must query via published projections.
 */
public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(String patientId);

    List<Patient> findByLaboratoryId(String laboratoryId);

    List<Patient> searchByNaturalKey(
            String tenantId,
            String normalizedFamilyName,
            String normalizedGivenName,
            java.time.LocalDate birthDate);

    boolean existsByPatientCode(String tenantId, String patientCode, String excludePatientId);

    void saveRepresentative(PatientRepresentative representative);

    List<PatientRepresentative> findRepresentatives(String patientId);

    Optional<PatientRepresentative> findRepresentativeById(String representativeId);

    void saveConsent(PatientConsent consent);

    List<PatientConsent> findConsents(String patientId);

    Optional<PatientConsent> findConsentById(String consentId);

    void saveDocument(PatientDocument document);

    List<PatientDocument> findDocuments(String patientId);

    void deleteDocument(String documentId);

    void saveEmergencyContact(PatientEmergencyContact emergencyContact);

    List<PatientEmergencyContact> findEmergencyContacts(String patientId);
}
