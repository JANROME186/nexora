package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonDuplicateCandidate;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonNaturalKey;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

/**
 * Implements BCM-PER-001 RN-001 (normalized natural-key matching), RN-003 (tenant-configurable
 * weighted confidence scoring) and RN-007 (one-way hashing of national identifier values). This
 * engine depends only on the {@link PatientRepository} and {@link DoctorRepository} ports (not on
 * {@code PatientManagementService}/{@code DoctorManagementService}) so it can be safely reused by
 * both aggregate services and by the BCM-ATT-002 patient-registration orchestration without
 * creating a circular application-service dependency.
 */
@Component
public class PersonDuplicateDetectionEngine {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AuditRecorder auditRecorder;
    private final TenantPeoplePolicyStore policyStore;

    public PersonDuplicateDetectionEngine(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AuditRecorder auditRecorder,
            TenantPeoplePolicyStore policyStore) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.auditRecorder = auditRecorder;
        this.policyStore = policyStore;
    }

    /**
     * Detects duplicate candidates for the given natural-key attributes. Recall is intentionally
     * broad (matched by normalized family name only at the repository layer); precision is then
     * expressed as a weighted confidence score per candidate so callers can apply their own
     * threshold policy (see {@link #hasHighConfidenceMatch(List, String)}).
     */
    public List<PersonDuplicateCandidate> detect(
            String tenantId,
            String personKindFilter,
            String familyName,
            String givenName,
            LocalDate birthDate,
            String sexAtBirth,
            String nationalIdentifier,
            boolean recordAuditEvent) {
        PersonNaturalKey key = PersonNaturalKey.normalize(familyName, givenName, birthDate, sexAtBirth,
                nationalIdentifier);
        PersonDuplicateScoringPolicy policy = policyStore.scoringPolicyFor(tenantId);

        List<PersonDuplicateCandidate> candidates = new ArrayList<>();
        if (personKindFilter == null || PersonKind.PATIENT.equals(personKindFilter)) {
            patientRepository.searchByNaturalKey(tenantId, key.normalizedFamilyName(), null, null)
                    .forEach(patient -> candidates.add(score(key, policy, patient)));
        }
        if (personKindFilter == null || PersonKind.DOCTOR.equals(personKindFilter)) {
            doctorRepository.searchByNaturalKey(tenantId, key.normalizedFamilyName(), null, null)
                    .forEach(doctor -> candidates.add(score(key, policy, doctor)));
        }

        if (recordAuditEvent) {
            auditRecorder.recordSystemEvent(tenantId, "PersonDuplicateDetectionRequested",
                    "PersonDuplicateDetection", UUID.randomUUID().toString(),
                    "{\"candidateCount\":%d}".formatted(candidates.size()));
        }
        return candidates;
    }

    /** Highest confidence found among candidates of the given kind (or any kind when null). */
    public double highestConfidence(List<PersonDuplicateCandidate> candidates, String personKindFilter) {
        return candidates.stream()
                .filter(candidate -> personKindFilter == null || personKindFilter.equals(candidate.personKind()))
                .mapToDouble(PersonDuplicateCandidate::confidence)
                .max()
                .orElse(0.0);
    }

    /** BCM-ATT-002 RN-006: whether any candidate of the given kind meets the tenant's threshold. */
    public boolean hasHighConfidenceMatch(String tenantId, List<PersonDuplicateCandidate> candidates,
            String personKindFilter) {
        double threshold = policyStore.scoringPolicyFor(tenantId).highConfidenceThreshold();
        return highestConfidence(candidates, personKindFilter) >= threshold;
    }

    // -- Scoring ---------------------------------------------------------------------------

    private static PersonDuplicateCandidate score(PersonNaturalKey key, PersonDuplicateScoringPolicy policy,
            Patient patient) {
        String storedGivenName = patient.name() == null ? null
                : PeopleValidation.normalizeNaturalKeyToken(patient.name().givenName());
        double score = weightedScore(key, policy, storedGivenName, patient.birthDate(), patient.sexAtBirth(),
                patient.primaryDocument());
        return new PersonDuplicateCandidate(PersonKind.PATIENT, patient.patientId(),
                patient.name() == null ? null : patient.name().fullNameDisplay(), score,
                "weighted_natural_key_match");
    }

    private static PersonDuplicateCandidate score(PersonNaturalKey key, PersonDuplicateScoringPolicy policy,
            Doctor doctor) {
        // Doctor does not carry a birth date or sex-at-birth in the current business model, so
        // those weights simply never contribute for doctor candidates.
        String storedGivenName = doctor.name() == null ? null
                : PeopleValidation.normalizeNaturalKeyToken(doctor.name().givenName());
        double score = weightedScore(key, policy, storedGivenName, null, null, doctor.primaryDocument());
        return new PersonDuplicateCandidate(PersonKind.DOCTOR, doctor.doctorId(),
                doctor.name() == null ? null : doctor.name().fullNameDisplay(), score,
                "weighted_natural_key_match");
    }

    private static double weightedScore(PersonNaturalKey key, PersonDuplicateScoringPolicy policy,
            String storedNormalizedGivenName, LocalDate storedBirthDate, String storedSexAtBirth,
            PersonDocument storedDocument) {
        // Every candidate returned by the repository already matched the normalized family name,
        // so that weight always applies.
        double score = policy.familyNameWeight();
        if (key.normalizedGivenName() != null
                && key.normalizedGivenName().equals(storedNormalizedGivenName)) {
            score += policy.givenNameWeight();
        }
        if (storedBirthDate != null && key.birthDate() != null && storedBirthDate.equals(key.birthDate())) {
            score += policy.birthDateWeight();
        }
        if (storedSexAtBirth != null && key.sexAtBirth() != null && storedSexAtBirth.equals(key.sexAtBirth())) {
            score += policy.sexAtBirthWeight();
        }
        if (storedDocument != null && key.nationalIdentifierHash() != null
                && Objects.equals(key.nationalIdentifierHash(),
                        PersonNaturalKey.hashIdentifier(storedDocument.documentNumber()))) {
            score += policy.nationalIdentifierWeight();
        }
        return Math.min(0.99, score);
    }
}
