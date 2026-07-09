package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.adapter.in.web;

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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application.CreateDiagnosticServiceCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application.DiagnosticServiceCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application.ServiceComponentLinkInput;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application.UpdateDiagnosticServiceCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.ServiceComponentLink;

/**
 * Rendered controller for bcm-svc-001-diagnostic-service-catalog/openapi-source.yaml
 * (base path /api/catalog/diagnostic-services).
 */
@RestController
@RequestMapping("/api/catalog/diagnostic-services")
class DiagnosticServiceController {

    private final DiagnosticServiceCatalogService service;

    DiagnosticServiceController(DiagnosticServiceCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<DiagnosticServiceResponse>> listDiagnosticServices(@RequestParam String laboratoryId) {
        List<DiagnosticServiceResponse> body = service.list(laboratoryId).stream()
                .map(DiagnosticServiceResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{serviceId}")
    ResponseEntity<DiagnosticServiceResponse> getDiagnosticService(@PathVariable String serviceId) {
        DiagnosticService found = service.get(serviceId);
        return ResponseEntity.ok(DiagnosticServiceResponse.from(found, service.getComponents(serviceId)));
    }

    @PostMapping
    ResponseEntity<DiagnosticServiceResponse> createDiagnosticService(
            @Valid @RequestBody CreateDiagnosticServiceRequest request) {
        DiagnosticService created = service.create(new CreateDiagnosticServiceCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                request.categoryId(), request.serviceType(), toInputs(request.components())));
        return ResponseEntity.created(URI.create("/api/catalog/diagnostic-services/" + created.serviceId()))
                .body(DiagnosticServiceResponse.from(created, service.getComponents(created.serviceId())));
    }

    @PutMapping("/{serviceId}")
    ResponseEntity<DiagnosticServiceResponse> updateDiagnosticService(
            @PathVariable String serviceId,
            @Valid @RequestBody UpdateDiagnosticServiceRequest request) {
        DiagnosticService updated = service.update(serviceId, new UpdateDiagnosticServiceCommand(
                request.code(), request.nameEn(), request.nameEs(), request.categoryId(), request.serviceType(),
                toInputs(request.components())));
        return ResponseEntity.ok(DiagnosticServiceResponse.from(updated, service.getComponents(serviceId)));
    }

    @PostMapping("/{serviceId}/publish")
    ResponseEntity<DiagnosticServiceResponse> publishDiagnosticService(@PathVariable String serviceId) {
        DiagnosticService published = service.publish(serviceId);
        return ResponseEntity.ok(DiagnosticServiceResponse.from(published, service.getComponents(serviceId)));
    }

    @PostMapping("/{serviceId}/deprecate")
    ResponseEntity<DiagnosticServiceResponse> deprecateDiagnosticService(@PathVariable String serviceId) {
        DiagnosticService deprecated = service.deprecate(serviceId);
        return ResponseEntity.ok(DiagnosticServiceResponse.from(deprecated, service.getComponents(serviceId)));
    }

    @GetMapping("/{serviceId}/published-snapshot")
    ResponseEntity<DiagnosticServiceResponse> getPublishedServiceSnapshot(@PathVariable String serviceId) {
        DiagnosticService snapshot = service.getPublishedSnapshot(serviceId);
        return ResponseEntity.ok(DiagnosticServiceResponse.from(snapshot, service.getComponents(serviceId)));
    }

    private static List<ServiceComponentLinkInput> toInputs(List<ServiceComponentLinkRequest> components) {
        if (components == null) {
            return List.of();
        }
        return components.stream()
                .map(component -> new ServiceComponentLinkInput(
                        component.componentType(), component.componentRefId(), component.displayOrder()))
                .toList();
    }

    record ServiceComponentLinkRequest(
            @NotBlank String componentType,
            @NotBlank String componentRefId,
            Integer displayOrder) {
    }

    record CreateDiagnosticServiceRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String categoryId,
            @NotBlank String serviceType,
            List<ServiceComponentLinkRequest> components) {
    }

    record UpdateDiagnosticServiceRequest(
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String categoryId,
            @NotBlank String serviceType,
            List<ServiceComponentLinkRequest> components) {
    }

    record ServiceComponentLinkResponse(String linkId, String componentType, String componentRefId, Integer displayOrder) {
        static ServiceComponentLinkResponse from(ServiceComponentLink link) {
            return new ServiceComponentLinkResponse(
                    link.linkId(), link.componentType(), link.componentRefId(), link.displayOrder());
        }
    }

    record DiagnosticServiceResponse(
            String serviceId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String categoryId,
            String serviceType,
            String status,
            int version,
            List<ServiceComponentLinkResponse> components,
            Instant createdAt,
            Instant updatedAt) {
        static DiagnosticServiceResponse from(DiagnosticService entity, List<ServiceComponentLink> links) {
            return new DiagnosticServiceResponse(
                    entity.serviceId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.categoryId(),
                    entity.serviceType(),
                    entity.status(),
                    entity.version(),
                    links.stream().map(ServiceComponentLinkResponse::from).toList(),
                    entity.createdAt(),
                    entity.updatedAt());
        }

        static DiagnosticServiceResponse from(DiagnosticService entity) {
            return from(entity, List.of());
        }
    }
}
