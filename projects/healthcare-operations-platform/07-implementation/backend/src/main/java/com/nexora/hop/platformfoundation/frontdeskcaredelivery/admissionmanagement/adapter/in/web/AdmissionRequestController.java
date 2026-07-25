package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.adapter.in.web;

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

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application.AdmissionManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application.CommitAdmissionRequestCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application.MarkAdmissionReadyCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application.StartAdmissionRequestCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionCatalogSelection;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequest;

/**
 * Rendered controller for {@code bcm-att-004-admission-management/openapi-source.md} (base path
 * /api/care-delivery/admission-requests).
 */
@RestController
@RequestMapping("/api/care-delivery/admission-requests")
class AdmissionRequestController {

    private final AdmissionManagementService service;

    AdmissionRequestController(AdmissionManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<AdmissionRequest>> listAdmissionRequests(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{admissionId}")
    ResponseEntity<AdmissionRequest> getAdmissionRequest(@PathVariable String admissionId) {
        return ResponseEntity.ok(service.get(admissionId));
    }

    @GetMapping("/{admissionId}/catalog-selections")
    ResponseEntity<List<AdmissionCatalogSelection>> listCatalogSelections(@PathVariable String admissionId) {
        return ResponseEntity.ok(service.getSelections(admissionId));
    }

    @PostMapping
    ResponseEntity<AdmissionRequest> startAdmissionRequest(@Valid @RequestBody StartAdmissionRequest request) {
        AdmissionRequest started = service.start(new StartAdmissionRequestCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.visitId(),
                request.patientId(), request.doctorId(), request.actorId()));
        return ResponseEntity.created(URI.create("/api/care-delivery/admission-requests/" + started.admissionId()))
                .body(started);
    }

    @PostMapping("/{admissionId}/mark-ready")
    ResponseEntity<AdmissionRequest> markAdmissionReady(@PathVariable String admissionId,
            @Valid @RequestBody MarkReadyRequest request) {
        return ResponseEntity.ok(service.markReady(admissionId, new MarkAdmissionReadyCommand(
                request.clinicalNotesDraft(),
                request.catalogSelection() == null ? List.of()
                        : request.catalogSelection().stream().map(CatalogSelectionRequest::toInput).toList())));
    }

    @PostMapping("/{admissionId}/commit")
    ResponseEntity<AdmissionRequest> commitAdmissionRequest(@PathVariable String admissionId,
            @Valid @RequestBody CommitAdmissionRequest request) {
        return ResponseEntity.ok(service.commit(admissionId, new CommitAdmissionRequestCommand(
                request.consentConfirmed(), request.sampleRequirementsAcknowledged())));
    }

    @PostMapping("/{admissionId}/reject")
    ResponseEntity<AdmissionRequest> rejectAdmissionRequest(@PathVariable String admissionId,
            @RequestBody(required = false) RejectAdmissionRequest request) {
        String reason = request == null ? null : request.rejectionReason();
        return ResponseEntity.ok(service.reject(admissionId, reason));
    }

    record CatalogSelectionRequest(@NotBlank String testDefinitionId, @NotBlank String catalogItemKind, Integer quantity) {
        MarkAdmissionReadyCommand.CatalogSelectionInput toInput() {
            return new MarkAdmissionReadyCommand.CatalogSelectionInput(testDefinitionId, catalogItemKind, quantity);
        }
    }

    record StartAdmissionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String visitId,
            @NotBlank String patientId,
            String doctorId,
            String actorId) {
    }

    record MarkReadyRequest(String clinicalNotesDraft, List<CatalogSelectionRequest> catalogSelection) {
    }

    record CommitAdmissionRequest(boolean consentConfirmed, boolean sampleRequirementsAcknowledged) {
    }

    record RejectAdmissionRequest(String rejectionReason) {
    }
}
