package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.application.ApiManagementService;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKey;

/** Rendered controller for bcm-plt-005-api-management/openapi-source.yaml (PartnerApiKey resource). */
@RestController
@RequestMapping("/api/platform/api-management/partner-keys")
class PartnerApiKeyController {

    private final ApiManagementService service;

    PartnerApiKeyController(ApiManagementService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<PartnerApiKeyResponse> issuePartnerApiKey(@Valid @RequestBody IssueKeyRequest request) {
        PartnerApiKey created = service.issuePartnerApiKey(
                request.tenantId(), request.consumerName(), request.grantedScopes(), request.actorId());
        return ResponseEntity.created(URI.create("/api/platform/api-management/partner-keys/" + created.keyId()))
                .body(PartnerApiKeyResponse.from(created));
    }

    @PostMapping("/{keyId}/revoke")
    ResponseEntity<PartnerApiKeyResponse> revokePartnerApiKey(
            @PathVariable String keyId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.ok(PartnerApiKeyResponse.from(service.revokePartnerApiKey(keyId, actorId)));
    }

    @GetMapping
    ResponseEntity<List<PartnerApiKeyResponse>> listPartnerApiKeys(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.listPartnerApiKeys(tenantId).stream().map(PartnerApiKeyResponse::from).toList());
    }

    record ActorRequest(String actorId) {
    }

    record IssueKeyRequest(
            @NotBlank String tenantId, @NotBlank String consumerName, @NotEmpty List<String> grantedScopes,
            @NotBlank String actorId) {
    }

    record PartnerApiKeyResponse(
            String keyId, String tenantId, String consumerName, List<String> grantedScopes,
            String rateLimitPolicyRef, String status) {
        static PartnerApiKeyResponse from(PartnerApiKey entity) {
            return new PartnerApiKeyResponse(
                    entity.keyId(), entity.tenantId(), entity.consumerName(), entity.grantedScopes(),
                    entity.rateLimitPolicyRef(), entity.status());
        }
    }
}
