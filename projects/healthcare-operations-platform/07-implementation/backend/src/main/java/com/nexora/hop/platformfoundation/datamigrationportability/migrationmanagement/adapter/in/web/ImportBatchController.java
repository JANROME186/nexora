package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application.MigrationManagementService;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;

/** Rendered controller for bcm-plt-010-open-data-ingestion-and-migration/openapi-source.md (DryRun/Commit resources). */
@RestController
@RequestMapping("/api/platform/migration/import-batches")
class ImportBatchController {

    private final MigrationManagementService service;

    ImportBatchController(MigrationManagementService service) {
        this.service = service;
    }

    @PostMapping("/{importBatchId}/dry-run")
    ResponseEntity<DryRunReportResponse> runDryRunValidation(
            @PathVariable String importBatchId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.ok(DryRunReportResponse.from(service.runDryRunValidation(importBatchId, actorId)));
    }

    @GetMapping("/{importBatchId}/dry-run")
    ResponseEntity<DryRunReportResponse> getDryRunReport(@PathVariable String importBatchId) {
        return ResponseEntity.ok(DryRunReportResponse.from(service.getDryRunReport(importBatchId)));
    }

    @PostMapping("/{importBatchId}/approve")
    ResponseEntity<JobStatusResponse> approveImport(
            @PathVariable String importBatchId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        MigrationJob job = service.approveImport(importBatchId, actorId);
        return ResponseEntity.ok(new JobStatusResponse(job.migrationJobId(), job.status()));
    }

    @PostMapping("/{importBatchId}/commit")
    ResponseEntity<ExecutionResponse> commitImport(
            @PathVariable String importBatchId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.accepted().body(ExecutionResponse.from(service.commitImport(importBatchId, actorId)));
    }

    record ActorRequest(String actorId) {
    }

    record JobStatusResponse(String migrationJobId, String status) {
    }

    record DryRunReportResponse(
            String reportId, String importBatchId, List<String> structuralErrors, List<String> rowLevelErrors,
            List<String> rowLevelWarnings, List<String> validationCategoriesEvaluated, boolean passed) {
        static DryRunReportResponse from(ImportValidationReport entity) {
            return new DryRunReportResponse(
                    entity.reportId(), entity.importBatchId(), entity.structuralErrors(), entity.rowLevelErrors(),
                    entity.rowLevelWarnings(), entity.validationCategoriesEvaluated(), entity.passed());
        }
    }

    record ExecutionResponse(
            String executionId, String migrationJobId, int attemptNumber, List<String> domainCommandsInvoked,
            String checkpoint, String status) {
        static ExecutionResponse from(ImportExecution entity) {
            return new ExecutionResponse(
                    entity.executionId(), entity.migrationJobId(), entity.attemptNumber(),
                    entity.domainCommandsInvoked(), entity.checkpoint(), entity.status());
        }
    }
}
