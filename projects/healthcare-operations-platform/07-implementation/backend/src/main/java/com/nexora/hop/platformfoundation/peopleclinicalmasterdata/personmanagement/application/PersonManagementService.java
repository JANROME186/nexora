package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.application.DoctorManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.Doctor;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonDuplicateCandidate;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordination;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonMergeCoordinationRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonNaturalKey;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonSearchEntry;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;

/**
 * Compiles the generatable outputs of BCM-PER-001 (Person Management) and, as of MVP-MOD-003-BE-002,
 * implements its custom rules. Because Person is not an owning aggregate, this service projects
 * Patient (BCM-PER-002) and Doctor (BCM-PER-003) into a unified search view (RM-PER-001).
 * Duplicate-detection scoring (RN-003) is delegated to {@link PersonDuplicateDetectionEngine} so it
 * can be reused by the aggregate services and the patient-registration orchestration without a
 * circular dependency on this service.
 */
@Service
public class PersonManagementService {

    private static final List<String> PERSON_KINDS = List.of(PersonKind.PATIENT, PersonKind.DOCTOR);

    private final PatientManagementService patientManagementService;
    private final DoctorManagementService doctorManagementService;
    private final PersonDuplicateDetectionEngine duplicateDetectionEngine;
    private final PersonMergeCoordinationRepository mergeCoordinationRepository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PersonManagementService(
            PatientManagementService patientManagementService,
            DoctorManagementService doctorManagementService,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            PersonMergeCoordinationRepository mergeCoordinationRepository,
            AuditRecorder auditRecorder) {
        this(patientManagementService, doctorManagementService, duplicateDetectionEngine,
                mergeCoordinationRepository, auditRecorder, Clock.systemUTC());
    }

    PersonManagementService(
            PatientManagementService patientManagementService,
            DoctorManagementService doctorManagementService,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            PersonMergeCoordinationRepository mergeCoordinationRepository,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.patientManagementService = patientManagementService;
        this.doctorManagementService = doctorManagementService;
        this.duplicateDetectionEngine = duplicateDetectionEngine;
        this.mergeCoordinationRepository = mergeCoordinationRepository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
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
     * BCM-PER-001 RN-001/RN-003/RN-007: delegates natural-key matching and tenant-configurable
     * weighted confidence scoring to {@link PersonDuplicateDetectionEngine}, which normalizes and
     * one-way-hashes identifier values before comparison.
     */
    public List<PersonDuplicateCandidate> detectDuplicates(DetectPersonDuplicatesCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String requestedKind = command.personKind() == null ? null
                : requiredOneOf(command.personKind(), "Person kind is invalid.",
                        PERSON_KINDS.toArray(String[]::new));

        return duplicateDetectionEngine.detect(tenantId, requestedKind, command.familyName(),
                command.givenName(), command.birthDate(), command.sexAtBirth(),
                command.nationalIdentifier(), true);
    }

    /**
     * BCM-PER-001 RN-004: {@code PersonSearchIndex} is a virtual, on-demand projection over the
     * Patient and Doctor aggregates (see {@link #search(SearchPersonsQuery)}) rather than a
     * separately persisted read model, so there is no event-sourced index to replay. "Rebuilding"
     * is therefore an idempotent confirmation that the live projection is consistent with the
     * current aggregate counts; it always succeeds and never diverges from the aggregates because
     * every query recomputes the projection directly from them.
     */
    public PersonSearchIndexRebuildResult rebuildIndex(String tenantId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        int patientCount = patientManagementService.searchByNaturalKey(tenant, null, null, null).size();
        int doctorCount = doctorManagementService.searchByNaturalKey(tenant, null, null, null).size();
        auditRecorder.recordSystemEvent(tenant, "PersonSearchIndexRebuilt", "PersonSearchIndex", tenant,
                "{\"patientCount\":%d,\"doctorCount\":%d}".formatted(patientCount, doctorCount));
        return new PersonSearchIndexRebuildResult(tenant, patientCount, doctorCount, Instant.now(clock));
    }

    /**
     * BCM-PER-001 custom implementation point CUS-PER-001-05: coordinates a cross-context merge
     * decision. When both records are patients, the coordination triggers the BCM-PER-002
     * {@code mergePatient} aggregate command (RN-005); Doctor has no merge concept in the current
     * business model, so any coordination involving a doctor record is recorded as a decision only.
     */
    public PersonMergeCoordination initiateMergeCoordination(String tenantId, String sourceRecordId,
            String targetRecordId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        String source = requiredText(sourceRecordId, "Source record id is required.");
        String target = requiredText(targetRecordId, "Target record id is required.");
        if (source.equals(target)) {
            throw new InvalidPeopleCommandException("Source and target record ids must be different.");
        }

        String sourceKind = resolveKind(source);
        String targetKind = resolveKind(target);

        boolean bothPatients = PersonKind.PATIENT.equals(sourceKind) && PersonKind.PATIENT.equals(targetKind);
        String status;
        boolean patientMergeApplied = false;
        if (bothPatients) {
            patientManagementService.merge(source, target);
            status = PersonMergeCoordination.STATUS_PATIENTS_MERGED;
            patientMergeApplied = true;
        } else {
            status = PersonMergeCoordination.STATUS_RECORDED_NO_AGGREGATE_OPERATION;
        }

        Instant now = Instant.now(clock);
        PersonMergeCoordination coordination = new PersonMergeCoordination(
                UUID.randomUUID().toString(), tenant, sourceKind, source, targetKind, target, status,
                patientMergeApplied, now, now);
        PersonMergeCoordination saved = mergeCoordinationRepository.save(coordination);
        auditRecorder.recordSystemEvent(tenant, "PersonMergeCoordinationInitiated", "PersonMergeCoordination",
                saved.coordinationId(),
                "{\"sourceKind\":\"%s\",\"targetKind\":\"%s\",\"status\":\"%s\"}"
                        .formatted(sourceKind, targetKind, status));
        return saved;
    }

    public PersonMergeCoordination getMergeCoordination(String coordinationId) {
        return mergeCoordinationRepository.findById(
                requiredText(coordinationId, "Coordination id is required."))
                .orElseThrow(() -> new PeopleEntityNotFoundException("Person merge coordination was not found."));
    }

    private String resolveKind(String recordId) {
        Optional<Patient> patient = patientManagementService.findRawById(recordId);
        if (patient.isPresent()) {
            return PersonKind.PATIENT;
        }
        if (doctorManagementService.doctorExists(recordId)) {
            return PersonKind.DOCTOR;
        }
        throw new PeopleEntityNotFoundException("Record id does not match an existing patient or doctor.");
    }

    /** Result of a {@link #rebuildIndex(String)} confirmation. */
    public record PersonSearchIndexRebuildResult(
            String tenantId, int patientCount, int doctorCount, Instant rebuiltAt) {
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
}
