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
 * REST controller for Medical Validation (BCM-LAB-009).
 *
 * <p>Generatable endpoint: listMedicalValidationWorklist (GET).
 * <p>Custom-rule stub: performMedicalValidation — logic deferred to MVP-MOD-006-BE-002.
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results/{resultId}/medical-validation")
class MedicalValidationController {

    private final LaboratoryResultsService service;

    MedicalValidationController(LaboratoryResultsService service) {
        this.service = service;
    }

    // Generatable: listMedicalValidationWorklist (shared surface)
    @GetMapping("/medical-validation-worklist")
    ResponseEntity<List<LaboratoryResult>> listMedicalValidationWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(service.listMedicalValidationWorklist(tenantId, laboratoryId));
    }

    // Custom-rule stub: performMedicalValidation (BE-002 implemented)
    @PostMapping("/validate")
    ResponseEntity<LaboratoryResult> performMedicalValidation(
            @PathVariable String resultId,
            @Valid @RequestBody MedicalValidateRequest request) {
        // Licensed-authority verification and AI-exclusion enforcement (CUS-LPR-009-01)
        // are implemented in the service.
        com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.MedicalValidateCommand command = 
             new com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.MedicalValidateCommand(
                     resultId, request.tenantId(), request.actorId(), request.licenseIdentifier()
             );
        return ResponseEntity.ok(service.medicalValidation(command));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record MedicalValidateRequest(
            @NotBlank String tenantId,
            @NotBlank String actorId,
            @NotBlank String licenseIdentifier) {
    }
}
