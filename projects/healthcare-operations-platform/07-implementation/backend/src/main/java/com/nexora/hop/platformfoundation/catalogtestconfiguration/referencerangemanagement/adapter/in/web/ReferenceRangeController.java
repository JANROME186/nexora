package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.adapter.in.web;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application.CreateReferenceRangeCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application.ReferenceRangeManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application.ReferenceRangeSegmentInput;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application.UpdateReferenceRangeCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRange;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain.ReferenceRangeSegment;

/**
 * Rendered controller for bcm-svc-006-reference-range-management/openapi-source.yaml
 * (base path /api/catalog/reference-ranges).
 */
@RestController
@RequestMapping("/api/catalog/reference-ranges")
class ReferenceRangeController {

    private final ReferenceRangeManagementService service;

    ReferenceRangeController(ReferenceRangeManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ReferenceRangeResponse>> listReferenceRanges(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/{rangeId}")
    ResponseEntity<ReferenceRangeResponse> getReferenceRange(@PathVariable String rangeId) {
        return ResponseEntity.ok(toResponse(service.get(rangeId)));
    }

    @PostMapping
    ResponseEntity<ReferenceRangeResponse> createReferenceRange(@Valid @RequestBody CreateReferenceRangeRequest request) {
        ReferenceRange created = service.create(new CreateReferenceRangeCommand(
                request.tenantId(), request.laboratoryId(), request.analyteRefId(), request.effectiveFrom(),
                request.effectiveTo(), toInputs(request.segments())));
        return ResponseEntity.created(URI.create("/api/catalog/reference-ranges/" + created.rangeId()))
                .body(toResponse(created));
    }

    @PutMapping("/{rangeId}")
    ResponseEntity<ReferenceRangeResponse> updateReferenceRange(
            @PathVariable String rangeId, @Valid @RequestBody UpdateReferenceRangeRequest request) {
        ReferenceRange updated = service.update(rangeId, new UpdateReferenceRangeCommand(
                request.effectiveFrom(), request.effectiveTo(), toInputs(request.segments())));
        return ResponseEntity.ok(toResponse(updated));
    }

    @PostMapping("/{rangeId}/publish")
    ResponseEntity<ReferenceRangeResponse> publishReferenceRange(@PathVariable String rangeId) {
        return ResponseEntity.ok(toResponse(service.publish(rangeId)));
    }

    @PostMapping("/{rangeId}/deprecate")
    ResponseEntity<ReferenceRangeResponse> deprecateReferenceRange(@PathVariable String rangeId) {
        return ResponseEntity.ok(toResponse(service.deprecate(rangeId)));
    }

    @GetMapping("/effective")
    ResponseEntity<ReferenceRangeResponse> getEffectiveRangeSnapshot(
            @RequestParam String analyteId,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) Integer ageDays,
            @RequestParam(required = false) String observationDate) {
        return ResponseEntity.ok(toResponse(service.getEffectiveRangeSnapshot(analyteId, sex, ageDays, observationDate)));
    }

    private ReferenceRangeResponse toResponse(ReferenceRange entity) {
        return ReferenceRangeResponse.from(entity, service.getSegments(entity.rangeId()));
    }

    private static List<ReferenceRangeSegmentInput> toInputs(List<ReferenceRangeSegmentRequest> segments) {
        if (segments == null) {
            return List.of();
        }
        return segments.stream()
                .map(segment -> new ReferenceRangeSegmentInput(
                        segment.sex(), segment.ageMinDays(), segment.ageMaxDays(), segment.condition(),
                        segment.normalLow(), segment.normalHigh(), segment.criticalLow(), segment.criticalHigh(),
                        segment.unit()))
                .toList();
    }

    record ReferenceRangeSegmentRequest(
            @NotBlank String sex,
            Integer ageMinDays,
            Integer ageMaxDays,
            String condition,
            BigDecimal normalLow,
            BigDecimal normalHigh,
            BigDecimal criticalLow,
            BigDecimal criticalHigh,
            String unit) {
    }

    record CreateReferenceRangeRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String analyteRefId,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo,
            List<ReferenceRangeSegmentRequest> segments) {
    }

    record UpdateReferenceRangeRequest(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo,
            List<ReferenceRangeSegmentRequest> segments) {
    }

    record ReferenceRangeSegmentResponse(
            String segmentId,
            String sex,
            Integer ageMinDays,
            Integer ageMaxDays,
            String condition,
            BigDecimal normalLow,
            BigDecimal normalHigh,
            BigDecimal criticalLow,
            BigDecimal criticalHigh,
            String unit) {
        static ReferenceRangeSegmentResponse from(ReferenceRangeSegment segment) {
            return new ReferenceRangeSegmentResponse(
                    segment.segmentId(), segment.sex(), segment.ageMinDays(), segment.ageMaxDays(),
                    segment.condition(), segment.normalLow(), segment.normalHigh(), segment.criticalLow(),
                    segment.criticalHigh(), segment.unit());
        }
    }

    record ReferenceRangeResponse(
            String rangeId,
            String tenantId,
            String laboratoryId,
            String analyteRefId,
            int version,
            String status,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            List<ReferenceRangeSegmentResponse> segments,
            Instant createdAt,
            Instant updatedAt) {
        static ReferenceRangeResponse from(ReferenceRange entity, List<ReferenceRangeSegment> segments) {
            return new ReferenceRangeResponse(
                    entity.rangeId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.analyteRefId(),
                    entity.version(),
                    entity.status(),
                    entity.effectiveFrom(),
                    entity.effectiveTo(),
                    segments.stream().map(ReferenceRangeSegmentResponse::from).toList(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
