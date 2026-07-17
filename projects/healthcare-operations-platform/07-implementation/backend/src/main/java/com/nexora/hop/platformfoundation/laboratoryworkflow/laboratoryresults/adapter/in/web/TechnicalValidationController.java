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
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.SubmitForValidationCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;

/**
 * REST controller for Technical Validation (BCM-LAB-008).
 *
 * <p>Generatable endpoint: listTechnicalValidationWorklist (GET).
 * <p>Custom-rule stubs: performTechnicalValidation, flagCriticalResult — validation
 *   logic deferred to MVP-MOD-006-BE-002.
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results/{resultId}/technical-validation")
class TechnicalValidationController {

    private final LaboratoryResultsService service;

    TechnicalValidationController(LaboratoryResultsService service) {
        this.service = service;
    }

    // Generatable: listTechnicalValidationWorklist (shared surface)
    @GetMapping("/technical-validation-worklist")
    ResponseEntity<List<LaboratoryResult>> listTechnicalValidationWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(service.listTechnicalValidationWorklist(tenantId, laboratoryId));
    }

    // Submit for technical validation (BCM-LAB-006 extension point CUS-LPR-006-04)
    @PostMapping("/submit")
    ResponseEntity<LaboratoryResult> submitForValidation(
            @PathVariable String resultId,
            @Valid @RequestBody SubmitValidationRequest request) {
        return ResponseEntity.ok(service.submitForValidation(new SubmitForValidationCommand(
                resultId, request.tenantId(), request.actorId())));
    }

    // Custom-rule stub: performTechnicalValidation (BE-002 implemented)
    @PostMapping("/validate")
    ResponseEntity<LaboratoryResult> performTechnicalValidation(
            @PathVariable String resultId,
            @Valid @RequestBody TechnicalValidateRequest request) {
        // Multi-criterion acceptance check (CUS-LPR-008-01) and Critical-threshold comparison (CUS-LPR-008-02) 
        // are implemented in the service.
        com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.TechnicalValidateCommand command = 
             new com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.TechnicalValidateCommand(
                     resultId, request.tenantId(), request.actorId(), request.approved()
             );
        return ResponseEntity.ok(service.technicalValidation(command));
    }

    @PostMapping("/flag-critical")
    ResponseEntity<LaboratoryResult> flagCriticalResult(
            @PathVariable String resultId,
            @Valid @RequestBody FlagCriticalRequest request) {
        return ResponseEntity.ok(service.flagCriticalResult(resultId, request.tenantId(), request.actorId(), request.criticalReason()));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record SubmitValidationRequest(@NotBlank String tenantId, @NotBlank String actorId) {}

    record TechnicalValidateRequest(@NotBlank String tenantId, @NotBlank String actorId, boolean approved) {}

    record FlagCriticalRequest(
            @NotBlank String tenantId,
            @NotBlank String actorId,
            @NotBlank String criticalReason) {
    }
}
