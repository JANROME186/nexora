package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.adapter.in.web;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.application.CriticalResultEscalationService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.CriticalResultEscalation;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/results/critical-escalations")
public class CriticalResultEscalationController {

    private final CriticalResultEscalationService service;

    public CriticalResultEscalationController(CriticalResultEscalationService service) {
        this.service = service;
    }

    @PostMapping("/{escalationId}/acknowledge")
    public ResponseEntity<CriticalResultEscalation> acknowledge(
            @PathVariable UUID escalationId,
            @RequestParam String userId,
            @RequestParam String actorId) {

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());
        CriticalResultEscalation escalation = service.acknowledge(escalationId, userId, audit);
        return ResponseEntity.ok(escalation);
    }

    @PostMapping("/{escalationId}/close")
    public ResponseEntity<CriticalResultEscalation> close(
            @PathVariable UUID escalationId,
            @RequestParam String actorId) {

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());
        CriticalResultEscalation escalation = service.close(escalationId, audit);
        return ResponseEntity.ok(escalation);
    }

    @PostMapping("/{escalationId}/escalate")
    public ResponseEntity<CriticalResultEscalation> escalate(
            @PathVariable UUID escalationId,
            @RequestParam String actorId) {

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());
        CriticalResultEscalation escalation = service.escalate(escalationId, audit);
        return ResponseEntity.ok(escalation);
    }

    @GetMapping("/open")
    public ResponseEntity<List<CriticalResultEscalation>> getOpenEscalations(
            @RequestParam String tenantId) {
        List<CriticalResultEscalation> openList = service.listOpenEscalations(tenantId);
        return ResponseEntity.ok(openList);
    }
}
