package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.application.PacsIntegrationService;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpoint;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PacsIntegrationController {

    private final PacsIntegrationService service;

    public PacsIntegrationController(PacsIntegrationService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-005")
    public ResponseEntity<Map<String, Object>> getBcmImg005Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-005",
                "name", "PACS Integration",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/pacs-endpoints")
    public ResponseEntity<PacsIntegrationEndpoint> registerEndpoint(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody RegisterPacsEndpointRequest request) {
        PacsIntegrationEndpoint endpoint = service.registerEndpoint(
                tenantId, request.pacsNodeId(), request.baseUrl(), request.protocol(), actorId
        );
        return ResponseEntity.ok(endpoint);
    }

    @GetMapping("/api/v1/imaging/pacs-endpoints/{endpointId}")
    public ResponseEntity<PacsIntegrationEndpoint> getEndpoint(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String endpointId) {
        return ResponseEntity.ok(service.getEndpoint(tenantId, endpointId));
    }

    @GetMapping("/api/v1/imaging/pacs-endpoints")
    public ResponseEntity<List<PacsIntegrationEndpoint>> listEndpoints(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(service.listEndpoints(tenantId));
    }

    @GetMapping("/api/v1/imaging/pacs-endpoints/{endpointId}/query")
    public ResponseEntity<Map<String, String>> queryPacs(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String endpointId,
            @RequestParam String accessionNumber) {
        String result = service.queryStudy(tenantId, endpointId, accessionNumber);
        return ResponseEntity.ok(Map.of("result", result));
    }

    public record RegisterPacsEndpointRequest(
            String pacsNodeId,
            String baseUrl,
            String protocol
    ) {}
}
