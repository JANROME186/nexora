package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.adapter.in.web;

import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.application.RadiologySignatureService;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
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
public class RadiologySignatureController {

    private final RadiologySignatureService service;

    public RadiologySignatureController(RadiologySignatureService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/imaging/bcm-img-007")
    public ResponseEntity<Map<String, Object>> getBcmImg007Status() {
        return ResponseEntity.ok(Map.of(
                "capability", "BCM-IMG-007",
                "name", "Radiology Signature",
                "status", "active"
        ));
    }

    @PostMapping("/api/v1/imaging/reports")
    public ResponseEntity<RadiologyReport> createReport(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @RequestBody CreateReportRequest request) {
        RadiologyReport report = service.createDraftReport(
                tenantId, request.studyId(), request.findingsText(), request.impressionText(), actorId
        );
        return ResponseEntity.ok(report);
    }

    @PostMapping("/api/v1/imaging/reports/{reportId}/sign")
    public ResponseEntity<RadiologyReport> signReport(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String actorId,
            @PathVariable String reportId) {
        RadiologyReport signed = service.signReport(tenantId, reportId, actorId);
        return ResponseEntity.ok(signed);
    }

    @GetMapping("/api/v1/imaging/reports/{reportId}")
    public ResponseEntity<RadiologyReport> getReport(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String reportId) {
        return ResponseEntity.ok(service.getReport(tenantId, reportId));
    }

    @GetMapping("/api/v1/imaging/reports")
    public ResponseEntity<List<RadiologyReport>> listReportsForStudy(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam String studyId) {
        return ResponseEntity.ok(service.listReportsForStudy(tenantId, studyId));
    }

    public record CreateReportRequest(
            String studyId,
            String findingsText,
            String impressionText
    ) {}
}
