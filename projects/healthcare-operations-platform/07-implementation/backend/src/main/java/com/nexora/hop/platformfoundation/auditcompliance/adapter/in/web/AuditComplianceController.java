package com.nexora.hop.platformfoundation.auditcompliance.adapter.in.web;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.auditcompliance.application.AuditComplianceService;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;

@RestController
@RequestMapping("/api/audit")
class AuditComplianceController {

    private final AuditComplianceService service;

    AuditComplianceController(AuditComplianceService service) {
        this.service = service;
    }

    @GetMapping("/events")
    ResponseEntity<List<AuditEventResponse>> searchEvents(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String subjectId) {
        List<AuditEventResponse> events = service.searchEvents(tenantId, subjectId).stream()
                .map(AuditEventResponse::from)
                .toList();
        return ResponseEntity.ok(events);
    }

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
