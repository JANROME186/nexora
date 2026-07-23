package com.nexora.hop.platformfoundation.platformconfiguration.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.platformconfiguration.application.PlatformConfigurationService;
import com.nexora.hop.platformfoundation.platformconfiguration.application.UpdateFeatureFlagCommand;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.ConfigParameter;
import com.nexora.hop.platformfoundation.platformconfiguration.domain.FeatureFlag;

/** BCM-PLT-002 Platform Configuration and Feature Flags (COM-MOD-012-BE-001). */
@RestController
@RequestMapping("/api/platform")
class PlatformConfigurationController {

    private final PlatformConfigurationService service;

    PlatformConfigurationController(PlatformConfigurationService service) {
        this.service = service;
    }

    @GetMapping("/config")
    ResponseEntity<List<ConfigParameterResponse>> getPlatformConfig() {
        return ResponseEntity.ok(service.getConfig().stream().map(ConfigParameterResponse::from).toList());
    }

    @GetMapping("/feature-flags")
    ResponseEntity<Map<String, Boolean>> evaluateFeatureFlags(
            @RequestParam(required = false) String tenantId) {
        return ResponseEntity.ok(service.evaluateFeatureFlags(tenantId));
    }

    @PostMapping("/feature-flags")
    ResponseEntity<FeatureFlagResponse> updateFeatureFlag(@Valid @RequestBody UpdateFeatureFlagRequest request) {
        FeatureFlag flag = service.updateFeatureFlag(new UpdateFeatureFlagCommand(
                request.flagKey(),
                request.enabledByDefault(),
                request.targetTenants(),
                request.rolloutPercentage(),
                request.updatedBy()));
        return ResponseEntity.ok(FeatureFlagResponse.from(flag));
    }

    record UpdateFeatureFlagRequest(
            @NotBlank String flagKey,
            boolean enabledByDefault,
            List<String> targetTenants,
            Integer rolloutPercentage,
            @NotBlank String updatedBy) {
    }

    record ConfigParameterResponse(String key, String valueType, String value, boolean tenantOverrideAllowed) {
        static ConfigParameterResponse from(ConfigParameter parameter) {
            return new ConfigParameterResponse(
                    parameter.key(), parameter.valueType(), parameter.rawValue(), parameter.tenantOverrideAllowed());
        }
    }

    record FeatureFlagResponse(
            String flagKey,
            boolean enabledByDefault,
            List<String> targetTenants,
            int rolloutPercentage,
            Instant updatedAt,
            String updatedBy) {
        static FeatureFlagResponse from(FeatureFlag flag) {
            return new FeatureFlagResponse(
                    flag.flagKey(),
                    flag.enabledByDefault(),
                    flag.targetTenants(),
                    flag.rolloutPercentage(),
                    flag.updatedAt(),
                    flag.updatedBy());
        }
    }
}
