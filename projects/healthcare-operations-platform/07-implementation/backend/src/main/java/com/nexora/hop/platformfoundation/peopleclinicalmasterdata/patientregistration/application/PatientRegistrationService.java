package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.optionalText;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.AttachPatientRepresentativeCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.RecordPatientConsentCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.RegisterPatientCommand;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.Patient;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.PersonDuplicateDetectionEngine;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.application.TenantPeoplePolicyStore;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonDuplicateCandidate;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.personmanagement.domain.PersonKind;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRequest;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.InvalidPeopleCommandException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

/**
 * Compiles the generatable outputs of BCM-ATT-002 (Patient Registration) and implements its
 * custom rules (MVP-MOD-003-BE-002): RN-001/RN-006 (duplicate-detection consultation and
 * high-confidence match resolution), RN-002 (commit only through BCM-PER-002 aggregate commands,
 * preserving AGG-001 Patient ownership), RN-003 (atomic representative attach), RN-005 (tenant
 * mandatory-consent enforcement) and RN-008 (age-of-majority default to representative
 * registration). This service never persists Patient state directly; every mutation of the
 * Patient aggregate goes through {@link PatientManagementService}.
 */
@Service
public class PatientRegistrationService {

    private static final List<String> INTAKE_CHANNELS = List.of(
            PatientRegistrationRequest.INTAKE_WALK_IN,
            PatientRegistrationRequest.INTAKE_APPOINTMENT,
            PatientRegistrationRequest.INTAKE_PORTAL_HANDOFF,
            PatientRegistrationRequest.INTAKE_MIGRATION_IMPORT);

    private static final List<String> REGISTRATION_KINDS = List.of(
            PatientRegistrationRequest.KIND_NEW_PATIENT,
            PatientRegistrationRequest.KIND_EXISTING_PATIENT_CONFIRMATION,
            PatientRegistrationRequest.KIND_REPRESENTATIVE_REGISTRATION);

    private final PatientRegistrationRepository repository;
    private final PatientManagementService patientManagementService;
    private final PersonDuplicateDetectionEngine duplicateDetectionEngine;
    private final TenantPeoplePolicyStore policyStore;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PatientRegistrationService(
            PatientRegistrationRepository repository,
            PatientManagementService patientManagementService,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            TenantPeoplePolicyStore policyStore,
            AuditRecorder auditRecorder) {
        this(repository, patientManagementService, duplicateDetectionEngine, policyStore, auditRecorder,
                Clock.systemUTC());
    }

    PatientRegistrationService(
            PatientRegistrationRepository repository,
            PatientManagementService patientManagementService,
            PersonDuplicateDetectionEngine duplicateDetectionEngine,
            TenantPeoplePolicyStore policyStore,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.patientManagementService = patientManagementService;
        this.duplicateDetectionEngine = duplicateDetectionEngine;
        this.policyStore = policyStore;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public PatientRegistrationRequest start(StartPatientRegistrationCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String intakeChannel = requiredOneOf(command.intakeChannel(), "Intake channel is invalid.",
                INTAKE_CHANNELS.toArray(String[]::new));
        String requestedKind = requiredOneOf(command.registrationKind(), "Registration kind is invalid.",
                REGISTRATION_KINDS.toArray(String[]::new));
        String givenName = requiredText(command.givenName(), "Given name is required.");
        String familyName = requiredText(command.familyName(), "Family name is required.");
        String documentType = requiredText(command.documentType(), "Document type is required.");
        String documentNumber = requiredText(command.documentNumber(), "Document number is required.");

        // BCM-ATT-002 RN-008: a minor patient registration must default to representative
        // registration when the tenant's age-of-majority policy applies. The default only kicks in
        // for a plain new-patient intake; an actor who already selected existing-patient
        // confirmation or representative registration explicitly keeps that choice.
        String registrationKind = requestedKind;
        boolean ageOfMajorityDefaultApplied = false;
        if (PatientRegistrationRequest.KIND_NEW_PATIENT.equals(requestedKind) && command.birthDate() != null) {
            int ageOfMajority = policyStore.ageOfMajorityYearsFor(tenantId);
            int age = Period.between(command.birthDate(), LocalDate.now(clock)).getYears();
            if (age < ageOfMajority) {
                registrationKind = PatientRegistrationRequest.KIND_REPRESENTATIVE_REGISTRATION;
                ageOfMajorityDefaultApplied = true;
            }
        }

        Instant now = Instant.now(clock);
        PatientRegistrationRequest registration = new PatientRegistrationRequest(
                newId(), tenantId, laboratoryId, branchId, intakeChannel, null, registrationKind,
                PeopleValidation.normalizeNaturalKeyToken(familyName),
                PeopleValidation.normalizeNaturalKeyToken(givenName),
                command.birthDate(), givenName, familyName, documentType, documentNumber,
                optionalText(command.draftPatientCode()),
                PatientRegistrationRequest.OUTCOME_PENDING, null,
                optionalText(command.actorId()), now, now);
        PatientRegistrationRequest saved = repository.save(registration);
        auditRecorder.recordSystemEvent(tenantId, "PatientRegistrationStarted",
                "PatientRegistrationRequest", saved.registrationRequestId(),
                "{\"branchId\":\"%s\",\"intakeChannel\":\"%s\",\"ageOfMajorityDefaultApplied\":%b}"
                        .formatted(jsonText(branchId), jsonText(intakeChannel), ageOfMajorityDefaultApplied));
        return saved;
    }

    public PatientRegistrationRequest cancel(String registrationRequestId, String reasonCode) {
        PatientRegistrationRequest current = require(registrationRequestId);
        if (!PatientRegistrationRequest.OUTCOME_PENDING.equals(current.outcome())) {
            throw new PeopleConflictException("Only pending registration requests can be cancelled.");
        }
        PatientRegistrationRequest cancelled = new PatientRegistrationRequest(
                current.registrationRequestId(), current.tenantId(), current.laboratoryId(),
                current.branchId(), current.intakeChannel(), current.candidatePatientId(),
                current.registrationKind(), current.normalizedFamilyName(), current.normalizedGivenName(),
                current.birthDate(), current.draftGivenName(), current.draftFamilyName(),
                current.draftDocumentType(), current.draftDocumentNumber(), current.draftPatientCode(),
                PatientRegistrationRequest.OUTCOME_CANCELLED, null, current.actorId(),
                current.createdAt(), Instant.now(clock));
        PatientRegistrationRequest saved = repository.save(cancelled);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PatientRegistrationCancelled",
                "PatientRegistrationRequest", saved.registrationRequestId(),
                "{\"reasonCode\":\"%s\"}".formatted(jsonText(reasonCode == null ? "unspecified" : reasonCode)));
        return saved;
    }

    /**
     * BCM-ATT-002 RN-001/RN-002/RN-003/RN-005/RN-006: consults duplicate detection, applies match
     * resolution, and commits only through BCM-PER-002 aggregate commands
     * ({@link PatientManagementService}) so this orchestration never owns or persists Patient state
     * directly (AGG-001 ownership stays with patient-management).
     * <p>
     * <b>Known boundary:</b> the patient creation, representative attach and consent capture below
     * are not wrapped in a database transaction (no {@code PlatformTransactionManager} is
     * guaranteed to be available in the in-memory profile used by most tests). A failure after the
     * patient is created but before the registration record is updated leaves an orphaned but
     * valid Patient and a still-pending registration that can be retried with
     * {@code resolvedExistingPatientId} pointing at the created patient. See technical debt
     * TD-BE-006.
     */
    public PatientRegistrationRequest commit(String registrationRequestId, CommitPatientRegistrationCommand command) {
        PatientRegistrationRequest registration = require(registrationRequestId);
        if (!PatientRegistrationRequest.OUTCOME_PENDING.equals(registration.outcome())) {
            throw new PeopleConflictException("Only pending registration requests can be committed.");
        }
        if (command == null) {
            throw new InvalidPeopleCommandException("Commit command is required.");
        }

        List<PersonDuplicateCandidate> candidates = duplicateDetectionEngine.detect(
                registration.tenantId(), PersonKind.PATIENT, registration.draftFamilyName(),
                registration.draftGivenName(), registration.birthDate(), null,
                registration.draftDocumentNumber(), true);
        boolean highConfidenceMatch = duplicateDetectionEngine.hasHighConfidenceMatch(
                registration.tenantId(), candidates, PersonKind.PATIENT);

        String resolvedExistingPatientId = optionalText(command.resolvedExistingPatientId());
        if (highConfidenceMatch && resolvedExistingPatientId == null) {
            // RN-006: cannot commit on a high-confidence match without an explicit actor decision.
            throw new PeopleConflictException(
                    "REGISTRATION_MATCH_RESOLUTION_REQUIRED: a high-confidence duplicate candidate exists; "
                            + "resubmit with resolvedExistingPatientId to reuse the existing record or confirm "
                            + "a new patient is intended.");
        }

        boolean representativeRegistration = PatientRegistrationRequest.KIND_REPRESENTATIVE_REGISTRATION
                .equals(registration.registrationKind());
        if (representativeRegistration && (optionalText(command.representativeGivenName()) == null
                || optionalText(command.representativeFamilyName()) == null
                || optionalText(command.representativeRelationship()) == null)) {
            // RN-003: a representative registration must attach a representative during commit.
            throw new PeopleConflictException(
                    "REGISTRATION_REPRESENTATIVE_REQUIRED: representative details are required to commit a "
                            + "representative registration.");
        }

        String outcomePatientId;
        if (resolvedExistingPatientId != null) {
            // Reuse an existing patient; RN-002 is honored because no Patient fields are persisted
            // here, only a read through PatientManagementService.
            patientManagementService.get(resolvedExistingPatientId);
            outcomePatientId = resolvedExistingPatientId;
        } else {
            String patientCode = requiredText(command.patientCode(),
                    "Patient code is required to commit a new patient registration.");
            String sexAtBirth = requiredText(command.sexAtBirth(),
                    "Sex at birth is required to commit a new patient registration.");
            Patient created = patientManagementService.register(new RegisterPatientCommand(
                    registration.tenantId(), registration.laboratoryId(), patientCode,
                    registration.draftGivenName(), null, registration.draftFamilyName(), null, null,
                    registration.birthDate(), sexAtBirth, registration.draftDocumentType(),
                    registration.draftDocumentNumber(), null, null, null,
                    command.addressCountry(), command.addressState(), command.addressCity(),
                    command.addressPostalCode(), command.addressStreet(), command.preferredLocale()));
            outcomePatientId = created.patientId();
        }

        if (representativeRegistration) {
            patientManagementService.attachRepresentative(outcomePatientId, new AttachPatientRepresentativeCommand(
                    command.representativeRelationship(), command.representativeGivenName(),
                    command.representativeMiddleName(), command.representativeFamilyName(),
                    command.representativeSecondFamilyName(), command.representativeDocumentType(),
                    command.representativeDocumentNumber(),
                    command.representativeAuthorizationFrom() == null
                            ? LocalDate.now(clock) : command.representativeAuthorizationFrom(),
                    command.representativeAuthorizationTo()));
        }

        // RN-005: tenant-configured mandatory consent types must be captured before commit.
        List<CommitPatientRegistrationCommand.ConsentSelection> providedConsents =
                command.consents() == null ? List.of() : command.consents();
        Set<String> providedTypes = providedConsents.stream()
                .map(CommitPatientRegistrationCommand.ConsentSelection::consentType)
                .collect(Collectors.toSet());
        Set<String> mandatory = policyStore.mandatoryConsentTypesFor(registration.tenantId());
        if (!providedTypes.containsAll(mandatory)) {
            throw new PeopleConflictException(
                    "REGISTRATION_CONSENT_MISSING: tenant policy requires consent types " + mandatory
                            + " to be captured before commit.");
        }
        for (CommitPatientRegistrationCommand.ConsentSelection consent : providedConsents) {
            patientManagementService.recordConsent(outcomePatientId, new RecordPatientConsentCommand(
                    consent.consentType(), consent.granted(), consent.grantedBy(), consent.evidenceReference()));
        }

        PatientRegistrationRequest committed = new PatientRegistrationRequest(
                registration.registrationRequestId(), registration.tenantId(), registration.laboratoryId(),
                registration.branchId(), registration.intakeChannel(), outcomePatientId,
                registration.registrationKind(), registration.normalizedFamilyName(),
                registration.normalizedGivenName(), registration.birthDate(), registration.draftGivenName(),
                registration.draftFamilyName(), registration.draftDocumentType(),
                registration.draftDocumentNumber(), registration.draftPatientCode(),
                PatientRegistrationRequest.OUTCOME_COMMITTED, outcomePatientId, registration.actorId(),
                registration.createdAt(), Instant.now(clock));
        PatientRegistrationRequest saved = repository.save(committed);
        // RN-007: audit event includes actor identity, branch and intake channel (already
        // generatable at start; recorded again here with the commit outcome).
        auditRecorder.recordSystemEvent(saved.tenantId(), "PatientRegistrationCommitted",
                "PatientRegistrationRequest", saved.registrationRequestId(),
                "{\"branchId\":\"%s\",\"intakeChannel\":\"%s\",\"actorId\":\"%s\",\"outcomePatientId\":\"%s\"}"
                        .formatted(jsonText(saved.branchId()), jsonText(saved.intakeChannel()),
                                jsonText(saved.actorId()), jsonText(outcomePatientId)));
        return saved;
    }

    public PatientRegistrationRequest get(String registrationRequestId) {
        return require(registrationRequestId);
    }

    public List<PatientRegistrationRequest> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    private PatientRegistrationRequest require(String registrationRequestId) {
        return repository.findById(requiredText(registrationRequestId, "Registration request id is required."))
                .orElseThrow(() -> new PeopleEntityNotFoundException("Patient registration request was not found."));
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
}
