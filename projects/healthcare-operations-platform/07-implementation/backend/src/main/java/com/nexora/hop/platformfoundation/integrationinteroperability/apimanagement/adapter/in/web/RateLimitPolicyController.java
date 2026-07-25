package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.application.ApiManagementService;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;

/** Rendered controller for bcm-plt-005-api-management/openapi-source.md (RateLimitPolicy resource). */
@RestController
@RequestMapping("/api/platform/api-management/rate-limit-policies")
class RateLimitPolicyController {

    private final ApiManagementService service;

    RateLimitPolicyController(ApiManagementService service) {
        this.service = service;
    }

    @PutMapping("/{classification}")
    ResponseEntity<RateLimitPolicyResponse> setRateLimitPolicy(
            @PathVariable String classification, @Valid @RequestBody SetPolicyRequest request) {
        RateLimitPolicy saved = service.setRateLimitPolicy(
                classification, request.requestsPerMinute(), request.consumerIdentificationMethod(), request.actorId());
        return ResponseEntity.ok(RateLimitPolicyResponse.from(saved));
    }

    record SetPolicyRequest(
            @Positive int requestsPerMinute, String consumerIdentificationMethod, @NotBlank String actorId) {
    }

    record RateLimitPolicyResponse(
            String policyId, String classification, int requestsPerMinute, String consumerIdentificationMethod) {
        static RateLimitPolicyResponse from(RateLimitPolicy entity) {
            return new RateLimitPolicyResponse(entity.policyId(), entity.classification(),
                    entity.requestsPerMinute(), entity.consumerIdentificationMethod());
        }
    }
}
