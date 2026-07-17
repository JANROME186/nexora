package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.OrderSamplesService;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.ReceiveSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.RejectSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;

/**
 * REST controller for Sample Reception (BCM-LAB-005).
 *
 * <p>Custom-rule stubs: receiveSampleAtLaboratory, rejectSampleAtReception —
 *   multi-criterion condition check deferred to MVP-MOD-006-BE-002.
 */
@RestController
@RequestMapping("/api/clinical-operations/samples/{sampleId}/reception")
class SampleReceptionController {

    private final OrderSamplesService service;

    SampleReceptionController(OrderSamplesService service) {
        this.service = service;
    }

    // Custom-rule stub: receiveSampleAtLaboratory (CUS-LAB-005-01/02 deferred to BE-002)
    @PostMapping("/receive")
    ResponseEntity<Sample> receiveSampleAtLaboratory(
            @PathVariable String sampleId,
            @Valid @RequestBody ReceiveSampleRequest request) {
        return ResponseEntity.ok(service.receiveSample(new ReceiveSampleCommand(
                sampleId, request.tenantId(),
                request.receivedBy(), request.conditionAtReception())));
    }

    // Custom-rule stub: rejectSampleAtReception (terminal-state guard deferred to BE-002)
    @PostMapping("/reject")
    ResponseEntity<Sample> rejectSampleAtReception(
            @PathVariable String sampleId,
            @Valid @RequestBody RejectAtReceptionRequest request) {
        return ResponseEntity.ok(service.rejectSample(new RejectSampleCommand(
                sampleId, request.tenantId(), request.rejectedBy(),
                "at_reception", request.reasonCode(), request.notes())));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record ReceiveSampleRequest(
            @NotBlank String tenantId,
            @NotBlank String receivedBy,
            String conditionAtReception) {
    }

    record RejectAtReceptionRequest(
            @NotBlank String tenantId,
            @NotBlank String rejectedBy,
            @NotBlank String reasonCode,
            String notes) {
    }
}
