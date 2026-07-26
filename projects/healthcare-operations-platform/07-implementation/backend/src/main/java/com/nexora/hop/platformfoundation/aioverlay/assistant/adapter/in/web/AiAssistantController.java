package com.nexora.hop.platformfoundation.aioverlay.assistant.adapter.in.web;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.aioverlay.assistant.application.AiAssistantService;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;

@RestController
@RequestMapping("/api/ai/assistant/sessions")
class AiAssistantController {

    private static final String SESSIONS_PATH = "/api/ai/assistant/sessions/";
    private static final String TENANT_HEADER = "X-Tenant-Id";

    private final AiAssistantService service;

    AiAssistantController(AiAssistantService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<AiInteractionResponse> requestDraft(
            @RequestHeader(TENANT_HEADER) String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @Valid @RequestBody AssistantRequest request) {
        AiInteraction created = service.requestAssistantDraft(
                tenantId, actorId, request.purpose(), request.sourceContextType(),
                request.sourceContextId(), request.prompt());
        return ResponseEntity.created(URI.create(SESSIONS_PATH + created.sessionId()))
                .body(AiInteractionResponse.from(created));
    }

    @GetMapping("/{sessionId}")
    ResponseEntity<AiInteractionResponse> getSession(
            @RequestHeader(TENANT_HEADER) String tenantId, @PathVariable String sessionId) {
        return ResponseEntity.ok(AiInteractionResponse.from(service.getSession(tenantId, sessionId)));
    }

    @PostMapping("/{sessionId}/review")
    ResponseEntity<AiInteractionResponse> reviewDraft(
            @RequestHeader(TENANT_HEADER) String tenantId,
            @PathVariable String sessionId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(AiInteractionResponse.from(
                service.reviewDraft(tenantId, sessionId, request.reviewerId(), request.decision(), request.reason())));
    }

    @GetMapping("/audit-records")
    ResponseEntity<List<AiInteractionResponse>> listAuditRecords(@RequestHeader(TENANT_HEADER) String tenantId) {
        return ResponseEntity.ok(service.listAuditRecords(tenantId).stream()
                .map(AiInteractionResponse::from)
                .toList());
    }

    record AssistantRequest(
            @NotBlank String purpose,
            @NotBlank String sourceContextType,
            @NotBlank String sourceContextId,
            @NotBlank String prompt) {
    }

    record ReviewRequest(@NotBlank String reviewerId, @NotBlank String decision, @NotBlank String reason) {
    }

    record AiInteractionResponse(
            String sessionId,
            String tenantId,
            String actorId,
            String purpose,
            String sourceContextType,
            String sourceContextId,
            String draftOutput,
            List<String> citations,
            String confidenceBand,
            String safetyDecision,
            String reviewStatus,
            String reviewerId,
            String reviewReason,
            String modelProviderRef,
            String modelNameRef,
            String policyVersion,
            String lifecycleStatus,
            Instant createdAt,
            Instant updatedAt) {

        static AiInteractionResponse from(AiInteraction interaction) {
            return new AiInteractionResponse(
                    interaction.sessionId(),
                    interaction.tenantId(),
                    interaction.actorId(),
                    interaction.purpose(),
                    interaction.sourceContextType(),
                    interaction.sourceContextId(),
                    interaction.draftOutput(),
                    interaction.citations(),
                    interaction.confidenceBand(),
                    interaction.safetyDecision(),
                    interaction.reviewStatus(),
                    interaction.reviewerId(),
                    interaction.reviewReason(),
                    interaction.modelProviderRef(),
                    interaction.modelNameRef(),
                    interaction.policyVersion(),
                    interaction.lifecycleStatus(),
                    interaction.audit().createdAt().atZone(ZoneOffset.UTC).toInstant(),
                    interaction.audit().updatedAt().atZone(ZoneOffset.UTC).toInstant());
        }
    }
}
