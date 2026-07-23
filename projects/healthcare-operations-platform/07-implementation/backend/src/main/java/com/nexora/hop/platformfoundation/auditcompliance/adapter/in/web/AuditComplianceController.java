package com.nexora.hop.platformfoundation.auditcompliance.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.auditcompliance.application.AuditComplianceService;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;

@RestController
@RequestMapping("/api/audit")
public class AuditComplianceController {

    private final AuditComplianceService service;

    public AuditComplianceController(AuditComplianceService service) {
        this.service = service;
    }

    @GetMapping("/events")
    ResponseEntity<List<AuditEventResponse>> searchEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String complianceCorrelationId,
            @RequestParam(required = false) String qualityInvestigationId,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant toDate) {
        List<AuditEventResponse> events = service.searchEventsFiltered(
                tenantId, subjectId, category, complianceCorrelationId, qualityInvestigationId, fromDate, toDate).stream()
                .map(AuditEventResponse::from)
                .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/events/export")
    ResponseEntity<ExportAuditEventsResponse> exportAuditEvents(
            @RequestBody(required = false) ExportAuditEventsRequest request) {
        String category = request != null ? request.category() : null;
        String correlationId = request != null ? request.complianceCorrelationId() : null;
        Instant from = request != null ? request.fromDate() : null;
        Instant to = request != null ? request.toDate() : null;
        String format = request != null ? request.format() : "csv";

        AuditComplianceService.AuditExportResult result = service.exportAuditEvents(category, correlationId, from, to, format);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ExportAuditEventsResponse(
                result.exportId(),
                result.recordCount(),
                result.storedDocumentId(),
                result.generatedAt()
        ));
    }

    public record ExportAuditEventsRequest(
            String category,
            String complianceCorrelationId,
            Instant fromDate,
            Instant toDate,
            String format
    ) {}

    record ExportAuditEventsResponse(
            UUID exportId,
            int recordCount,
            UUID storedDocumentId,
            Instant generatedAt
    ) {}

    record AuditEventResponse(
            String auditEventId,
            Instant occurredAt,
            String tenantId,
            String actorId,
            String actorType,
            String action,
            String subjectType,
            String subjectId,
            String metadataJson) {
        static AuditEventResponse from(AuditEvent event) {
            return new AuditEventResponse(
                    event.auditEventId(),
                    event.occurredAt(),
                    event.tenantId(),
                    event.actorId(),
                    event.actorType(),
                    event.action(),
                    event.subjectType(),
                    event.subjectId(),
                    event.metadataJson());
        }
    }
}
