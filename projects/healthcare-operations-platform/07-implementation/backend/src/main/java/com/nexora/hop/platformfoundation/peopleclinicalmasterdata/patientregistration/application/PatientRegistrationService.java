package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.application;

import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.optionalText;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRepository;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientregistration.domain.PatientRegistrationRequest;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleValidation;

/**
 * Compiles the generatable outputs of BCM-ATT-002 (Patient Registration). The
 * duplicate-detection-consultation, atomic representative attach, match-resolution and
 * age-of-majority custom rules stay deferred to MVP-MOD-003-BE-002; those endpoints raise
 * {@link PeopleCustomRuleNotImplementedException} so BE-002 can plug them in without changing the
 * controller surface.
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
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PatientRegistrationService(PatientRegistrationRepository repository, AuditRecorder auditRecorder) {
        this(repository, auditRecorder, Clock.systemUTC());
    }

    PatientRegistrationService(PatientRegistrationRepository repository, AuditRecorder auditRecorder, Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public PatientRegistrationRequest start(StartPatientRegistrationCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String intakeChannel = requiredOneOf(command.intakeChannel(), "Intake channel is invalid.",
                INTAKE_CHANNELS.toArray(String[]::new));
        String registrationKind = requiredOneOf(command.registrationKind(), "Registration kind is invalid.",
                REGISTRATION_KINDS.toArray(String[]::new));
        String givenName = requiredText(command.givenName(), "Given name is required.");
        String familyName = requiredText(command.familyName(), "Family name is required.");
        String documentType = requiredText(command.documentType(), "Document type is required.");
        String documentNumber = requiredText(command.documentNumber(), "Document number is required.");

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
                "{\"branchId\":\"%s\",\"intakeChannel\":\"%s\"}"
                        .formatted(jsonText(branchId), jsonText(intakeChannel)));
        return saved;
    }

    public PatientRegistrationRequest cancel(String registrationRequestId, String reasonCode) {
        PatientRegistrationRequest current = require(registrationRequestId);
        if (!PatientRegistrationRequest.OUTCOME_PENDING.equals(current.outcome())) {
            throw new com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared.PeopleConflictException(
                    "Only pending registration requests can be cancelled.");
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
     * Committing a registration must consult duplicate detection, apply match resolution and
     * delegate to BCM-PER-002 commands (RN-001/002/003/005/006/008 custom rules). Deferred to
     * MVP-MOD-003-BE-002.
     */
    public PatientRegistrationRequest commit(String registrationRequestId) {
        require(registrationRequestId);
        throw new PeopleCustomRuleNotImplementedException("BCM-ATT-002-RN-001",
                "Committing a patient registration requires duplicate-detection orchestration and is "
                        + "deferred to MVP-MOD-003-BE-002.");
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
