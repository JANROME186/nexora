package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.adapter.in.web;

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

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.LaboratoryResultsService;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;

/**
 * REST controller for Result Release (BCM-LAB-010).
 *
 * <p>Generatable endpoint: listReleaseWorklist (GET).
 * <p>Custom-rule stubs: releaseResult, amendResult — logic deferred to MVP-MOD-006-BE-002.
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results/{resultId}/release")
class ResultReleaseController {

    private final LaboratoryResultsService service;

    ResultReleaseController(LaboratoryResultsService service) {
        this.service = service;
    }

    // Generatable: listReleaseWorklist (shared surface)
    @GetMapping("/release-worklist")
    ResponseEntity<List<LaboratoryResult>> listReleaseWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(service.listReleaseWorklist(tenantId, laboratoryId));
    }

    // Custom-rule stub: releaseResult (BE-002 implemented)
    @PostMapping("/release")
    ResponseEntity<LaboratoryResult> releaseResult(
            @PathVariable String resultId,
            @Valid @RequestBody ReleaseResultRequest request) {
        // Eligibility check spanning medical validation and sample status (CUS-LPR-010-01)
        // is implemented in the service.
        com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.ReleaseResultCommand command =
             new com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.ReleaseResultCommand(
                     resultId, request.tenantId(), request.actorId()
             );
        return ResponseEntity.ok(service.releaseResult(command));
    }

    // Custom-rule stub: amendResult (deferred to BE-002)
    @PostMapping("/amend")
    ResponseEntity<LaboratoryResult> amendResult(
            @PathVariable String resultId,
            @Valid @RequestBody AmendResultRequest request) {
        // Append-only amendment recording and authorization check (CUS-LPR-010-02)
        // are BE-002 extension points.
        return ResponseEntity.ok(service.getResult(resultId, request.tenantId()));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record ReleaseResultRequest(@NotBlank String tenantId, @NotBlank String actorId) {}

    record AmendResultRequest(
            @NotBlank String tenantId,
            @NotBlank String actorId,
            @NotBlank String amendmentReason,
            @NotBlank String licenseIdentifier) {
    }
}
