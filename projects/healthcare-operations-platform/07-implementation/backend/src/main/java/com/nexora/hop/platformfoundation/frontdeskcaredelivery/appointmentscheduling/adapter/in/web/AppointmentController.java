package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.adapter.in.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application.AppointmentSchedulingService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.application.RequestAppointmentCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.RequestedCatalogItem;

/**
 * Rendered controller for {@code bcm-att-001-appointment-scheduling/openapi-source.yaml} (base
 * path /api/care-delivery/appointments).
 */
@RestController
@RequestMapping("/api/care-delivery/appointments")
class AppointmentController {

    private final AppointmentSchedulingService service;

    AppointmentController(AppointmentSchedulingService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<AppointmentSlot>> listAppointments(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{appointmentId}")
    ResponseEntity<AppointmentSlot> getAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.get(appointmentId));
    }

    @GetMapping("/{appointmentId}/requested-items")
    ResponseEntity<List<RequestedCatalogItem>> listRequestedItems(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.getRequestedItems(appointmentId));
    }

    @PostMapping
    ResponseEntity<AppointmentSlot> requestAppointment(@Valid @RequestBody RequestAppointmentRequest request) {
        AppointmentSlot created = service.request(new RequestAppointmentCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.patientId(),
                request.doctorId(), request.scheduledStart(), request.scheduledEnd(), request.channel(),
                request.actorId(),
                request.requestedItems() == null ? List.of()
                        : request.requestedItems().stream().map(RequestedItemRequest::toInput).toList()));
        return ResponseEntity.created(URI.create("/api/care-delivery/appointments/" + created.appointmentId()))
                .body(created);
    }

    @PostMapping("/{appointmentId}/confirm")
    ResponseEntity<AppointmentSlot> confirmAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.confirm(appointmentId));
    }

    @PostMapping("/{appointmentId}/check-in")
    ResponseEntity<AppointmentSlot> checkInAppointment(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.checkIn(appointmentId));
    }

    @PostMapping("/{appointmentId}/cancel")
    ResponseEntity<AppointmentSlot> cancelAppointment(@PathVariable String appointmentId,
            @RequestBody(required = false) CancelAppointmentRequest request) {
        String reasonCode = request == null ? null : request.reasonCode();
        return ResponseEntity.ok(service.cancel(appointmentId, reasonCode));
    }

    @PostMapping("/{appointmentId}/no-show")
    ResponseEntity<AppointmentSlot> markAppointmentNoShow(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.markNoShow(appointmentId));
    }

    @GetMapping("/{appointmentId}/preparation-instructions")
    ResponseEntity<List<PreparationInstruction>> getAppointmentPreparationInstructions(@PathVariable String appointmentId) {
        return ResponseEntity.ok(service.getPreparationInstructions(appointmentId));
    }

    record RequestedItemRequest(@NotBlank String testDefinitionId, @NotBlank String catalogItemKind) {
        RequestAppointmentCommand.RequestedItemInput toInput() {
            return new RequestAppointmentCommand.RequestedItemInput(testDefinitionId, catalogItemKind);
        }
    }

    record RequestAppointmentRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String patientId,
            String doctorId,
            LocalDate scheduledStart,
            LocalDate scheduledEnd,
            @NotBlank String channel,
            String actorId,
            List<RequestedItemRequest> requestedItems) {
    }

    record CancelAppointmentRequest(String reasonCode) {
    }
}
