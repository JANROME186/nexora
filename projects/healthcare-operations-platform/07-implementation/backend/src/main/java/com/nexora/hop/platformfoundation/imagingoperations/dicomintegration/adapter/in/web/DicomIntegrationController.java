package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.application.DicomIntegrationService;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfiguration;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DicomIntegrationController {

    private final DicomIntegrationService service;

    public DicomIntegrationController(DicomIntegrationService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-004")
    public ResponseEntity<Map<String, Object>> getBcmImg004Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-004",
                "name", "DICOM Integration",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/dicom-configs")
    public ResponseEntity<DicomAdapterConfiguration> registerConfig(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody RegisterDicomConfigRequest request) {
        DicomAdapterConfiguration config = service.registerConfiguration(
                tenantId, request.aeTitle(), request.host(), request.port(),
                request.modalityType(), actorId
        );
        return ResponseEntity.ok(config);
    }

    @GetMapping("/api/v1/imaging/dicom-configs/{configurationId}")
    public ResponseEntity<DicomAdapterConfiguration> getConfig(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String configurationId) {
        return ResponseEntity.ok(service.getConfiguration(tenantId, configurationId));
    }

    @GetMapping("/api/v1/imaging/dicom-configs")
    public ResponseEntity<List<DicomAdapterConfiguration>> listConfigs(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(service.listConfigurations(tenantId));
    }

    @PostMapping("/api/v1/imaging/dicom-configs/{configurationId}/echo")
    public ResponseEntity<Map<String, String>> echoCEcho(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String configurationId) {
        String result = service.testCEcho(tenantId, configurationId);
        return ResponseEntity.ok(Map.of("result", result));
    }

    public record RegisterDicomConfigRequest(
            String aeTitle,
            String host,
            int port,
            String modalityType
    ) {}
}
