package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.application.BillingEventService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecord;

/** Rendered controller for bcm-plt-011/openapi-source.md {@code /billing/events}. */
@RestController
@RequestMapping("/api/marketplace/billing")
class BillingEventController {

    private final BillingEventService service;

    BillingEventController(BillingEventService service) {
        this.service = service;
    }

    @PostMapping("/events")
    ResponseEntity<BillingEventResponse> publishBillingEvent(@Valid @RequestBody PublishBillingEventRequest request) {
        BillingEventRecord created = service.publishBillingEvent(
                request.tenantId(), request.entitlementId(), request.eventType(), request.amountMinorUnits(),
                request.currency(), request.providerReference(), request.actorId());
        return ResponseEntity.created(URI.create("/api/marketplace/billing/events/" + created.billingEventId()))
                .body(BillingEventResponse.from(created));
    }

    record PublishBillingEventRequest(
            @NotBlank String tenantId, String entitlementId, @NotBlank String eventType, long amountMinorUnits,
            String currency, String providerReference, @NotBlank String actorId) {
    }

    record BillingEventResponse(
            String billingEventId, String tenantId, String entitlementId, String eventType, long amountMinorUnits,
            String currency, String providerReference, String adapterStatus, Instant createdAt) {
        static BillingEventResponse from(BillingEventRecord entity) {
            return new BillingEventResponse(
                    entity.billingEventId(), entity.tenantId(), entity.entitlementId(), entity.eventType(),
                    entity.amountMinorUnits(), entity.currency(), entity.providerReference(), entity.adapterStatus(),
                    entity.audit().createdAt().atZone(ZoneOffset.UTC).toInstant());
        }
    }
}
