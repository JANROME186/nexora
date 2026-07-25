package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.adapter.in.web;

import java.net.URI;
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

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.application.ReceptionManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.application.StartReceptionVisitCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisit;

/**
 * Rendered controller for {@code bcm-att-003-reception-management/openapi-source.md} (base path
 * /api/care-delivery/reception-visits).
 */
@RestController
@RequestMapping("/api/care-delivery/reception-visits")
class ReceptionVisitController {

    private final ReceptionManagementService service;

    ReceptionVisitController(ReceptionManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ReceptionVisit>> listReceptionVisits(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{visitId}")
    ResponseEntity<ReceptionVisit> getReceptionVisit(@PathVariable String visitId) {
        return ResponseEntity.ok(service.get(visitId));
    }

    @PostMapping
    ResponseEntity<ReceptionVisit> startReceptionVisit(@Valid @RequestBody StartReceptionVisitRequest request) {
        ReceptionVisit started = service.start(new StartReceptionVisitCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.patientId(),
                request.linkedAppointmentId(), request.intakeChannel(), request.actorId()));
        return ResponseEntity.created(URI.create("/api/care-delivery/reception-visits/" + started.visitId()))
                .body(started);
    }

    @PostMapping("/{visitId}/confirm-identity")
    ResponseEntity<ReceptionVisit> confirmReceptionIdentity(@PathVariable String visitId,
            @Valid @RequestBody ConfirmIdentityRequest request) {
        return ResponseEntity.ok(service.confirmIdentity(visitId, request.identityConfirmationMethod()));
    }

    @PostMapping("/{visitId}/advance-to-admission")
    ResponseEntity<ReceptionVisit> advanceToAdmission(@PathVariable String visitId) {
        return ResponseEntity.ok(service.advanceToAdmission(visitId));
    }

    @PostMapping("/{visitId}/priority")
    ResponseEntity<ReceptionVisit> updateReceptionPriority(@PathVariable String visitId,
            @Valid @RequestBody UpdatePriorityRequest request) {
        return ResponseEntity.ok(service.updatePriority(visitId, request.priority()));
    }

    @PostMapping("/{visitId}/abandon")
    ResponseEntity<ReceptionVisit> abandonReceptionVisit(@PathVariable String visitId) {
        return ResponseEntity.ok(service.abandon(visitId));
    }

    record StartReceptionVisitRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String patientId,
            String linkedAppointmentId,
            @NotBlank String intakeChannel,
            String actorId) {
    }

    record ConfirmIdentityRequest(@NotBlank String identityConfirmationMethod) {
    }

    record UpdatePriorityRequest(@NotBlank String priority) {
    }
}
