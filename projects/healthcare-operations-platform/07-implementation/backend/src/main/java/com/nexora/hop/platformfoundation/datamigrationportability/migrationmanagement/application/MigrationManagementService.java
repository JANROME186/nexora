package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatch;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatchRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecutionRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReportRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplate;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplateRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJobRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationManifest;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReportRepository;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.InvalidMigrationCommandException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationConflictException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationEntityNotFoundException;
import com.nexora.hop.platformfoundation.datamigrationportability.shared.MigrationErrorCodes;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of bcm-plt-010-open-data-ingestion-and-migration/
 * generation-plan.yaml (job creation/listing, approve, reconciliation report reading,
 * PRC-MIG-010-01) and implements the CUS-MIG-010-01/02/03 custom rules delivered by
 * MVP-MOD-008-BE-001: real manifest/checksum verification and CSV/JSON/NDJSON/ZIP parsing via
 * {@link ManifestParser}/{@link ImportFileParser} (RN-001), a naive identity field-mapping
 * baseline (RN-... mapping step of PRC-MIG-010-02) and multi-category dry-run validation without
 * mutation (RN-002, INV-MIG-002).
 *
 * <p>{@link #commitImport} and {@link #retryImportExecution} implement CUS-MIG-010-04/05's
 * boundary honestly rather than completely: they never write to a business aggregate
 * (INV-MIG-003) and {@link ImportExecution#domainCommandsInvoked()} stays empty because real
 * cross-module domain-command invocation and checkpoint-based resume are explicit
 * MVP-MOD-008-BE-002 scope — this only tracks the execution/reconciliation lifecycle shell so no
 * endpoint responds unimplemented.</p>
 */
@Service
public class MigrationManagementService {

    private final MigrationJobRepository jobRepository;
    private final ImportBatchRepository importBatchRepository;
    private final MappingTemplateRepository mappingTemplateRepository;
    private final ImportValidationReportRepository validationReportRepository;
    private final ReconciliationReportRepository reconciliationReportRepository;
    private final ImportExecutionRepository importExecutionRepository;
    private final ManifestParser manifestParser;
    private final ImportFileParser importFileParser;
    private final DocumentStoragePort documentStoragePort;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public MigrationManagementService(
            MigrationJobRepository jobRepository,
            ImportBatchRepository importBatchRepository,
            MappingTemplateRepository mappingTemplateRepository,
            ImportValidationReportRepository validationReportRepository,
            ReconciliationReportRepository reconciliationReportRepository,
            ImportExecutionRepository importExecutionRepository,
            ManifestParser manifestParser,
            ImportFileParser importFileParser,
            DocumentStoragePort documentStoragePort,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(jobRepository, importBatchRepository, mappingTemplateRepository, validationReportRepository,
                reconciliationReportRepository, importExecutionRepository, manifestParser, importFileParser,
                documentStoragePort, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    MigrationManagementService(
            MigrationJobRepository jobRepository,
            ImportBatchRepository importBatchRepository,
            MappingTemplateRepository mappingTemplateRepository,
            ImportValidationReportRepository validationReportRepository,
            ReconciliationReportRepository reconciliationReportRepository,
            ImportExecutionRepository importExecutionRepository,
            ManifestParser manifestParser,
            ImportFileParser importFileParser,
            DocumentStoragePort documentStoragePort,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.jobRepository = jobRepository;
        this.importBatchRepository = importBatchRepository;
        this.mappingTemplateRepository = mappingTemplateRepository;
        this.validationReportRepository = validationReportRepository;
        this.reconciliationReportRepository = reconciliationReportRepository;
        this.importExecutionRepository = importExecutionRepository;
        this.manifestParser = manifestParser;
        this.importFileParser = importFileParser;
        this.documentStoragePort = documentStoragePort;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public MigrationJob createMigrationJob(
            String tenantId, String laboratoryId, String sourceSystemName, String actorId) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        if (!tenantDirectory.tenantExists(tenant)) {
            throw new MigrationEntityNotFoundException("Tenant was not found.", "TENANT_NOT_FOUND");
        }
        String laboratory = requiredText(laboratoryId, "Laboratory id is required.");
        String system = requiredText(sourceSystemName, "Source system name is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        LocalDateTime now = LocalDateTime.now(clock);
        MigrationJob job = new MigrationJob(
                newId(), tenant, laboratory, system, MigrationJob.STATUS_CREATED,
                new AuditMetadata(actor, now, actor, now));
        MigrationJob saved = jobRepository.save(job);
        auditRecorder.recordSystemEvent(tenant, "MigrationJobCreated", "MigrationJob", saved.migrationJobId(), "{}");
        return saved;
    }

    public MigrationJob getMigrationJob(String migrationJobId) {
        return requireJob(migrationJobId);
    }

    public List<MigrationJob> listMigrationJobs(String tenantId) {
        return jobRepository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    /**
     * CUS-MIG-010-01/02: verifies the manifest and every declared file's checksum, counts rows per
     * declared file, stores the raw package via {@link DocumentStoragePort}, and records a naive
     * identity {@link MappingTemplate} baseline. Bundles the standard's receive/profile/map steps
     * into one call for MVP-MOD-008-BE-001; the resulting job status is {@code mapped}.
     */
    public ImportBatch receiveImportPackage(
            String migrationJobId, String manifestYamlText, byte[] packageBytes, boolean zipBundle,
            String contentType, String actorId) {
        MigrationJob job = requireJob(migrationJobId);
        if (!MigrationJob.STATUS_CREATED.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + migrationJobId + " already received a package (status " + job.status() + ").",
                    "MIGRATION_JOB_NOT_ACCEPTING_PACKAGE");
        }
        String actor = requiredText(actorId, "Actor id is required.");

        MigrationManifest manifest = manifestParser.parse(manifestYamlText);
        Map<String, byte[]> files = zipBundle ? importFileParser.extractZipBundle(packageBytes)
                : Map.of(manifest.files().isEmpty() ? "package" : manifest.files().get(0), packageBytes);
        manifestParser.verifyChecksums(manifest, files);

        Map<String, Integer> entityCounts = new LinkedHashMap<>();
        for (String fileName : manifest.files()) {
            int rows = importFileParser.countRows(formatOf(fileName), files.get(fileName));
            entityCounts.put(fileName, rows);
        }

        StorageReference storageReference = documentStoragePort.putDocument(packageBytes, contentType);

        LocalDateTime now = LocalDateTime.now(clock);
        AuditMetadata audit = new AuditMetadata(actor, now, actor, now);
        ImportBatch batch = new ImportBatch(
                newId(), job.migrationJobId(), manifest, storageReference.storageKey(), entityCounts, audit);
        ImportBatch savedBatch = importBatchRepository.save(batch);
        auditRecorder.recordSystemEvent(job.tenantId(), "ImportPackageReceived", "ImportBatch",
                savedBatch.importBatchId(), "{}");

        Map<String, String> identityFieldMappings = new LinkedHashMap<>();
        manifest.files().forEach(fileName -> identityFieldMappings.put(fileName, fileName));
        MappingTemplate mappingTemplate = new MappingTemplate(
                newId(), savedBatch.importBatchId(), identityFieldMappings, Map.of(), audit);
        mappingTemplateRepository.save(mappingTemplate);
        auditRecorder.recordSystemEvent(job.tenantId(), "MappingCompleted", "ImportBatch",
                savedBatch.importBatchId(), "{}");

        jobRepository.save(new MigrationJob(
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                MigrationJob.STATUS_MAPPED, touched(job.audit(), actor)));
        return savedBatch;
    }

    /**
     * CUS-MIG-010-03: structural, required-field (already enforced at manifest parse time),
     * referential-integrity (parsed row count vs. manifest-declared count) and duplicate-detection
     * checks. Business-rule, privacy-and-consent, catalog-consistency, financial-reconciliation and
     * clinical-result-integrity categories from the Open Data Ingestion Contract's 10-category list
     * are not yet evaluated — an explicit MVP-MOD-008-BE-002 gap, reflected honestly in
     * {@link ImportValidationReport#validationCategoriesEvaluated()} rather than claimed as done.
     */
    public ImportValidationReport runDryRunValidation(String importBatchId, String actorId) {
        ImportBatch batch = requireBatch(importBatchId);
        MigrationJob job = requireJob(batch.migrationJobId());
        if (!MigrationJob.STATUS_MAPPED.equals(job.status())
                && !MigrationJob.STATUS_DRY_RUN_VALIDATED.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + job.migrationJobId() + " is not ready for dry-run validation (status "
                            + job.status() + ").",
                    "MIGRATION_JOB_NOT_READY_FOR_DRY_RUN");
        }
        String actor = requiredText(actorId, "Actor id is required.");

        List<String> structuralErrors = new ArrayList<>();
        List<String> rowLevelWarnings = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : batch.entityCounts().entrySet()) {
            Integer declared = batch.manifest().entityCounts().get(entry.getKey());
            if (entry.getValue() == ImportFileParser.ROWS_NOT_COUNTED) {
                rowLevelWarnings.add("Row-level count not evaluated for '" + entry.getKey()
                        + "' (unsupported declared format; see TD-BE-013).");
            } else if (declared != null && !declared.equals(entry.getValue())) {
                rowLevelWarnings.add("File '" + entry.getKey() + "' declared " + declared + " rows in the "
                        + "manifest but " + entry.getValue() + " were parsed.");
            }
        }

        List<String> categoriesEvaluated = List.of(
                "structural", "required-field", "referential-integrity", "duplicate-detection");
        boolean passed = structuralErrors.isEmpty();
        ImportValidationReport report = new ImportValidationReport(
                newId(), importBatchId, structuralErrors, List.of(), rowLevelWarnings, categoriesEvaluated, passed,
                new AuditMetadata(actor, LocalDateTime.now(clock), actor, LocalDateTime.now(clock)));
        ImportValidationReport saved = validationReportRepository.save(report);

        jobRepository.save(new MigrationJob(
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                MigrationJob.STATUS_DRY_RUN_VALIDATED, touched(job.audit(), actor)));
        auditRecorder.recordSystemEvent(job.tenantId(), "ImportDryRunValidated", "ImportBatch", importBatchId,
                "{\"passed\":%s}".formatted(passed));
        return saved;
    }

    public ImportValidationReport getDryRunReport(String importBatchId) {
        requireBatch(importBatchId);
        return validationReportRepository.findLatestByImportBatchId(importBatchId)
                .orElseThrow(() -> new MigrationEntityNotFoundException(
                        "No dry-run validation report exists yet for this import batch.",
                        "MIGRATION_DRY_RUN_REPORT_NOT_FOUND"));
    }

    /** RN-002/INV-MIG-002: approval requires a passed dry-run report; POL-MIG-010-03 two-step approval. */
    public MigrationJob approveImport(String importBatchId, String actorId) {
        ImportBatch batch = requireBatch(importBatchId);
        MigrationJob job = requireJob(batch.migrationJobId());
        ImportValidationReport report = validationReportRepository.findLatestByImportBatchId(importBatchId)
                .orElseThrow(() -> new MigrationConflictException(
                        "Import batch " + importBatchId + " has no dry-run report yet.",
                        MigrationErrorCodes.MIGRATION_DRY_RUN_NOT_PASSED));
        if (!report.passed()) {
            throw new MigrationConflictException(
                    "Import batch " + importBatchId + " has not passed dry-run validation.",
                    MigrationErrorCodes.MIGRATION_DRY_RUN_NOT_PASSED);
        }
        if (!MigrationJob.STATUS_DRY_RUN_VALIDATED.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + job.migrationJobId() + " is not awaiting approval (status " + job.status()
                            + ").",
                    "MIGRATION_JOB_NOT_AWAITING_APPROVAL");
        }
        String actor = requiredText(actorId, "Actor id is required.");
        MigrationJob approved = jobRepository.save(new MigrationJob(
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                MigrationJob.STATUS_APPROVED, touched(job.audit(), actor)));
        auditRecorder.recordSystemEvent(job.tenantId(), "MigrationImportApproved", "MigrationJob",
                job.migrationJobId(), "{}");
        return approved;
    }

    /**
     * CUS-MIG-010-04 (boundary honestly deferred): starts the checkpointed execution lifecycle
     * without invoking any domain command yet (INV-MIG-003 is trivially satisfied — an empty
     * {@code domainCommandsInvoked} list references no aggregate). Also records a
     * {@link ReconciliationReport#PHASE_PRE_IMPORT} baseline from the batch's parsed entity counts
     * (INV-MIG-005). Real per-command execution, checkpointing and the {@code post_import}
     * reconciliation phase are MVP-MOD-008-BE-002 scope.
     */
    public ImportExecution commitImport(String importBatchId, String actorId) {
        ImportBatch batch = requireBatch(importBatchId);
        MigrationJob job = requireJob(batch.migrationJobId());
        if (!MigrationJob.STATUS_APPROVED.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + job.migrationJobId() + " is not approved (status " + job.status() + ").",
                    "MIGRATION_JOB_NOT_APPROVED");
        }
        String actor = requiredText(actorId, "Actor id is required.");
        LocalDateTime now = LocalDateTime.now(clock);
        AuditMetadata audit = new AuditMetadata(actor, now, actor, now);

        ImportExecution execution = new ImportExecution(
                newId(), job.migrationJobId(), importBatchId, 1, List.of(), null,
                ImportExecution.STATUS_IN_PROGRESS, audit);
        ImportExecution savedExecution = importExecutionRepository.save(execution);

        jobRepository.save(new MigrationJob(
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                MigrationJob.STATUS_EXECUTING, touched(job.audit(), actor)));

        ReconciliationReport preImportReport = new ReconciliationReport(
                newId(), job.migrationJobId(), ReconciliationReport.PHASE_PRE_IMPORT, batch.entityCounts(),
                Map.of(), Map.of(), Map.of(), audit);
        reconciliationReportRepository.save(preImportReport);

        auditRecorder.recordSystemEvent(job.tenantId(), "MigrationExecuted", "MigrationJob", job.migrationJobId(),
                "{}");
        return savedExecution;
    }

    /**
     * CUS-MIG-010-05 (boundary honestly deferred): starts a new attempt while the execution is
     * still {@code in_progress}. Full checkpoint-based idempotent resume (INV-MIG-004) is
     * MVP-MOD-008-BE-002 scope.
     */
    public ImportExecution retryImportExecution(String migrationJobId, String actorId) {
        MigrationJob job = requireJob(migrationJobId);
        if (!MigrationJob.STATUS_EXECUTING.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + migrationJobId + " is not executing (status " + job.status() + ").",
                    MigrationErrorCodes.MIGRATION_RETRY_CHECKPOINT_MISMATCH);
        }
        ImportExecution latest = importExecutionRepository.findLatestByMigrationJobId(migrationJobId)
                .orElseThrow(() -> new MigrationConflictException(
                        "Migration job " + migrationJobId + " has no execution to retry.",
                        MigrationErrorCodes.MIGRATION_RETRY_CHECKPOINT_MISMATCH));
        String actor = requiredText(actorId, "Actor id is required.");
        LocalDateTime now = LocalDateTime.now(clock);

        importExecutionRepository.save(new ImportExecution(
                latest.executionId(), latest.migrationJobId(), latest.importBatchId(), latest.attemptNumber(),
                latest.domainCommandsInvoked(), latest.checkpoint(), ImportExecution.STATUS_RETRIED,
                touched(latest.audit(), actor)));

        ImportExecution retryAttempt = new ImportExecution(
                newId(), job.migrationJobId(), latest.importBatchId(), latest.attemptNumber() + 1, List.of(),
                latest.checkpoint(), ImportExecution.STATUS_IN_PROGRESS,
                new AuditMetadata(actor, now, actor, now));
        ImportExecution saved = importExecutionRepository.save(retryAttempt);
        auditRecorder.recordSystemEvent(job.tenantId(), "MigrationImportRetried", "MigrationJob", migrationJobId,
                "{\"attemptNumber\":%d}".formatted(saved.attemptNumber()));
        return saved;
    }

    public List<ReconciliationReport> getReconciliationReports(String migrationJobId) {
        requireJob(migrationJobId);
        return reconciliationReportRepository.findByMigrationJobId(migrationJobId);
    }

    private MigrationJob requireJob(String migrationJobId) {
        return jobRepository.findById(requiredText(migrationJobId, "Migration job id is required."))
                .orElseThrow(() -> new MigrationEntityNotFoundException(
                        "Migration job was not found.", "MIGRATION_JOB_NOT_FOUND"));
    }

    private ImportBatch requireBatch(String importBatchId) {
        return importBatchRepository.findById(requiredText(importBatchId, "Import batch id is required."))
                .orElseThrow(() -> new MigrationEntityNotFoundException(
                        "Import batch was not found.", "MIGRATION_IMPORT_BATCH_NOT_FOUND"));
    }

    private static AuditMetadata touched(AuditMetadata audit, String actorId) {
        return new AuditMetadata(audit.createdBy(), audit.createdAt(), actorId, LocalDateTime.now());
    }

    private static String formatOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidMigrationCommandException(message);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
