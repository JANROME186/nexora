package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.in.web;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.application.ResultDeliveryService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/results/delivery")
public class ResultDeliveryController {

    private final ResultDeliveryService service;

    public ResultDeliveryController(ResultDeliveryService service) {
        this.service = service;
    }

    @PostMapping("/authorize")
    public ResponseEntity<List<ResultDeliveryTicket>> authorizeDelivery(
            @RequestParam String resultId,
            @RequestParam String tenantId,
            @RequestParam String actorId) {

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());
        List<ResultDeliveryTicket> tickets = service.authorizeResultDelivery(resultId, tenantId, audit);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ResultDeliveryTicket> getDeliveredResult(
            @PathVariable UUID ticketId,
            @RequestParam String tenantId,
            @RequestParam String callerId,
            @RequestParam String actorId) {

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());
        ResultDeliveryTicket ticket = service.getDeliveredResult(ticketId, tenantId, callerId, audit);
        return ResponseEntity.ok(ticket);
    }
}
