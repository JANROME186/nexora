package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.application;

import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.optionalOneOf;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.optionalText;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application.AppointmentSchedulingService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisit;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisitRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.PatientDirectory;

/**
 * Compiles BCM-ATT-003 (Reception Management) generatable outputs and a functional baseline for
 * RN-001..RN-007. Identity confirmation is a read-only lookup against BCM-PER-002 (RN-003);
 * advancing to admission never writes DiagnosticOrder state (RN-006), it only signals readiness so
 * Admission Management (in the same module) can start its own process record.
 * <p>
 * <b>BE-002 hook:</b> {@link #updatePriority(String, String)} applies the requested priority
 * directly; tenant-configurable priority rules that also weigh elapsed wait time (RN-005) are
 * deferred.
 */
@Service
public class ReceptionManagementService {

    private static final List<String> INTAKE_CHANNELS = List.of(ReceptionVisit.CHANNEL_WALK_IN, ReceptionVisit.CHANNEL_SCHEDULED);
    private static final List<String> IDENTITY_METHODS = List.of(
            ReceptionVisit.IDENTITY_DOCUMENT_CHECK, ReceptionVisit.IDENTITY_PORTAL_HANDOFF,
            ReceptionVisit.IDENTITY_REPRESENTATIVE_VERIFICATION);
    private static final List<String> PRIORITIES = List.of(
            ReceptionVisit.PRIORITY_NORMAL, ReceptionVisit.PRIORITY_PRIORITY, ReceptionVisit.PRIORITY_URGENT);

    private final ReceptionVisitRepository repository;
    private final PatientDirectory patientDirectory;
    private final AppointmentSchedulingService appointmentSchedulingService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public ReceptionManagementService(
            ReceptionVisitRepository repository,
            PatientDirectory patientDirectory,
            AppointmentSchedulingService appointmentSchedulingService,
            AuditRecorder auditRecorder) {
        this(repository, patientDirectory, appointmentSchedulingService, auditRecorder, Clock.systemUTC());
    }

    ReceptionManagementService(
            ReceptionVisitRepository repository,
            PatientDirectory patientDirectory,
            AppointmentSchedulingService appointmentSchedulingService,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.patientDirectory = patientDirectory;
        this.appointmentSchedulingService = appointmentSchedulingService;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-002: a linked appointment must be checked_in. */
    public ReceptionVisit start(StartReceptionVisitCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String patientId = requiredText(command.patientId(), "Patient id is required.");
        String intakeChannel = requiredOneOf(command.intakeChannel(), "Intake channel is invalid.",
                INTAKE_CHANNELS.toArray(String[]::new));
        if (!patientDirectory.patientExists(patientId)) {
            throw new FrontDeskEntityNotFoundException("Patient was not found.");
        }

        String linkedAppointmentId = optionalText(command.linkedAppointmentId());
        if (linkedAppointmentId != null) {
            AppointmentSlot appointment = appointmentSchedulingService.get(linkedAppointmentId);
            if (!AppointmentSlot.STATUS_CHECKED_IN.equals(appointment.status())) {
                throw new FrontDeskConflictException(
                        "RECEPTION_APPOINTMENT_NOT_CHECKED_IN: the linked appointment must be checked in.");
            }
        }

        Instant now = Instant.now(clock);
        ReceptionVisit visit = new ReceptionVisit(
                newId(), tenantId, laboratoryId, branchId, patientId, linkedAppointmentId, intakeChannel,
                false, null, ReceptionVisit.QUEUE_WAITING, ReceptionVisit.PRIORITY_NORMAL,
                optionalText(command.actorId()), 1, now, now);
        ReceptionVisit saved = repository.save(visit);
        auditRecorder.recordSystemEvent(tenantId, "ReceptionVisitStarted", "ReceptionVisit", saved.visitId(),
                "{\"branchId\":\"%s\",\"intakeChannel\":\"%s\"}".formatted(jsonText(branchId), jsonText(intakeChannel)));
        return saved;
    }

    /** RN-001, RN-003: read-only identity confirmation against BCM-PER-002. */
    public ReceptionVisit confirmIdentity(String visitId, String identityConfirmationMethod) {
        ReceptionVisit visit = require(visitId);
        if (!patientDirectory.patientExists(visit.patientId())) {
            throw new FrontDeskEntityNotFoundException("Patient was not found.");
        }
        String method = requiredOneOf(identityConfirmationMethod, "Identity confirmation method is invalid.",
                IDENTITY_METHODS.toArray(String[]::new));
        ReceptionVisit confirmed = new ReceptionVisit(
                visit.visitId(), visit.tenantId(), visit.laboratoryId(), visit.branchId(), visit.patientId(),
                visit.linkedAppointmentId(), visit.intakeChannel(), true, method, visit.queueStatus(),
                visit.priority(), visit.actorId(), visit.version() + 1, visit.createdAt(), Instant.now(clock));
        ReceptionVisit saved = repository.save(confirmed);
        auditRecorder.recordSystemEvent(saved.tenantId(), "ReceptionIdentityConfirmed", "ReceptionVisit", visitId,
                "{\"identityConfirmationMethod\":\"%s\"}".formatted(jsonText(method)));
        return saved;
    }

    /** RN-001, RN-006: identity must be confirmed; this never writes DiagnosticOrder state. */
    public ReceptionVisit advanceToAdmission(String visitId) {
        ReceptionVisit visit = require(visitId);
        if (!visit.identityConfirmed()) {
            throw new FrontDeskConflictException(
                    "RECEPTION_IDENTITY_NOT_CONFIRMED: identity must be confirmed before advancing to admission.");
        }
        ReceptionVisit advanced = withQueueStatus(visit, ReceptionVisit.QUEUE_IN_ADMISSION);
        ReceptionVisit saved = repository.save(advanced);
        auditRecorder.recordSystemEvent(saved.tenantId(), "ReceptionVisitReadyForAdmission", "ReceptionVisit",
                visitId, "{\"branchId\":\"%s\"}".formatted(jsonText(saved.branchId())));
        return saved;
    }

    public ReceptionVisit updatePriority(String visitId, String priority) {
        ReceptionVisit visit = require(visitId);
        String resolvedPriority = optionalOneOf(priority, "Priority is invalid.", PRIORITIES.toArray(String[]::new));
        ReceptionVisit updated = new ReceptionVisit(
                visit.visitId(), visit.tenantId(), visit.laboratoryId(), visit.branchId(), visit.patientId(),
                visit.linkedAppointmentId(), visit.intakeChannel(), visit.identityConfirmed(),
                visit.identityConfirmationMethod(), visit.queueStatus(),
                resolvedPriority == null ? visit.priority() : resolvedPriority, visit.actorId(),
                visit.version() + 1, visit.createdAt(), Instant.now(clock));
        return repository.save(updated);
    }

    public ReceptionVisit abandon(String visitId) {
        ReceptionVisit visit = require(visitId);
        ReceptionVisit abandoned = withQueueStatus(visit, ReceptionVisit.QUEUE_ABANDONED);
        ReceptionVisit saved = repository.save(abandoned);
        auditRecorder.recordSystemEvent(saved.tenantId(), "ReceptionVisitAbandoned", "ReceptionVisit", visitId, "{}");
        return saved;
    }

    public ReceptionVisit get(String visitId) {
        return require(visitId);
    }

    public List<ReceptionVisit> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    private ReceptionVisit withQueueStatus(ReceptionVisit visit, String queueStatus) {
        return new ReceptionVisit(
                visit.visitId(), visit.tenantId(), visit.laboratoryId(), visit.branchId(), visit.patientId(),
                visit.linkedAppointmentId(), visit.intakeChannel(), visit.identityConfirmed(),
                visit.identityConfirmationMethod(), queueStatus, visit.priority(), visit.actorId(),
                visit.version() + 1, visit.createdAt(), Instant.now(clock));
    }

    private ReceptionVisit require(String visitId) {
        return repository.findById(requiredText(visitId, "Visit id is required."))
                .orElseThrow(() -> new FrontDeskEntityNotFoundException("Reception visit was not found."));
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
