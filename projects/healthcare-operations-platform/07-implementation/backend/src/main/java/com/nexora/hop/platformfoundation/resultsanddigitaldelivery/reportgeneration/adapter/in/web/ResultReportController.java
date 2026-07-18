package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.adapter.in.web;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.application.ResultReportService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReport;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for PDF Report Generation history and regeneration (BCM-RES-002).
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results/{resultId}/reports")
class ResultReportController {

    private final ResultReportService service;

    ResultReportController(ResultReportService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<GeneratedResultReportView>> listReports(
            @PathVariable String resultId,
            @RequestParam @NotBlank String tenantId) {
        List<GeneratedResultReportView> views =
                service.listReports(resultId, tenantId).stream().map(GeneratedResultReportView::from).toList();
        return ResponseEntity.ok(views);
    }

    @PostMapping("/regenerate")
    ResponseEntity<GeneratedResultReportView> regenerateReport(
            @PathVariable String resultId,
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String actorId) {
        GeneratedResultReport report = service.regenerateReport(resultId, tenantId, actorId);
        return ResponseEntity.ok(GeneratedResultReportView.from(report));
    }

    /** Employee-portal-facing view of a generated report (BCM-RES-002 wire contract). */
    record GeneratedResultReportView(
            UUID reportId,
            String resultId,
            String tenantId,
            String status,
            UUID documentId,
            String integrityChecksum,
            LocalDateTime generatedAt,
            String generatedBy) {

        static GeneratedResultReportView from(GeneratedResultReport report) {
            return new GeneratedResultReportView(
                    report.getReportId(),
                    report.getResultId().value(),
                    report.getTenantId().value(),
                    report.getStatus().name().toLowerCase(java.util.Locale.ROOT),
                    report.getStoredDocumentId(),
                    report.getIntegrityChecksum(),
                    report.getAudit().createdAt(),
                    report.getAudit().createdBy());
        }
    }
}
