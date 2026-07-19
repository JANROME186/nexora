package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatch;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatchRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecutionRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReportRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplateRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationAdapterException;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationDomainCommandPort;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJobRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationManifest;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReportRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationConflictException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

import tools.jackson.databind.json.JsonMapper;

/** Unit coverage for RN-003/RN-004/RN-005's checkpointed commit, idempotent retry and incremental reconciliation. */
class MigrationManagementServiceTest {

    private static final String JOB_ID = "job-1";
    private static final String BATCH_ID = "batch-1";

    private MigrationJobRepository jobRepository;
    private ImportBatchRepository importBatchRepository;
    private ImportExecutionRepository importExecutionRepository;
    private ReconciliationReportRepository reconciliationReportRepository;
    private MigrationDomainCommandPort domainCommandPort;
    private MigrationManagementService service;
    private MigrationJob[] job;
    private ImportExecution[] latestExecution;

    @BeforeEach
    void setUp() {
        jobRepository = mock(MigrationJobRepository.class);
        importBatchRepository = mock(ImportBatchRepository.class);
        ImportValidationReportRepository validationReportRepository = mock(ImportValidationReportRepository.class);
        reconciliationReportRepository = mock(ReconciliationReportRepository.class);
        importExecutionRepository = mock(ImportExecutionRepository.class);
        MappingTemplateRepository mappingTemplateRepository = mock(MappingTemplateRepository.class);
        DocumentStoragePort documentStoragePort = mock(DocumentStoragePort.class);
        domainCommandPort = mock(MigrationDomainCommandPort.class);
        TenantDirectory tenantDirectory = mock(TenantDirectory.class);
        AuditRecorder auditRecorder = mock(AuditRecorder.class);
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        service = new MigrationManagementService(
                jobRepository, importBatchRepository, mappingTemplateRepository, validationReportRepository,
                reconciliationReportRepository, importExecutionRepository, new ManifestParser(),
                new ImportFileParser(JsonMapper.builder().build()), documentStoragePort, domainCommandPort,
                tenantDirectory, auditRecorder, clock);

        job = new MigrationJob[] {new MigrationJob(
                JOB_ID, "tenant-1", "lab-1", "LegacyLIS", MigrationJob.STATUS_APPROVED, audit())};
        when(jobRepository.findById(JOB_ID)).thenAnswer(invocation -> Optional.of(job[0]));
        when(jobRepository.save(any())).thenAnswer(invocation -> {
            job[0] = invocation.getArgument(0);
            return job[0];
        });

        latestExecution = new ImportExecution[] {null};
        when(importExecutionRepository.save(any())).thenAnswer(invocation -> {
            latestExecution[0] = invocation.getArgument(0);
            return latestExecution[0];
        });
        when(importExecutionRepository.findLatestByMigrationJobId(JOB_ID))
                .thenAnswer(invocation -> Optional.ofNullable(latestExecution[0]));

        when(reconciliationReportRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBatch batch = importBatch("patients.csv", 2, "records_FAIL.csv", 2);
        when(importBatchRepository.findById(BATCH_ID)).thenReturn(Optional.of(batch));

        when(domainCommandPort.invokeImportCommand(eq(JOB_ID), eq("patients.csv"), anyInt()))
                .thenReturn("cmd-patients");
        when(domainCommandPort.invokeImportCommand(eq(JOB_ID), eq("records_FAIL.csv"), anyInt()))
                .thenThrow(new MigrationAdapterException(
                        "adapter failure", MigrationErrorCodes.MIGRATION_DOMAIN_COMMAND_FAILED));
    }

    @Test
    void commitStopsAtTheFirstFailingCategoryAndCheckpointsCompletedCategoriesOnly() {
        ImportExecution execution = service.commitImport(BATCH_ID, "migration-lead");

        assertThat(execution.status()).isEqualTo(ImportExecution.STATUS_FAILED);
        assertThat(execution.attemptNumber()).isEqualTo(1);
        assertThat(execution.domainCommandsInvoked()).containsExactly("cmd-patients");
        assertThat(execution.checkpoint()).isEqualTo("patients.csv");
        assertThat(job[0].status()).isEqualTo(MigrationJob.STATUS_EXECUTING);
    }

    @Test
    void retryResumesFromTheCheckpointWithoutReinvokingTheCompletedCategory() {
        service.commitImport(BATCH_ID, "migration-lead");

        ImportExecution retried = service.retryImportExecution(JOB_ID, "migration-lead");

        assertThat(retried.attemptNumber()).isEqualTo(2);
        assertThat(retried.status()).isEqualTo(ImportExecution.STATUS_FAILED);
        assertThat(retried.domainCommandsInvoked()).containsExactly("cmd-patients");
        assertThat(retried.checkpoint()).isEqualTo("patients.csv");
        org.mockito.Mockito.verify(domainCommandPort, org.mockito.Mockito.times(1))
                .invokeImportCommand(JOB_ID, "patients.csv", 2);
    }

    @Test
    void exhaustingTheMaximumAttemptsFailsTheJobAndBlocksFurtherRetries() {
        service.commitImport(BATCH_ID, "migration-lead");
        for (int attempt = 2; attempt <= MigrationManagementService.MAX_EXECUTION_ATTEMPTS; attempt++) {
            ImportExecution retried = service.retryImportExecution(JOB_ID, "migration-lead");
            assertThat(retried.attemptNumber()).isEqualTo(attempt);
        }

        MigrationConflictException exception = assertThrows(MigrationConflictException.class,
                () -> service.retryImportExecution(JOB_ID, "migration-lead"));
        assertThat(exception.code()).isEqualTo(MigrationErrorCodes.MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED);
        assertThat(job[0].status()).isEqualTo(MigrationJob.STATUS_FAILED);

        MigrationConflictException blocked = assertThrows(MigrationConflictException.class,
                () -> service.retryImportExecution(JOB_ID, "migration-lead"));
        assertThat(blocked.code()).isEqualTo(MigrationErrorCodes.MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED);
    }

    @Test
    void everyCategorySucceedingCompletesTheExecutionAndReconcilesTheJob() {
        ImportBatch batch = importBatch("patients.csv", 2, "doctors.csv", 3);
        when(importBatchRepository.findById(BATCH_ID)).thenReturn(Optional.of(batch));
        when(domainCommandPort.invokeImportCommand(eq(JOB_ID), eq("doctors.csv"), anyInt()))
                .thenReturn("cmd-doctors");

        ImportExecution execution = service.commitImport(BATCH_ID, "migration-lead");

        assertThat(execution.status()).isEqualTo(ImportExecution.STATUS_COMPLETED);
        assertThat(execution.domainCommandsInvoked()).containsExactly("cmd-patients", "cmd-doctors");
        assertThat(com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec.splitList(execution.checkpoint()))
                .containsExactly("patients.csv", "doctors.csv");
        assertThat(job[0].status()).isEqualTo(MigrationJob.STATUS_RECONCILED);

        org.mockito.Mockito.verify(reconciliationReportRepository, org.mockito.Mockito.times(2)).save(any());
    }

    private ImportBatch importBatch(String category1, int count1, String category2, int count2) {
        Map<String, Integer> entityCounts = new LinkedHashMap<>();
        entityCounts.put(category1, count1);
        entityCounts.put(category2, count2);
        MigrationManifest manifest = new MigrationManifest(
                "LegacyLIS", null, "Legacy Labs", Instant.parse("2026-01-01T00:00:00Z"), "UTC",
                "exporter@legacy.com", "exporter@legacy.com", java.util.List.of(category1, category2), entityCounts,
                "sha256", Map.of(), java.util.List.of("csv", "csv"), "UTF-8");
        return new ImportBatch(BATCH_ID, JOB_ID, manifest, "storage-ref", entityCounts, audit());
    }

    private AuditMetadata audit() {
        return new AuditMetadata("migration-lead", LocalDateTime.now(), "migration-lead", LocalDateTime.now());
    }
}
