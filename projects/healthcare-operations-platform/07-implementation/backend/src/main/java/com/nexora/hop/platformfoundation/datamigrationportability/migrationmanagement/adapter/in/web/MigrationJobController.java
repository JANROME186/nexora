package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.in.web;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application.MigrationManagementService;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatch;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.InvalidMigrationCommandException;

/** Rendered controller for bcm-plt-010-open-data-ingestion-and-migration/openapi-source.md (MigrationJob resource). */
@RestController
@RequestMapping("/api/platform/migration/jobs")
class MigrationJobController {

    private final MigrationManagementService service;

    MigrationJobController(MigrationManagementService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<JobResponse> createMigrationJob(@Valid @RequestBody CreateJobRequest request) {
        MigrationJob created = service.createMigrationJob(
                request.tenantId(), request.laboratoryId(), request.sourceSystemName(), request.actorId());
        return ResponseEntity.created(URI.create("/api/platform/migration/jobs/" + created.migrationJobId()))
                .body(JobResponse.from(created));
    }

    @GetMapping("/{migrationJobId}")
    ResponseEntity<JobResponse> getMigrationJob(@PathVariable String migrationJobId) {
        return ResponseEntity.ok(JobResponse.from(service.getMigrationJob(migrationJobId)));
    }

    @GetMapping
    ResponseEntity<List<JobResponse>> listMigrationJobs(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.listMigrationJobs(tenantId).stream().map(JobResponse::from).toList());
    }

    @PostMapping(value = "/{migrationJobId}/import-batches", consumes = "multipart/form-data")
    ResponseEntity<ImportBatchResponse> receiveImportPackage(
            @PathVariable String migrationJobId,
            @RequestPart("manifest") MultipartFile manifest,
            @RequestPart("package") MultipartFile packageFile,
            @RequestParam(defaultValue = "false") boolean zipBundle,
            @RequestParam String actorId) {
        ImportBatch created = service.receiveImportPackage(
                migrationJobId, readText(manifest), readBytes(packageFile), zipBundle,
                packageFile.getContentType(), actorId);
        return ResponseEntity.created(
                        URI.create("/api/platform/migration/import-batches/" + created.importBatchId()))
                .body(ImportBatchResponse.from(created));
    }

    @PostMapping("/{migrationJobId}/retry")
    ResponseEntity<ExecutionResponse> retryImportExecution(
            @PathVariable String migrationJobId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.ok(ExecutionResponse.from(service.retryImportExecution(migrationJobId, actorId)));
    }

    @GetMapping("/{migrationJobId}/reconciliation")
    ResponseEntity<List<ReconciliationResponse>> getReconciliationReport(@PathVariable String migrationJobId) {
        return ResponseEntity.ok(
                service.getReconciliationReports(migrationJobId).stream().map(ReconciliationResponse::from).toList());
    }

    private static String readText(MultipartFile file) {
        try {
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new InvalidMigrationCommandException("manifest could not be read: " + exception.getMessage());
        }
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new InvalidMigrationCommandException("package could not be read: " + exception.getMessage());
        }
    }

    record CreateJobRequest(
            @NotBlank String tenantId, @NotBlank String laboratoryId, @NotBlank String sourceSystemName,
            @NotBlank String actorId) {
    }

    record ActorRequest(String actorId) {
    }

    record JobResponse(
            String migrationJobId, String tenantId, String laboratoryId, String sourceSystemName, String status) {
        static JobResponse from(MigrationJob entity) {
            return new JobResponse(
                    entity.migrationJobId(), entity.tenantId(), entity.laboratoryId(), entity.sourceSystemName(),
                    entity.status());
        }
    }

    record ImportBatchResponse(
            String importBatchId, String migrationJobId, String storedPackageReference,
            Map<String, Integer> entityCounts) {
        static ImportBatchResponse from(ImportBatch entity) {
            return new ImportBatchResponse(
                    entity.importBatchId(), entity.migrationJobId(), entity.storedPackageReference(),
                    entity.entityCounts());
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

    record ReconciliationResponse(
            String reconciliationReportId, String migrationJobId, String phase,
            Map<String, Integer> importedCounts, Map<String, Integer> rejectedCounts,
            Map<String, Integer> skippedCounts, Map<String, Integer> warningCounts) {
        static ReconciliationResponse from(ReconciliationReport entity) {
            return new ReconciliationResponse(
                    entity.reconciliationReportId(), entity.migrationJobId(), entity.phase(),
                    entity.importedCounts(), entity.rejectedCounts(), entity.skippedCounts(),
                    entity.warningCounts());
        }
    }
}
