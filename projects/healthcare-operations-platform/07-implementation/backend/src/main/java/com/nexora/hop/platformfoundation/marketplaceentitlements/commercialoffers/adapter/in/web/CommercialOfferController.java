package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
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

import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.application.CommercialOfferService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;

/** Rendered controller for bcm-plt-011/openapi-source.yaml {@code /offers}. */
@RestController
@RequestMapping("/api/marketplace/offers")
class CommercialOfferController {

    private final CommercialOfferService service;

    CommercialOfferController(CommercialOfferService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<OfferResponse> publishOffer(@Valid @RequestBody PublishOfferRequest request) {
        CommercialOffer created = service.publishOffer(
                request.packageId(), request.packageVersion(), request.offerCode(), request.offerType(),
                request.tierCodes(), request.trialPeriodDays(), request.billingEventRulesSummary(),
                request.actorId());
        return ResponseEntity.created(URI.create("/api/marketplace/offers/" + created.offerId()))
                .body(OfferResponse.from(created));
    }

    @GetMapping
    ResponseEntity<List<OfferResponse>> listOffers(@RequestParam(required = false) String packageId) {
        return ResponseEntity.ok(service.listOffers(packageId).stream().map(OfferResponse::from).toList());
    }

    @PostMapping("/{offerId}/accept")
    ResponseEntity<AcceptOfferResponse> acceptOffer(
            @PathVariable String offerId, @Valid @RequestBody AcceptOfferRequest request) {
        TenantEntitlement granted = service.acceptOffer(offerId, request.tenantId(), request.actorId());
        return ResponseEntity.ok(new AcceptOfferResponse(
                offerId, request.tenantId(), granted.entitlementId(),
                granted.grantedAt().atZone(ZoneOffset.UTC).toInstant()));
    }

    record PublishOfferRequest(
            @NotBlank String packageId, @NotBlank String packageVersion, @NotBlank String offerCode,
            @NotBlank String offerType, List<String> tierCodes, Integer trialPeriodDays,
            String billingEventRulesSummary, @NotBlank String actorId) {
    }

    record AcceptOfferRequest(@NotBlank String tenantId, @NotBlank String actorId) {
    }

    record OfferResponse(
            String offerId, String packageId, String packageVersion, String offerCode, String offerType,
            String lifecycleStatus, List<String> tierCodes, Integer trialPeriodDays, int effectiveVersion) {
        static OfferResponse from(CommercialOffer entity) {
            return new OfferResponse(
                    entity.offerId(), entity.packageId(), entity.packageVersion(), entity.offerCode(),
                    entity.offerType(), entity.lifecycleStatus(), entity.tierCodes(), entity.trialPeriodDays(),
                    entity.effectiveVersion());
        }
    }

    record AcceptOfferResponse(String offerId, String tenantId, String entitlementId, Instant grantedAt) {
    }
}
