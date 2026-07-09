package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.adapter.out.memory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientConsent;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientEmergencyContact;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

@Repository
@Profile("!local")
class InMemoryPatientRepository implements PatientRepository {

    private final Map<String, Patient> patients = new ConcurrentHashMap<>();
    private final Map<String, List<PatientRepresentative>> representatives = new ConcurrentHashMap<>();
    private final Map<String, List<PatientConsent>> consents = new ConcurrentHashMap<>();
    private final Map<String, List<PatientDocument>> documents = new ConcurrentHashMap<>();
    private final Map<String, List<PatientEmergencyContact>> emergencyContacts = new ConcurrentHashMap<>();

    @Override
    public Patient save(Patient patient) {
        patients.put(patient.patientId(), patient);
        return patient;
    }

    @Override
    public Optional<Patient> findById(String patientId) {
        return Optional.ofNullable(patients.get(patientId));
    }

    @Override
    public List<Patient> findByLaboratoryId(String laboratoryId) {
        return patients.values().stream()
                .filter(patient -> patient.laboratoryId().equals(laboratoryId))
                .toList();
    }

    @Override
    public List<Patient> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        return patients.values().stream()
                .filter(patient -> patient.tenantId().equals(tenantId))
                .filter(patient -> normalizedFamilyName == null
                        || normalizedFamilyName.equals(PeopleValidation.normalizeNaturalKeyToken(
                                patient.name() == null ? null : patient.name().familyName())))
                .filter(patient -> normalizedGivenName == null
                        || normalizedGivenName.equals(PeopleValidation.normalizeNaturalKeyToken(
                                patient.name() == null ? null : patient.name().givenName())))
                .filter(patient -> birthDate == null || Objects.equals(birthDate, patient.birthDate()))
                .toList();
    }

    @Override
    public boolean existsByPatientCode(String tenantId, String patientCode, String excludePatientId) {
        return patients.values().stream()
                .anyMatch(patient -> patient.tenantId().equals(tenantId)
                        && patient.patientCode().equals(patientCode)
                        && !patient.patientId().equals(excludePatientId));
    }

    @Override
    public void saveRepresentative(PatientRepresentative representative) {
        representatives.computeIfAbsent(representative.patientId(), key -> new ArrayList<>())
                .add(representative);
    }

    @Override
    public List<PatientRepresentative> findRepresentatives(String patientId) {
        return List.copyOf(representatives.getOrDefault(patientId, List.of()));
    }

    @Override
    public Optional<PatientRepresentative> findRepresentativeById(String representativeId) {
        return representatives.values().stream()
                .flatMap(List::stream)
                .filter(rep -> rep.representativeId().equals(representativeId))
                .findFirst();
    }

    @Override
    public void saveConsent(PatientConsent consent) {
        consents.computeIfAbsent(consent.patientId(), key -> new ArrayList<>()).add(consent);
    }

    @Override
    public List<PatientConsent> findConsents(String patientId) {
        return List.copyOf(consents.getOrDefault(patientId, List.of()));
    }

    @Override
    public Optional<PatientConsent> findConsentById(String consentId) {
        return consents.values().stream()
                .flatMap(List::stream)
                .filter(consent -> consent.consentId().equals(consentId))
                .findFirst();
    }

    @Override
    public void saveDocument(PatientDocument document) {
        documents.computeIfAbsent(document.patientId(), key -> new ArrayList<>()).add(document);
    }

    @Override
    public List<PatientDocument> findDocuments(String patientId) {
        return List.copyOf(documents.getOrDefault(patientId, List.of()));
    }

    @Override
    public void deleteDocument(String documentId) {
        documents.values().forEach(list -> list.removeIf(document -> document.documentId().equals(documentId)));
    }

    @Override
    public void saveEmergencyContact(PatientEmergencyContact emergencyContact) {
        emergencyContacts.computeIfAbsent(emergencyContact.patientId(), key -> new ArrayList<>())
                .add(emergencyContact);
    }

    @Override
    public List<PatientEmergencyContact> findEmergencyContacts(String patientId) {
        return List.copyOf(emergencyContacts.getOrDefault(patientId, List.of()));
    }
}
