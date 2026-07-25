package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.optionalText;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.PatientDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientConsent;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientSnapshot;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonDocumentUniquenessPolicy;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonDuplicateDetectionEngine;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Compiles the generatable outputs of {@code bcm-per-002-patient-management/generation-plan.md}
 * (MVP-MOD-003-BE-001) and implements its custom rules (MVP-MOD-003-BE-002): RN-002 (duplicate
 * detection integration at registration), RN-005 (patient merge with snapshot archival and
 * downstream reference rewiring), RN-006 (representative active-window enforcement on revocation)
 * and RN-007 (append-only consent revocation history).
 */
@Service
public class PatientManagementService implements PatientDirectory {

    private static final int MAX_MERGE_CHAIN_HOPS = 10;

    private static final List<String> SEX_VALUES = List.of(
            Patient.SEX_FEMALE, Patient.SEX_MALE, Patient.SEX_UNKNOWN, Patient.SEX_NOT_DISCLOSED);

    private static final List<String> DOCUMENT_TYPES = List.of(
            PersonDocument.TYPE_NATIONAL_ID,
            PersonDocument.TYPE_PASSPORT,
            PersonDocument.TYPE_DRIVERS_LICENSE,
            PersonDocument.TYPE_TAX_ID,
            PersonDocument.TYPE_PROFESSIONAL_LICENSE,
            PersonDocument.TYPE_OTHER);

    private static final List<String> CONSENT_TYPES = List.of(
            PatientConsent.TYPE_DATA_PROCESSING,
            PatientConsent.TYPE_PORTAL_ACCESS,
            PatientConsent.TYPE_NOTIFICATION_CHANNEL,
            PatientConsent.TYPE_MARKETING,
            PatientConsent.TYPE_RESEARCH);

    private static final List<String> DOCUMENT_CATEGORIES = List.of(
            PatientDocument.CATEGORY_IDENTIFICATION,
            PatientDocument.CATEGORY_INSURANCE,
            PatientDocument.CATEGORY_AUTHORIZATION,
            PatientDocument.CATEGORY_MEDICAL_REPORT,
            PatientDocument.CATEGORY_OTHER);

    private final PatientRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final PersonDocumentUniquenessPolicy documentUniquenessPolicy;
    private final PersonDuplicateDetectionEngine duplicateDetectionEngine;
    private final Clock clock;

    @Autowired
    public PatientManagementService(
            PatientRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            PersonDocumentUniquenessPolicy documentUniquenessPolicy,
            PersonDuplicateDetectionEngine duplicateDetectionEngine) {
        this(repository, tenantDirectory, auditRecorder, documentUniquenessPolicy, duplicateDetectionEngine,
                Clock.systemUTC());
    }

    PatientManagementService(
            PatientRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            PersonDocumentUniquenessPolicy documentUniquenessPolicy,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.documentUniquenessPolicy = documentUniquenessPolicy;
        this.duplicateDetectionEngine = duplicateDetectionEngine;
        this.clock = clock;
    }

    // -- Patient aggregate commands and queries -------------------------------------------------

    public Patient register(RegisterPatientCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String patientCode = requiredText(command.patientCode(), "Patient code is required.");
        ValidatedPatientProfile profile = validateProfile(command);

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new PeopleEntityNotFoundException("Tenant was not found.");
        }
        // RN-001: Patient code must be unique within tenant.
        if (repository.existsByPatientCode(tenantId, patientCode, null)) {
            throw new PeopleConflictException("Patient code already exists in this tenant.");
        }
        // BCM-PER-001 RN-002: primary document number must be unique within tenant across Patient
        // and Doctor scopes.
        documentUniquenessPolicy.ensureUnique(tenantId, profile.primaryDocument().documentType(),
                profile.primaryDocument().documentNumber(), null, null);

        // BCM-PER-002 RN-002: duplicate detection must be invoked and its result recorded before a
        // Patient is registered. The aggregate command itself remains non-blocking; strict match
        // resolution is enforced by the higher-level BCM-ATT-002 registration orchestration
        // (commitPatientRegistration), which is the actor-facing intake flow.
        duplicateDetectionEngine.detect(tenantId, PersonKind.PATIENT, command.familyName(),
                command.givenName(), profile.birthDate(), profile.sexAtBirth(), null, true);

        Instant now = Instant.now(clock);
        Patient patient = new Patient(
                newId(), tenantId, laboratoryId, patientCode, profile.name(), profile.birthDate(),
                profile.sexAtBirth(), profile.primaryDocument(), profile.address(), profile.preferredLocale(),
                Patient.STATUS_ACTIVE, null, 1, now, now);
        Patient saved = repository.save(patient);
        auditRecorder.recordSystemEvent(tenantId, "PatientRegistered", "Patient", saved.patientId(),
                "{\"patientCode\":\"%s\"}".formatted(jsonText(saved.patientCode())));
        return saved;
    }

    public Patient update(String patientId, UpdatePatientCommand command) {
        Patient current = require(patientId);
        // RN-009: deceased is terminal.
        if (Patient.STATUS_DECEASED.equals(current.status())) {
            throw new PeopleConflictException("Deceased patient status cannot be reverted through update commands.");
        }
        if (Patient.STATUS_MERGED.equals(current.status())) {
            throw new PeopleConflictException("A merged patient must be updated through its surviving record.");
        }

        ValidatedPatientProfile profile = validateProfile(command);

        Patient updated = new Patient(
                current.patientId(), current.tenantId(), current.laboratoryId(), current.patientCode(),
                profile.name(), profile.birthDate(), profile.sexAtBirth(), profile.primaryDocument(),
                profile.address(), profile.preferredLocale(), current.status(), current.mergedIntoPatientId(),
                current.version() + 1, current.createdAt(), Instant.now(clock));
        Patient saved = repository.save(updated);
        // RN-004: publish delta as audit event; full-featured delta is deferred to BE-002.
        auditRecorder.recordSystemEvent(saved.tenantId(), "PatientUpdated", "Patient", saved.patientId(),
                "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    public Patient deactivate(String patientId) {
        Patient current = require(patientId);
        if (Patient.STATUS_DECEASED.equals(current.status())) {
            throw new PeopleConflictException("Deceased patient status cannot be changed.");
        }
        if (Patient.STATUS_MERGED.equals(current.status())) {
            throw new PeopleConflictException("A merged patient is already inactive.");
        }
        Patient deactivated = new Patient(
                current.patientId(), current.tenantId(), current.laboratoryId(), current.patientCode(),
                current.name(), current.birthDate(), current.sexAtBirth(), current.primaryDocument(),
                current.address(), current.preferredLocale(), Patient.STATUS_INACTIVE,
                current.mergedIntoPatientId(), current.version() + 1, current.createdAt(), Instant.now(clock));
        Patient saved = repository.save(deactivated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PatientDeactivated", "Patient", saved.patientId(), "{}");
        return saved;
    }

    /**
     * BCM-PER-002 RN-005: nominates a surviving patient id and archives the merged patient. The
     * merged (source) record is never deleted: its status becomes {@link Patient#STATUS_MERGED} and
     * {@code mergedIntoPatientId} points at the survivor, which both preserves the record for
     * historical references and makes the operation idempotent (a repeated merge into the same
     * survivor is a no-op). Downstream snapshot references are rewired transparently by
     * {@link #findSnapshot(String)}, which follows the merge chain to the surviving patient.
     */
    public Patient merge(String patientId, String survivingPatientId) {
        String sourceId = requiredText(patientId, "Patient id is required.");
        String targetId = requiredText(survivingPatientId, "Surviving patient id is required.");
        if (sourceId.equals(targetId)) {
            throw new InvalidPeopleCommandException("A patient cannot be merged into itself.");
        }
        Patient source = require(sourceId);
        Patient target = require(targetId);
        if (Patient.STATUS_MERGED.equals(target.status())) {
            throw new PeopleConflictException("Surviving patient id must not itself be a merged patient.");
        }

        if (Patient.STATUS_MERGED.equals(source.status())) {
            if (targetId.equals(source.mergedIntoPatientId())) {
                // RN-005 idempotent replay: already merged into the requested survivor.
                return source;
            }
            throw new PeopleConflictException(
                    "Patient is already merged into a different surviving patient (" + source.mergedIntoPatientId()
                            + ").");
        }

        Patient merged = new Patient(
                source.patientId(), source.tenantId(), source.laboratoryId(), source.patientCode(),
                source.name(), source.birthDate(), source.sexAtBirth(), source.primaryDocument(),
                source.address(), source.preferredLocale(), Patient.STATUS_MERGED, targetId,
                source.version() + 1, source.createdAt(), Instant.now(clock));
        Patient saved = repository.save(merged);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PatientMerged", "Patient", saved.patientId(),
                "{\"survivingPatientId\":\"%s\"}".formatted(jsonText(targetId)));
        return saved;
    }

    public Patient get(String patientId) {
        return require(patientId);
    }

    public PatientSnapshot snapshot(String patientId) {
        return PatientSnapshot.from(require(patientId));
    }

    public List<Patient> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    // -- Representative commands ---------------------------------------------------------------

    public PatientRepresentative attachRepresentative(String patientId, AttachPatientRepresentativeCommand command) {
        Patient patient = require(patientId);
        String relationship = requiredOneOf(command.relationship(), "Representative relationship is invalid.",
                "parent", "legal_guardian", "spouse", "adult_child", "other_authorized");
        PersonName repName = requiredName(command.givenName(), command.middleName(),
                command.familyName(), command.secondFamilyName(), null);
        PersonDocument document = requiredDocument(command.documentType(), command.documentNumber(),
                null, null, null);
        LocalDate from = requiredDate(command.authorizationFrom(), "Authorization start date is required.");
        LocalDate to = command.authorizationTo();
        if (to != null && to.isBefore(from)) {
            throw new InvalidPeopleCommandException("Authorization end date must be on or after start date.");
        }
        PatientRepresentative representative = new PatientRepresentative(
                newId(), patient.patientId(), relationship, repName, document, from, to,
                PatientRepresentative.STATUS_ACTIVE);
        repository.saveRepresentative(representative);
        auditRecorder.recordSystemEvent(patient.tenantId(), "PatientRepresentativeAttached",
                "PatientRepresentative", representative.representativeId(),
                "{\"patientId\":\"%s\"}".formatted(jsonText(patient.patientId())));
        return representative;
    }

    public List<PatientRepresentative> listRepresentatives(String patientId) {
        require(patientId);
        return repository.findRepresentatives(patientId);
    }

    /**
     * BCM-PER-002 RN-006: closes a representative's active authorization window. A representative
     * is only "honored" while {@link PatientRepresentative#status()} is
     * {@link PatientRepresentative#STATUS_ACTIVE} and today falls within the authorization range;
     * revocation closes that window explicitly (status becomes {@code revoked} and
     * {@code authorizationTo} is capped at today) so read paths never need to re-derive the decision.
     */
    public PatientRepresentative revokeRepresentative(String patientId, String representativeId) {
        Patient patient = require(patientId);
        String repId = requiredText(representativeId, "Representative id is required.");
        PatientRepresentative representative = repository.findRepresentativeById(repId)
                .orElseThrow(() -> new PeopleEntityNotFoundException("Patient representative was not found."));
        if (!patientId.equals(representative.patientId())) {
            throw new PeopleEntityNotFoundException("Patient representative was not found.");
        }
        if (PatientRepresentative.STATUS_REVOKED.equals(representative.status())) {
            throw new PeopleConflictException("Patient representative is already revoked.");
        }
        LocalDate today = LocalDate.now(clock);
        LocalDate closedAuthorizationTo = representative.authorizationTo() != null
                && representative.authorizationTo().isBefore(today)
                        ? representative.authorizationTo()
                        : today;
        PatientRepresentative revoked = new PatientRepresentative(
                representative.representativeId(), representative.patientId(), representative.relationship(),
                representative.representativeName(), representative.representativeDocument(),
                representative.authorizationFrom(), closedAuthorizationTo, PatientRepresentative.STATUS_REVOKED);
        repository.saveRepresentative(revoked);
        auditRecorder.recordSystemEvent(patient.tenantId(), "PatientRepresentativeRevoked",
                "PatientRepresentative", revoked.representativeId(),
                "{\"patientId\":\"%s\"}".formatted(jsonText(patientId)));
        return revoked;
    }

    // -- Consent commands ----------------------------------------------------------------------

    public PatientConsent recordConsent(String patientId, RecordPatientConsentCommand command) {
        Patient patient = require(patientId);
        String consentType = requiredOneOf(command.consentType(), "Consent type is invalid.",
                CONSENT_TYPES.toArray(String[]::new));
        String grantedBy = requiredOneOf(command.grantedBy(), "Consent grantedBy is invalid.",
                PatientConsent.GRANTED_BY_PATIENT, PatientConsent.GRANTED_BY_REPRESENTATIVE);
        PatientConsent consent = new PatientConsent(
                newId(), patient.patientId(), consentType, command.granted(), grantedBy,
                Instant.now(clock), null, optionalText(command.evidenceReference()));
        repository.saveConsent(consent);
        auditRecorder.recordSystemEvent(patient.tenantId(), "PatientConsentRecorded", "PatientConsent",
                consent.consentId(), "{\"patientId\":\"%s\",\"consentType\":\"%s\"}"
                        .formatted(jsonText(patient.patientId()), jsonText(consent.consentType())));
        return consent;
    }

    public List<PatientConsent> listConsents(String patientId) {
        require(patientId);
        return repository.findConsents(patientId);
    }

    /**
     * BCM-PER-002 RN-007: a consent may be revoked but existing consent evidence must remain
     * immutable. The JDBC adapter for {@code people.patient_consents} only supports insert (no
     * update), by design: revocation always appends a new consent record (granted=false,
     * evidence referencing the original consent id) rather than mutating the original row. The
     * original grant evidence is never altered.
     */
    public PatientConsent revokeConsent(String patientId, String consentId) {
        Patient patient = require(patientId);
        String originalId = requiredText(consentId, "Consent id is required.");
        PatientConsent original = repository.findConsentById(originalId)
                .orElseThrow(() -> new PeopleEntityNotFoundException("Patient consent was not found."));
        if (!patientId.equals(original.patientId())) {
            throw new PeopleEntityNotFoundException("Patient consent was not found.");
        }
        boolean alreadyRevoked = repository.findConsents(patientId).stream()
                .anyMatch(consent -> originalId.equals(consent.evidenceReference()));
        if (alreadyRevoked) {
            throw new PeopleConflictException("Patient consent is already revoked.");
        }

        Instant now = Instant.now(clock);
        PatientConsent revocation = new PatientConsent(
                newId(), patient.patientId(), original.consentType(), false, original.grantedBy(),
                now, now, originalId);
        repository.saveConsent(revocation);
        auditRecorder.recordSystemEvent(patient.tenantId(), "PatientConsentRevoked", "PatientConsent",
                revocation.consentId(), "{\"patientId\":\"%s\",\"revokesConsentId\":\"%s\"}"
                        .formatted(jsonText(patient.patientId()), jsonText(originalId)));
        return revocation;
    }

    // -- Document commands ---------------------------------------------------------------------

    public PatientDocument attachDocument(String patientId, AttachPatientDocumentCommand command) {
        Patient patient = require(patientId);
        String category = requiredOneOf(command.category(), "Document category is invalid.",
                DOCUMENT_CATEGORIES.toArray(String[]::new));
        String fileReference = requiredText(command.fileReference(), "Document file reference is required.");
        PatientDocument document = new PatientDocument(
                newId(), patient.patientId(), category, fileReference, Instant.now(clock),
                command.expiresAt());
        repository.saveDocument(document);
        return document;
    }

    public List<PatientDocument> listDocuments(String patientId) {
        require(patientId);
        return repository.findDocuments(patientId);
    }

    public void removeDocument(String patientId, String documentId) {
        require(patientId);
        repository.deleteDocument(requiredText(documentId, "Document id is required."));
    }

    // -- PatientDirectory implementation -------------------------------------------------------

    /**
     * BCM-PER-002 RN-005: downstream snapshot references are rewired transparently across a merge.
     * A merged patient's snapshot resolves to its surviving patient (following the merge chain, in
     * case of a chain of merges) so consumers never need to know a record was merged away.
     */
    @Override
    public Optional<PatientSnapshot> findSnapshot(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return Optional.empty();
        }
        Optional<Patient> current = repository.findById(patientId);
        int hops = 0;
        while (current.isPresent() && Patient.STATUS_MERGED.equals(current.get().status())
                && current.get().mergedIntoPatientId() != null && hops < MAX_MERGE_CHAIN_HOPS) {
            current = repository.findById(current.get().mergedIntoPatientId());
            hops++;
        }
        return current.map(PatientSnapshot::from);
    }

    @Override
    public boolean patientExists(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return false;
        }
        return repository.findById(patientId).isPresent();
    }

    // -- Internal helpers ----------------------------------------------------------------------

    private Patient require(String patientId) {
        return repository.findById(requiredText(patientId, "Patient id is required."))
                .orElseThrow(() -> new PeopleEntityNotFoundException("Patient was not found."));
    }

    private static ValidatedPatientProfile validateProfile(PatientProfileFields fields) {
        PersonName name = requiredName(fields.givenName(), fields.middleName(), fields.familyName(),
                fields.secondFamilyName(), fields.preferredName());
        LocalDate dateOfBirth = requiredDate(fields.birthDate(), "Birth date is required.");
        String biologicalSex = requiredOneOf(fields.sexAtBirth(), "Sex at birth is invalid.", SEX_VALUES.toArray(String[]::new));
        PersonDocument primaryDocument = requiredDocument(fields.primaryDocumentType(), fields.primaryDocumentNumber(),
                fields.primaryDocumentIssuingCountry(), fields.primaryDocumentIssuedAt(), fields.primaryDocumentExpiresAt());
        PersonAddress address = optionalAddress(fields.addressCountry(), fields.addressState(), fields.addressCity(),
                fields.addressPostalCode(), fields.addressStreet());
        return new ValidatedPatientProfile(name, dateOfBirth, biologicalSex, primaryDocument, address,
                optionalText(fields.preferredLocale()));
    }

    private static PersonName requiredName(String given, String middle, String family, String secondFamily,
            String preferred) {
        String givenTrimmed = requiredText(given, "Given name is required.");
        String familyTrimmed = requiredText(family, "Family name is required.");
        return new PersonName(givenTrimmed, optionalText(middle), familyTrimmed,
                optionalText(secondFamily), optionalText(preferred));
    }

    private static PersonDocument requiredDocument(String type, String number, String country,
            LocalDate issuedAt, LocalDate expiresAt) {
        String docType = requiredOneOf(type, "Document type is invalid.",
                DOCUMENT_TYPES.toArray(String[]::new));
        String docNumber = requiredText(number, "Document number is required.");
        return new PersonDocument(docType, docNumber, optionalText(country), issuedAt, expiresAt);
    }

    private static PersonAddress optionalAddress(String country, String state, String city,
            String postalCode, String street) {
        if (country == null && state == null && city == null && postalCode == null && street == null) {
            return null;
        }
        return new PersonAddress(
                optionalText(country), optionalText(state), optionalText(city), null,
                optionalText(postalCode), optionalText(street), null, null);
    }

    private static LocalDate requiredDate(LocalDate value, String message) {
        if (value == null) {
            throw new InvalidPeopleCommandException(message);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private record ValidatedPatientProfile(
            PersonName name,
            LocalDate birthDate,
            String sexAtBirth,
            PersonDocument primaryDocument,
            PersonAddress address,
            String preferredLocale) {
    }

    // -- Cross-service helpers used by other capabilities (e.g., patient registration) --------

    /**
     * Broad normalized-natural-key lookup used by person search and by
     * {@link PersonDuplicateDetectionEngine} (through {@link PatientRepository} directly) for
     * weighted-confidence duplicate detection (BCM-PER-001 RN-003).
     */
    public List<Patient> searchByNaturalKey(String tenantId, String normalizedFamilyName,
            String normalizedGivenName, LocalDate birthDate) {
        return repository.searchByNaturalKey(
                requiredText(tenantId, "Tenant id is required."),
                normalizedFamilyName, normalizedGivenName, birthDate);
    }

    /** Optional query used by controllers. Returns empty list when key data is missing. */
    public List<Patient> findByLaboratoryId(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    /** Used by orchestration to satisfy consumers that want a snapshot without depending on Patient. */
    public Optional<Patient> findRawById(String patientId) {
        return repository.findById(patientId);
    }

}
