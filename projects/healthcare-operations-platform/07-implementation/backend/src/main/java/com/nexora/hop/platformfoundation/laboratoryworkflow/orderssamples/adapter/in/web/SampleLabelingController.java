package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.adapter.in.web;

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

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.LabelSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.OrderSamplesService;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;

/**
 * REST controller for Sample Labeling (BCM-LAB-003).
 *
 * <p>Generatable endpoint: getLabelPrintJob (GET).
 * <p>Custom-rule stubs: printSpecimenLabel, confirmSpecimenLabel, requestLabelReprint —
 *   full barcode generation and mismatch detection deferred to MVP-MOD-006-BE-002.
 */
@RestController
@RequestMapping("/api/clinical-operations/samples/{sampleId}/label")
class SampleLabelingController {

    private final OrderSamplesService service;

    SampleLabelingController(OrderSamplesService service) {
        this.service = service;
    }

    // Generatable: getLabelPrintJob
    @GetMapping
    ResponseEntity<Sample> getLabelPrintJob(
            @PathVariable String sampleId,
            @RequestParam @NotBlank String tenantId) {
        return ResponseEntity.ok(service.getSample(sampleId, tenantId));
    }

    // Custom-rule stub: printSpecimenLabel (barcode generation deferred to BE-002)
    @PostMapping("/print")
    ResponseEntity<Sample> printSpecimenLabel(
            @PathVariable String sampleId,
            @Valid @RequestBody PrintLabelRequest request) {
        return ResponseEntity.ok(service.labelSample(new LabelSampleCommand(
                sampleId, request.tenantId(),
                request.labelId(), request.barcodeValue(), request.actorId())));
    }

    // Custom-rule stub: confirmSpecimenLabel (mismatch detection deferred to BE-002)
    @PostMapping("/confirm")
    ResponseEntity<Sample> confirmSpecimenLabel(
            @PathVariable String sampleId,
            @Valid @RequestBody ConfirmLabelRequest request) {
        // Mismatch detection (CUS-LAB-003-02) is a BE-002 extension point.
        return ResponseEntity.ok(service.getSample(sampleId, request.tenantId()));
    }

    // Custom-rule stub: requestLabelReprint (override reason deferred to BE-002)
    @PostMapping("/reprint")
    ResponseEntity<Sample> requestLabelReprint(
            @PathVariable String sampleId,
            @Valid @RequestBody ReprintLabelRequest request) {
        // Override reason capture (CUS-LAB-003-03) is a BE-002 extension point.
        return ResponseEntity.ok(service.getSample(sampleId, request.tenantId()));
    }

    // Generatable: listReceptionWorklist (shared surface)
    @GetMapping("/reception-worklist")
    ResponseEntity<List<Sample>> listReceptionWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(service.listReceptionWorklist(tenantId, laboratoryId));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record PrintLabelRequest(
            @NotBlank String tenantId,
            @NotBlank String labelId,
            @NotBlank String barcodeValue,
            @NotBlank String actorId) {
    }

    record ConfirmLabelRequest(@NotBlank String tenantId, @NotBlank String actorId) {
    }

    record ReprintLabelRequest(@NotBlank String tenantId, @NotBlank String actorId,
            String overrideReason) {
    }
}
