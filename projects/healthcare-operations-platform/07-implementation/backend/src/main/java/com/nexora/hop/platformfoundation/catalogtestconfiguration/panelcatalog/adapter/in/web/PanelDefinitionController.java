package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.CreatePanelDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.PanelCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.PanelMemberInput;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.UpdatePanelDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelMember;

/** Rendered controller for bcm-svc-003-panel-catalog/openapi-source.md (base path /api/catalog/panels). */
@RestController
@RequestMapping("/api/catalog/panels")
class PanelDefinitionController {

    private final PanelCatalogService service;

    PanelDefinitionController(PanelCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PanelDefinitionResponse>> listPanels(@RequestParam String laboratoryId) {
        List<PanelDefinitionResponse> body = service.list(laboratoryId).stream()
                .map(panel -> PanelDefinitionResponse.from(panel, service.getMembers(panel.panelId())))
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{panelId}")
    ResponseEntity<PanelDefinitionResponse> getPanel(@PathVariable String panelId) {
        return ResponseEntity.ok(PanelDefinitionResponse.from(service.get(panelId), service.getMembers(panelId)));
    }

    @PostMapping
    ResponseEntity<PanelDefinitionResponse> createPanel(@Valid @RequestBody CreatePanelDefinitionRequest request) {
        PanelDefinition created = service.create(new CreatePanelDefinitionCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                toInputs(request.members())));
        return ResponseEntity.created(URI.create("/api/catalog/panels/" + created.panelId()))
                .body(PanelDefinitionResponse.from(created, service.getMembers(created.panelId())));
    }

    @PutMapping("/{panelId}")
    ResponseEntity<PanelDefinitionResponse> updatePanel(
            @PathVariable String panelId, @Valid @RequestBody UpdatePanelDefinitionRequest request) {
        PanelDefinition updated = service.update(panelId, new UpdatePanelDefinitionCommand(
                request.code(), request.nameEn(), request.nameEs(), toInputs(request.members())));
        return ResponseEntity.ok(PanelDefinitionResponse.from(updated, service.getMembers(panelId)));
    }

    @PostMapping("/{panelId}/publish")
    ResponseEntity<PanelDefinitionResponse> publishPanel(@PathVariable String panelId) {
        PanelDefinition published = service.publish(panelId);
        return ResponseEntity.ok(PanelDefinitionResponse.from(published, service.getMembers(panelId)));
    }

    @PostMapping("/{panelId}/deprecate")
    ResponseEntity<PanelDefinitionResponse> deprecatePanel(@PathVariable String panelId) {
        PanelDefinition deprecated = service.deprecate(panelId);
        return ResponseEntity.ok(PanelDefinitionResponse.from(deprecated, service.getMembers(panelId)));
    }

    @GetMapping("/{panelId}/published-snapshot")
    ResponseEntity<PanelDefinitionResponse> getPublishedPanelSnapshot(@PathVariable String panelId) {
        PanelDefinition snapshot = service.getPublishedSnapshot(panelId);
        return ResponseEntity.ok(PanelDefinitionResponse.from(snapshot, service.getMembers(panelId)));
    }

    private static List<PanelMemberInput> toInputs(List<PanelMemberRequest> members) {
        if (members == null) {
            return List.of();
        }
        return members.stream()
                .map(member -> new PanelMemberInput(member.testRefId(), member.displayOrder(), member.mandatory()))
                .toList();
    }

    record PanelMemberRequest(@NotBlank String testRefId, Integer displayOrder, boolean mandatory) {
    }

    record CreatePanelDefinitionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            List<PanelMemberRequest> members) {
    }

    record UpdatePanelDefinitionRequest(
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            List<PanelMemberRequest> members) {
    }

    record PanelMemberResponse(String memberId, String testRefId, Integer displayOrder, boolean mandatory) {
        static PanelMemberResponse from(PanelMember member) {
            return new PanelMemberResponse(member.memberId(), member.testRefId(), member.displayOrder(), member.mandatory());
        }
    }

    record PanelDefinitionResponse(
            String panelId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String status,
            int version,
            List<PanelMemberResponse> members,
            Instant createdAt,
            Instant updatedAt) {
        static PanelDefinitionResponse from(PanelDefinition entity, List<PanelMember> members) {
            return new PanelDefinitionResponse(
                    entity.panelId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.status(),
                    entity.version(),
                    members.stream().map(PanelMemberResponse::from).toList(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
