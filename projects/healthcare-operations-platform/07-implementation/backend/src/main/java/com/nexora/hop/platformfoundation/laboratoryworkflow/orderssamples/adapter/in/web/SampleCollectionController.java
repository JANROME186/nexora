package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.adapter.in.web;

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

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.CollectSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.DisposeSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.OrderSamplesService;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application.RejectSampleCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;

/**
 * REST controller for Sample Collection (BCM-LAB-002) and related generatable operations.
 *
 * <p>Generatable endpoints: listCollectionWorklist (GET), getSample (GET).
 * <p>Custom-rule stubs: collectSample (POST), rejectSampleAtCollection (POST) —
 *   full validation deferred to MVP-MOD-006-BE-002 (CUS-COL-002-01 through CUS-COL-002-05).
 */
@RestController
@RequestMapping("/api/clinical-operations/samples")
class SampleCollectionController {

    private final OrderSamplesService service;

    SampleCollectionController(OrderSamplesService service) {
        this.service = service;
    }

    // Generatable: listCollectionWorklist
    @GetMapping("/collection-worklist")
    ResponseEntity<List<Sample>> listCollectionWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String branchId) {
        return ResponseEntity.ok(service.listCollectionWorklist(tenantId, branchId));
    }

    // Generatable: getSample
    @GetMapping("/{sampleId}")
    ResponseEntity<Sample> getSample(
            @PathVariable String sampleId,
            @RequestParam @NotBlank String tenantId) {
        return ResponseEntity.ok(service.getSample(sampleId, tenantId));
    }

    // Custom-rule stub: collectSample (CUS-COL-002-01/02/03 deferred to BE-002)
    @PostMapping
    ResponseEntity<Sample> collectSample(@Valid @RequestBody CollectSampleRequest request) {
        Sample created = service.collectSample(new CollectSampleCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(),
                request.orderId(), request.orderLineId(),
                request.collectorId(), request.collectionSite(), request.collectionMethod(),
                request.containerUsed(), null,
                request.patientConditionAtCollection(),
                request.patientId(), request.patientFullName(), request.patientBirthDate(),
                request.sampleRequirementId(), request.containerType()));
        return ResponseEntity
                .created(URI.create("/api/clinical-operations/samples/" + created.sampleId()))
                .body(created);
    }

    // Custom-rule stub: rejectSampleAtCollection (CUS-COL-002-04 deferred to BE-002)
    @PostMapping("/{sampleId}/reject-at-collection")
    ResponseEntity<Sample> rejectSampleAtCollection(
            @PathVariable String sampleId,
            @Valid @RequestBody RejectAtCollectionRequest request) {
        return ResponseEntity.ok(service.rejectSample(new RejectSampleCommand(
                sampleId, request.tenantId(), request.rejectedBy(),
                "at_collection", request.reasonCode(), request.notes())));
    }

    // rejectAtReception stub (shared reject path, BCM-LAB-005 context)
    @PostMapping("/{sampleId}/dispose")
    ResponseEntity<Sample> disposeSample(
            @PathVariable String sampleId,
            @Valid @RequestBody DisposeSampleRequest request) {
        return ResponseEntity.ok(service.disposeSample(
                new DisposeSampleCommand(sampleId, request.tenantId(),
                        request.actorId(), request.disposalNotes())));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record CollectSampleRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String orderId,
            @NotBlank String orderLineId,
            @NotBlank String collectorId,
            String collectionSite,
            @NotBlank String collectionMethod,
            @NotBlank String containerUsed,
            String patientConditionAtCollection,
            @NotBlank String patientId,
            @NotBlank String patientFullName,
            @NotBlank String patientBirthDate,
            String sampleRequirementId,
            String containerType) {
    }

    record RejectAtCollectionRequest(
            @NotBlank String tenantId,
            @NotBlank String rejectedBy,
            @NotBlank String reasonCode,
            String notes) {
    }

    record DisposeSampleRequest(
            @NotBlank String tenantId,
            @NotBlank String actorId,
            String disposalNotes) {
    }
}
