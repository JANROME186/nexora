package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonDuplicateCandidate;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonNaturalKey;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonSearchEntry;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;

/**
 * Compiles the generatable outputs of BCM-PER-001 (Person Management). Because Person is not an
 * owning aggregate, this service projects Patient (BCM-PER-002) and Doctor (BCM-PER-003) into a
 * unified search view (RM-PER-001) and delegates duplicate-detection lookup to those aggregates.
 * The confidence-scoring, index-rebuild and merge-coordination custom rules stay deferred to
 * MVP-MOD-003-BE-002.
 */
@Service
public class PersonManagementService {

    private static final List<String> PERSON_KINDS = List.of(PersonKind.PATIENT, PersonKind.DOCTOR);

    private final PatientManagementService patientManagementService;
    private final DoctorManagementService doctorManagementService;
    private final AuditRecorder auditRecorder;

    public PersonManagementService(
            PatientManagementService patientManagementService,
            DoctorManagementService doctorManagementService,
            AuditRecorder auditRecorder) {
        this.patientManagementService = patientManagementService;
        this.doctorManagementService = doctorManagementService;
        this.auditRecorder = auditRecorder;
    }

    public List<PersonSearchEntry> search(SearchPersonsQuery query) {
        String tenantId = requiredText(query.tenantId(), "Tenant id is required.");
        String requestedKind = query.personKind() == null ? null
                : requiredOneOf(query.personKind(), "Person kind is invalid.", PERSON_KINDS.toArray(String[]::new));

        PersonNaturalKey naturalKey = PersonNaturalKey.normalize(
                query.familyName(), query.givenName(), query.birthDate(), null, null);

        List<PersonSearchEntry> results = new ArrayList<>();

        if (requestedKind == null || PersonKind.PATIENT.equals(requestedKind)) {
            List<Patient> matches = patientManagementService.searchByNaturalKey(tenantId,
                    naturalKey.normalizedFamilyName(), naturalKey.normalizedGivenName(),
                    query.birthDate());
            matches.forEach(patient -> results.add(toEntry(patient)));
        }
        if (requestedKind == null || PersonKind.DOCTOR.equals(requestedKind)) {
            List<Doctor> matches = doctorManagementService.searchByNaturalKey(tenantId,
                    naturalKey.normalizedFamilyName(), naturalKey.normalizedGivenName(), null);
            matches.forEach(doctor -> results.add(toEntry(doctor)));
        }
        return results;
    }

    /**
     * Simplified duplicate detector that returns entries matching by normalized natural key. The
     * tenant-configurable weighted confidence scoring (BCM-PER-001 RN-003) is delivered by
     * MVP-MOD-003-BE-002. Each match here reports a flat confidence value proportional to how many
     * key attributes matched exactly, so downstream flows can consume the shape immediately.
     */
    public List<PersonDuplicateCandidate> detectDuplicates(DetectPersonDuplicatesCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String requestedKind = command.personKind() == null ? null
                : requiredOneOf(command.personKind(), "Person kind is invalid.",
                        PERSON_KINDS.toArray(String[]::new));

        PersonNaturalKey key = PersonNaturalKey.normalize(command.familyName(), command.givenName(),
                command.birthDate(), command.sexAtBirth(), command.nationalIdentifier());

        List<PersonDuplicateCandidate> candidates = new ArrayList<>();
        String detectionId = UUID.randomUUID().toString();

        if (requestedKind == null || PersonKind.PATIENT.equals(requestedKind)) {
            patientManagementService.searchByNaturalKey(tenantId,
                    key.normalizedFamilyName(), key.normalizedGivenName(), command.birthDate())
                    .forEach(patient -> candidates.add(new PersonDuplicateCandidate(
                            PersonKind.PATIENT, patient.patientId(),
                            patient.name() == null ? null : patient.name().fullNameDisplay(),
                            computeConfidence(key, patient),
                            "natural_key_normalized_match")));
        }
        if (requestedKind == null || PersonKind.DOCTOR.equals(requestedKind)) {
            doctorManagementService.searchByNaturalKey(tenantId,
                    key.normalizedFamilyName(), key.normalizedGivenName(), null)
                    .forEach(doctor -> candidates.add(new PersonDuplicateCandidate(
                            PersonKind.DOCTOR, doctor.doctorId(),
                            doctor.name() == null ? null : doctor.name().fullNameDisplay(),
                            computeConfidence(key, doctor),
                            "natural_key_normalized_match")));
        }

        auditRecorder.recordSystemEvent(tenantId, "PersonDuplicateDetectionRequested",
                "PersonDuplicateDetection", detectionId,
                "{\"candidateCount\":%d}".formatted(candidates.size()));
        return candidates;
    }

    /** BCM-PER-001 RN-004 requires event-driven projection replay. Deferred to MVP-MOD-003-BE-002. */
    public void rebuildIndex(String tenantId) {
        requiredText(tenantId, "Tenant id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-001-RN-004",
                "Person search index rebuild is deferred to MVP-MOD-003-BE-002.");
    }

    /** BCM-PER-001 merge coordination cascade is deferred to MVP-MOD-003-BE-002. */
    public String initiateMergeCoordination(String tenantId, String sourceRecordId, String targetRecordId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(sourceRecordId, "Source record id is required.");
        requiredText(targetRecordId, "Target record id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-001-RN-004",
                "Person merge coordination is deferred to MVP-MOD-003-BE-002.");
    }

    // -- Helpers --------------------------------------------------------------------------

    private static PersonSearchEntry toEntry(Patient patient) {
        return new PersonSearchEntry(
                patient.tenantId(), patient.laboratoryId(), PersonKind.PATIENT, patient.patientId(),
                patient.patientCode(),
                patient.name() == null ? null : patient.name().fullNameDisplay(),
                patient.name() == null ? null
                        : com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation
                                .normalizeNaturalKeyToken(patient.name().familyName()),
                patient.name() == null ? null
                        : com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation
                                .normalizeNaturalKeyToken(patient.name().givenName()),
                patient.birthDate(),
                patient.primaryDocument() == null ? null : patient.primaryDocument().documentType(),
                patient.primaryDocument() == null ? null : patient.primaryDocument().maskedNumber(),
                patient.status());
    }

    private static PersonSearchEntry toEntry(Doctor doctor) {
        return new PersonSearchEntry(
                doctor.tenantId(), doctor.laboratoryId(), PersonKind.DOCTOR, doctor.doctorId(),
                doctor.doctorCode(),
                doctor.name() == null ? null : doctor.name().fullNameDisplay(),
                doctor.name() == null ? null
                        : com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation
                                .normalizeNaturalKeyToken(doctor.name().familyName()),
                doctor.name() == null ? null
                        : com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation
                                .normalizeNaturalKeyToken(doctor.name().givenName()),
                null,
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().documentType(),
                doctor.primaryDocument() == null ? null : doctor.primaryDocument().maskedNumber(),
                doctor.status());
    }

    private static double computeConfidence(PersonNaturalKey key, Patient patient) {
        return baseConfidenceScore(key, patient.birthDate(), primaryDoc(patient));
    }

    private static double computeConfidence(PersonNaturalKey key, Doctor doctor) {
        return baseConfidenceScore(key, null, primaryDoc(doctor));
    }

    private static double baseConfidenceScore(PersonNaturalKey key, java.time.LocalDate storedBirthDate,
            PersonDocument document) {
        // Simplified fixed-weight score used only for MVP compilation. The tenant-configurable
        // weighting is delivered as part of MVP-MOD-003-BE-002.
        double score = 0.5;
        if (storedBirthDate != null && key.birthDate() != null && storedBirthDate.equals(key.birthDate())) {
            score += 0.2;
        }
        if (document != null && key.nationalIdentifierHash() != null) {
            score += 0.2;
        }
        return Math.min(0.95, score);
    }

    private static PersonDocument primaryDoc(Patient patient) {
        return patient.primaryDocument();
    }

    private static PersonDocument primaryDoc(Doctor doctor) {
        return doctor.primaryDocument();
    }
}
