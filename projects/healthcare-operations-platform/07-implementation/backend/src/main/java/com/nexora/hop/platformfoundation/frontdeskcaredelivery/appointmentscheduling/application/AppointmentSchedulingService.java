package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application;

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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.PanelCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.PatientPreparationManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.TestCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlotRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.RequestedCatalogItem;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskPolicyStore;
import com.nexora.hop.platformfoundation.organizationmanagement.BranchDirectory;

/**
 * Compiles BCM-ATT-001 (Appointment Scheduling) generatable outputs and implements its custom
 * rules RN-001..RN-007. Never mutates DiagnosticOrder state directly (RN-005); check-in only
 * hands the appointment off, order creation happens downstream in Admission Management.
 * <p>
 * <b>MVP-MOD-004-BE-002 refinements:</b> {@link #confirm(String)} now also enforces a tenant-
 * configurable daily appointment capacity per branch (RN-002, {@link FrontDeskPolicyStore
 * #branchDailyAppointmentCapacityFor(String)}); {@link #markNoShow(String)} enforces a tenant-
 * configurable grace period after the scheduled date before a confirmed appointment can be
 * marked no-show (RN-006, {@link FrontDeskPolicyStore#noShowGraceDaysFor(String)}); and
 * {@link #getPreparationInstructions(String)} surfaces published preparation instructions
 * (VO-APT-002) for every requested catalog item.
 */
@Service
public class AppointmentSchedulingService {

    private static final List<String> CHANNELS = List.of(
            AppointmentSlot.CHANNEL_WALK_IN_SCHEDULING, AppointmentSlot.CHANNEL_PHONE,
            AppointmentSlot.CHANNEL_EMPLOYEE_PORTAL, AppointmentSlot.CHANNEL_PATIENT_PORTAL_REQUEST_LATER);

    private static final List<String> ITEM_KINDS = List.of(
            RequestedCatalogItem.KIND_TEST, RequestedCatalogItem.KIND_PANEL);

    private final AppointmentSlotRepository repository;
    private final BranchDirectory branchDirectory;
    private final TestCatalogService testCatalogService;
    private final PanelCatalogService panelCatalogService;
    private final PatientPreparationManagementService preparationManagementService;
    private final FrontDeskPolicyStore policyStore;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public AppointmentSchedulingService(
            AppointmentSlotRepository repository,
            BranchDirectory branchDirectory,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PatientPreparationManagementService preparationManagementService,
            FrontDeskPolicyStore policyStore,
            AuditRecorder auditRecorder) {
        this(repository, branchDirectory, testCatalogService, panelCatalogService, preparationManagementService,
                policyStore, auditRecorder, Clock.systemUTC());
    }

    AppointmentSchedulingService(
            AppointmentSlotRepository repository,
            BranchDirectory branchDirectory,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PatientPreparationManagementService preparationManagementService,
            FrontDeskPolicyStore policyStore,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.branchDirectory = branchDirectory;
        this.testCatalogService = testCatalogService;
        this.panelCatalogService = panelCatalogService;
        this.preparationManagementService = preparationManagementService;
        this.policyStore = policyStore;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-003, RN-004: requested catalog items must be published; scope is authorization-checked upstream. */
    public AppointmentSlot request(RequestAppointmentCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String patientId = requiredText(command.patientId(), "Patient id is required.");
        String channel = requiredOneOf(command.channel(), "Appointment channel is invalid.",
                CHANNELS.toArray(String[]::new));
        if (command.scheduledStart() == null) {
            throw new com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.InvalidFrontDeskCommandException(
                    "Scheduled start date is required.");
        }
        java.time.LocalDate scheduledEnd = command.scheduledEnd() == null ? command.scheduledStart() : command.scheduledEnd();

        String appointmentId = newId();
        List<RequestAppointmentCommand.RequestedItemInput> requestedItems =
                command.requestedItems() == null ? List.of() : command.requestedItems();
        for (RequestAppointmentCommand.RequestedItemInput item : requestedItems) {
            validateCatalogItemPublished(item.testDefinitionId(), item.catalogItemKind());
        }

        Instant now = Instant.now(clock);
        AppointmentSlot appointment = new AppointmentSlot(
                appointmentId, tenantId, laboratoryId, branchId, patientId, optionalText(command.doctorId()),
                command.scheduledStart(), scheduledEnd, channel, AppointmentSlot.STATUS_REQUESTED, null, null,
                optionalText(command.actorId()), 1, now, now);
        AppointmentSlot saved = repository.save(appointment);
        for (RequestAppointmentCommand.RequestedItemInput item : requestedItems) {
            repository.saveRequestedItem(new RequestedCatalogItem(
                    newId(), appointmentId, item.testDefinitionId(), item.catalogItemKind()));
        }
        auditRecorder.recordSystemEvent(tenantId, "AppointmentRequested", "AppointmentSlot", appointmentId,
                "{\"branchId\":\"%s\",\"channel\":\"%s\"}".formatted(jsonText(branchId), jsonText(channel)));
        return saved;
    }

    /**
     * RN-001, RN-002: branch operational-status, same-patient overlap and tenant-configurable
     * daily branch capacity validation ({@link FrontDeskPolicyStore
     * #branchDailyAppointmentCapacityFor(String)}, default
     * {@value FrontDeskPolicyStore#DEFAULT_BRANCH_DAILY_APPOINTMENT_CAPACITY}).
     */
    public AppointmentSlot confirm(String appointmentId) {
        AppointmentSlot appointment = require(appointmentId);
        if (!AppointmentSlot.STATUS_REQUESTED.equals(appointment.status())) {
            throw new FrontDeskConflictException("Only a requested appointment can be confirmed.");
        }
        if (!branchDirectory.isBranchOperational(appointment.branchId())) {
            throw new FrontDeskConflictException(
                    "APPOINTMENT_BRANCH_NOT_ACTIVE: the branch is not operationally active.");
        }
        boolean overlaps = repository.findByPatientAndBranch(appointment.patientId(), appointment.branchId()).stream()
                .filter(other -> !other.appointmentId().equals(appointmentId))
                .filter(other -> AppointmentSlot.STATUS_CONFIRMED.equals(other.status())
                        || AppointmentSlot.STATUS_CHECKED_IN.equals(other.status()))
                .anyMatch(other -> windowsOverlap(appointment, other));
        if (overlaps) {
            throw new FrontDeskConflictException(
                    "APPOINTMENT_WINDOW_OVERLAP: an overlapping confirmed appointment already exists for this patient.");
        }
        int capacity = policyStore.branchDailyAppointmentCapacityFor(appointment.tenantId());
        long confirmedOnDate = repository.findByBranchId(appointment.branchId()).stream()
                .filter(other -> !other.appointmentId().equals(appointmentId))
                .filter(other -> AppointmentSlot.STATUS_CONFIRMED.equals(other.status())
                        || AppointmentSlot.STATUS_CHECKED_IN.equals(other.status()))
                .filter(other -> windowsOverlap(appointment, other))
                .count();
        if (confirmedOnDate >= capacity) {
            throw new FrontDeskConflictException(
                    "APPOINTMENT_BRANCH_CAPACITY_EXCEEDED: the branch has reached its daily appointment capacity.");
        }
        AppointmentSlot confirmed = withStatus(appointment, AppointmentSlot.STATUS_CONFIRMED, appointment.cancellationReason());
        AppointmentSlot saved = repository.save(confirmed);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AppointmentConfirmed", "AppointmentSlot", appointmentId,
                "{\"branchId\":\"%s\"}".formatted(jsonText(saved.branchId())));
        return saved;
    }

    /** RN-005: hands off to Reception Management without mutating a diagnostic order. */
    public AppointmentSlot checkIn(String appointmentId) {
        AppointmentSlot appointment = require(appointmentId);
        if (!AppointmentSlot.STATUS_CONFIRMED.equals(appointment.status())) {
            throw new FrontDeskConflictException("Only a confirmed appointment can be checked in.");
        }
        AppointmentSlot checkedIn = withStatus(appointment, AppointmentSlot.STATUS_CHECKED_IN, appointment.cancellationReason());
        AppointmentSlot saved = repository.save(checkedIn);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AppointmentCheckedIn", "AppointmentSlot", appointmentId,
                "{\"branchId\":\"%s\"}".formatted(jsonText(saved.branchId())));
        return saved;
    }

    public AppointmentSlot cancel(String appointmentId, String reasonCode) {
        AppointmentSlot appointment = require(appointmentId);
        if (AppointmentSlot.STATUS_CHECKED_IN.equals(appointment.status())
                || AppointmentSlot.STATUS_CANCELLED.equals(appointment.status())
                || AppointmentSlot.STATUS_COMPLETED.equals(appointment.status())) {
            throw new FrontDeskConflictException("Only a requested or confirmed appointment can be cancelled.");
        }
        AppointmentSlot cancelled = new AppointmentSlot(
                appointment.appointmentId(), appointment.tenantId(), appointment.laboratoryId(),
                appointment.branchId(), appointment.patientId(), appointment.doctorId(),
                appointment.scheduledStart(), appointment.scheduledEnd(), appointment.channel(),
                AppointmentSlot.STATUS_CANCELLED, appointment.linkedOrderId(),
                optionalText(reasonCode) == null ? "unspecified" : reasonCode, appointment.actorId(),
                appointment.version() + 1, appointment.createdAt(), Instant.now(clock));
        AppointmentSlot saved = repository.save(cancelled);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AppointmentCancelled", "AppointmentSlot", appointmentId,
                "{\"reasonCode\":\"%s\"}".formatted(jsonText(saved.cancellationReason())));
        return saved;
    }

    /**
     * RN-006: a confirmed appointment can only be marked no-show once its tenant-configurable
     * grace period ({@link FrontDeskPolicyStore#noShowGraceDaysFor(String)}, in days, consistent
     * with {@link AppointmentSlot}'s date-only scheduling granularity) has elapsed after the
     * scheduled end date. This remains a manually triggered endpoint (no scheduler exists yet)
     * but is no longer evaluated unconditionally.
     */
    public AppointmentSlot markNoShow(String appointmentId) {
        AppointmentSlot appointment = require(appointmentId);
        if (!AppointmentSlot.STATUS_CONFIRMED.equals(appointment.status())) {
            throw new FrontDeskConflictException("Only a confirmed appointment can be marked no-show.");
        }
        int graceDays = policyStore.noShowGraceDaysFor(appointment.tenantId());
        java.time.LocalDate earliestNoShowDate = appointment.scheduledEnd().plusDays(graceDays);
        java.time.LocalDate today = java.time.LocalDate.now(clock);
        if (today.isBefore(earliestNoShowDate)) {
            throw new FrontDeskConflictException(
                    "APPOINTMENT_NO_SHOW_GRACE_PERIOD_ACTIVE: the no-show grace period has not elapsed yet.");
        }
        AppointmentSlot noShow = withStatus(appointment, AppointmentSlot.STATUS_NO_SHOW, appointment.cancellationReason());
        AppointmentSlot saved = repository.save(noShow);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AppointmentNoShowMarked", "AppointmentSlot", appointmentId,
                "{\"branchId\":\"%s\",\"graceDays\":%d}".formatted(jsonText(saved.branchId()), graceDays));
        return saved;
    }

    public AppointmentSlot get(String appointmentId) {
        return require(appointmentId);
    }

    public List<AppointmentSlot> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<RequestedCatalogItem> getRequestedItems(String appointmentId) {
        require(appointmentId);
        return repository.findRequestedItems(appointmentId);
    }

    /**
     * VO-APT-002: surfaces published preparation instructions for every catalog item requested on
     * this appointment, so front desk staff and patients can see fasting/medication/activity
     * guidance before the visit.
     */
    public List<PreparationInstruction> getPreparationInstructions(String appointmentId) {
        require(appointmentId);
        List<RequestedCatalogItem> items = repository.findRequestedItems(appointmentId);
        java.util.LinkedHashMap<String, PreparationInstruction> byId = new java.util.LinkedHashMap<>();
        for (RequestedCatalogItem item : items) {
            String targetType = RequestedCatalogItem.KIND_TEST.equals(item.catalogItemKind())
                    ? com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationAssignment.TARGET_TEST
                    : com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationAssignment.TARGET_PANEL;
            for (PreparationInstruction preparation
                    : preparationManagementService.findPublishedForTarget(targetType, item.testDefinitionId())) {
                byId.putIfAbsent(preparation.preparationId(), preparation);
            }
        }
        return List.copyOf(byId.values());
    }

    private void validateCatalogItemPublished(String testDefinitionId, String catalogItemKind) {
        String kind = requiredOneOf(catalogItemKind, "Requested item kind is invalid.", ITEM_KINDS.toArray(String[]::new));
        String id = requiredText(testDefinitionId, "Requested item catalog id is required.");
        if (RequestedCatalogItem.KIND_TEST.equals(kind)) {
            TestDefinition testDefinition = testCatalogService.get(id);
            if (!TestDefinition.STATUS_PUBLISHED.equals(testDefinition.status())) {
                throw new FrontDeskConflictException(
                        "APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED: test " + id + " is not published.");
            }
        } else {
            PanelDefinition panelDefinition = panelCatalogService.get(id);
            if (!PanelDefinition.STATUS_PUBLISHED.equals(panelDefinition.status())) {
                throw new FrontDeskConflictException(
                        "APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED: panel " + id + " is not published.");
            }
        }
    }

    private static boolean windowsOverlap(AppointmentSlot a, AppointmentSlot b) {
        return !a.scheduledStart().isAfter(b.scheduledEnd()) && !b.scheduledStart().isAfter(a.scheduledEnd());
    }

    private AppointmentSlot withStatus(AppointmentSlot appointment, String status, String cancellationReason) {
        return new AppointmentSlot(
                appointment.appointmentId(), appointment.tenantId(), appointment.laboratoryId(),
                appointment.branchId(), appointment.patientId(), appointment.doctorId(),
                appointment.scheduledStart(), appointment.scheduledEnd(), appointment.channel(), status,
                appointment.linkedOrderId(), cancellationReason, appointment.actorId(), appointment.version() + 1,
                appointment.createdAt(), Instant.now(clock));
    }

    private AppointmentSlot require(String appointmentId) {
        return repository.findById(requiredText(appointmentId, "Appointment id is required."))
                .orElseThrow(() -> new FrontDeskEntityNotFoundException("Appointment was not found."));
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
