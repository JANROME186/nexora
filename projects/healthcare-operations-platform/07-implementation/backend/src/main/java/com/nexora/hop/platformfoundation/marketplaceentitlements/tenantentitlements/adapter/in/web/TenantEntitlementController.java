package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application.TenantEntitlementService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;

/** Rendered controller for bcm-plt-011/openapi-source.md {@code /tenants/{tenantId}/entitlements}. */
@RestController
@RequestMapping("/api/marketplace/entitlements/{tenantId}")
class TenantEntitlementController {

    private final TenantEntitlementService service;

    TenantEntitlementController(TenantEntitlementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<EntitlementResponse>> listTenantEntitlements(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.listTenantEntitlements(tenantId).stream().map(EntitlementResponse::from).toList());
    }

    @PostMapping
    ResponseEntity<EntitlementResponse> grantEntitlement(
            @PathVariable String tenantId, @Valid @RequestBody GrantEntitlementRequest request) {
        TenantEntitlement granted = service.grantEntitlement(
                tenantId, request.packageId(), request.offerId(), request.expiresAt(), request.usageLimit(),
                request.actorId());
        return ResponseEntity.created(
                        URI.create("/api/marketplace/entitlements/" + tenantId + "/" + granted.entitlementId()))
                .body(EntitlementResponse.from(granted));
    }

    @PostMapping("/{entitlementId}/revoke")
    ResponseEntity<EntitlementResponse> revokeEntitlement(
            @PathVariable String tenantId, @PathVariable String entitlementId,
            @Valid @RequestBody RevokeEntitlementRequest request) {
        return ResponseEntity.ok(EntitlementResponse.from(
                service.revokeEntitlement(tenantId, entitlementId, request.reason(), request.actorId())));
    }

    record GrantEntitlementRequest(
            @NotBlank String packageId, String offerId, LocalDateTime expiresAt, Integer usageLimit,
            @NotBlank String actorId) {
    }

    record RevokeEntitlementRequest(@NotBlank String reason, @NotBlank String actorId) {
    }

    record EntitlementResponse(
            String entitlementId, String tenantId, String packageId, String offerId, String status,
            Instant grantedAt, Instant expiresAt, String revokedReason, Integer usageLimit) {
        static EntitlementResponse from(TenantEntitlement entity) {
            return new EntitlementResponse(
                    entity.entitlementId(), entity.tenantId(), entity.packageId(), entity.offerId(), entity.status(),
                    entity.grantedAt().atZone(ZoneOffset.UTC).toInstant(),
                    entity.expiresAt() == null ? null : entity.expiresAt().atZone(ZoneOffset.UTC).toInstant(),
                    entity.revokedReason(), entity.usageLimit());
        }
    }
}
