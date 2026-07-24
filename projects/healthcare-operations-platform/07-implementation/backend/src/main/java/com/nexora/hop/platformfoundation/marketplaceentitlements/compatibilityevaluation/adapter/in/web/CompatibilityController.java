package com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;

/** Rendered controller for bcm-plt-011/openapi-source.yaml {@code /compatibility/evaluate}. */
@RestController
@RequestMapping("/api/marketplace/compatibility")
class CompatibilityController {

    private final CompatibilityEvaluator evaluator;

    CompatibilityController(CompatibilityEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @PostMapping("/evaluate")
    ResponseEntity<CompatibilityResponse> evaluateCompatibility(@Valid @RequestBody EvaluateRequest request) {
        return ResponseEntity.ok(CompatibilityResponse.from(evaluator.evaluate(request.version())));
    }

    record EvaluateRequest(@NotBlank String packageId, @NotBlank String version) {
    }

    record CompatibilityResponse(String decision, String effect, String reason) {
        static CompatibilityResponse from(CompatibilityDecision decision) {
            return new CompatibilityResponse(decision.decision(), decision.effect(), decision.reason());
        }
    }
}
