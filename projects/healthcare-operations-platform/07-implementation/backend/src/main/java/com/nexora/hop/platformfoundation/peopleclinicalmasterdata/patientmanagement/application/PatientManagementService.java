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
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonAddress;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonDocument;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PersonName;

/**
 * Compiles the generatable outputs of {@code bcm-per-002-patient-management/generation-plan.yaml}
 * (MVP-MOD-003-BE-001). The custom rules listed in
 * {@code business-rules.yaml.enforcement_summary.custom_implementation_rules} — RN-002 (duplicate
 * detection integration), RN-005 (patient merge), RN-006 (representative active window) and RN-007
 * (append-only consent history) — are deferred to MVP-MOD-003-BE-002 and surface as explicit
 * {@link PeopleCustomRuleNotImplementedException} hooks so downstream work has a discoverable
 * plug-in point.
 */
@Service
public class PatientManagementService implements PatientDirectory {

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
    private final Clock clock;

    @Autowired
    public PatientManagementService(
            PatientRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    PatientManagementService(
            PatientRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    // -- Patient aggregate commands and queries -------------------------------------------------

    public Patient register(RegisterPatientCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String patientCode = requiredText(command.patientCode(), "Patient code is required.");
        PersonName name = requiredName(command.givenName(), command.middleName(),
                command.familyName(), command.secondFamilyName(), command.preferredName());
        LocalDate birthDate = requiredDate(command.birthDate(), "Birth date is required.");
        String sexAtBirth = requiredOneOf(command.sexAtBirth(), "Sex at birth is invalid.",
                SEX_VALUES.toArray(String[]::new));
        PersonDocument primaryDocument = requiredDocument(command.primaryDocumentType(),
                command.primaryDocumentNumber(), command.primaryDocumentIssuingCountry(),
                command.primaryDocumentIssuedAt(), command.primaryDocumentExpiresAt());

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new PeopleEntityNotFoundException("Tenant was not found.");
        }
        // RN-001: Patient code must be unique within tenant.
        if (repository.existsByPatientCode(tenantId, patientCode, null)) {
            throw new PeopleConflictException("Patient code already exists in this tenant.");
        }

        PersonAddress address = optionalAddress(command.addressCountry(), command.addressState(),
                command.addressCity(), command.addressPostalCode(), command.addressStreet());
        Instant now = Instant.now(clock);
        Patient patient = new Patient(
                newId(), tenantId, laboratoryId, patientCode, name, birthDate, sexAtBirth,
                primaryDocument, address, optionalText(command.preferredLocale()),
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

        PersonName name = requiredName(command.givenName(), command.middleName(),
                command.familyName(), command.secondFamilyName(), command.preferredName());
        LocalDate birthDate = requiredDate(command.birthDate(), "Birth date is required.");
        String sexAtBirth = requiredOneOf(command.sexAtBirth(), "Sex at birth is invalid.",
                SEX_VALUES.toArray(String[]::new));
        PersonDocument primaryDocument = requiredDocument(command.primaryDocumentType(),
                command.primaryDocumentNumber(), command.primaryDocumentIssuingCountry(),
                command.primaryDocumentIssuedAt(), command.primaryDocumentExpiresAt());
        PersonAddress address = optionalAddress(command.addressCountry(), command.addressState(),
                command.addressCity(), command.addressPostalCode(), command.addressStreet());

        Patient updated = new Patient(
                current.patientId(), current.tenantId(), current.laboratoryId(), current.patientCode(),
                name, birthDate, sexAtBirth, primaryDocument, address,
                optionalText(command.preferredLocale()), current.status(), current.mergedIntoPatientId(),
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
     * Merge cascade rewires downstream references and archives the source snapshot. Deferred to
     * MVP-MOD-003-BE-002 (BCM-PER-002 RN-005). Validates only the request shape here so the deferred
     * hook is discoverable without depending on prior aggregate state.
     */
    public Patient merge(String patientId, String survivingPatientId) {
        requiredText(patientId, "Patient id is required.");
        requiredText(survivingPatientId, "Surviving patient id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-002-RN-005",
                "Patient merge cascade is deferred to MVP-MOD-003-BE-002.");
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

    public PatientRepresentative revokeRepresentative(String patientId, String representativeId) {
        require(patientId);
        require(requiredText(representativeId, "Representative id is required.") != null ? patientId : patientId);
        // RN-007 append-only history and RN-006 active-window enforcement are deferred to BE-002.
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-002-RN-006",
                "Representative revocation with append-only history is deferred to MVP-MOD-003-BE-002.");
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

    public PatientConsent revokeConsent(String patientId, String consentId) {
        require(patientId);
        requiredText(consentId, "Consent id is required.");
        throw new PeopleCustomRuleNotImplementedException("BCM-PER-002-RN-007",
                "Consent revocation with immutable evidence history is deferred to MVP-MOD-003-BE-002.");
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

    @Override
    public Optional<PatientSnapshot> findSnapshot(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return Optional.empty();
        }
        return repository.findById(patientId).map(PatientSnapshot::from);
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

    // -- Cross-service helpers used by other capabilities (e.g., patient registration) --------

    /**
     * Used by BCM-ATT-002 to check duplicates using a simplified normalized-natural-key match. The
     * advanced weighted-confidence duplicate detection required by BCM-PER-001 RN-003 remains
     * deferred to MVP-MOD-003-BE-002.
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
