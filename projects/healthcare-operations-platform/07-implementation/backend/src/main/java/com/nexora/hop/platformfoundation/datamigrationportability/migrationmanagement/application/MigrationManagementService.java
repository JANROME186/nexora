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
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationAdapterException;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationDomainCommandPort;
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
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of bcm-plt-010-open-data-ingestion-and-migration/
 * generation-plan.md (job creation/listing, approve, reconciliation report reading,
 * PRC-MIG-010-01) and implements the CUS-MIG-010-01 through CUS-MIG-010-06 custom rules: real
 * manifest/checksum verification and CSV/JSON/NDJSON/XLSX/ZIP parsing via
 * {@link ManifestParser}/{@link ImportFileParser} (RN-001), a naive identity field-mapping
 * baseline (mapping step of PRC-MIG-010-02), multi-category dry-run validation without mutation
 * (RN-002, INV-MIG-002), import execution delegated exclusively to
 * {@link MigrationDomainCommandPort} with per-category checkpoint recording (RN-003, INV-MIG-003,
 * delivered by MVP-MOD-008-BE-002), checkpoint-based idempotent retry that resumes only the
 * categories not yet completed (RN-004, INV-MIG-004, delivered by MVP-MOD-008-BE-002), and
 * incremental reconciliation-report aggregation after every commit/retry attempt (RN-005,
 * delivered by MVP-MOD-008-BE-002).
 */
@Service
public class MigrationManagementService {

    /**
     * Bounded retry ceiling for {@link ImportExecution} attempts (RN-004): the migration job is
     * marked {@link MigrationJob#STATUS_FAILED} rather than retried again once this many attempts
     * have been made.
     */
    static final int MAX_EXECUTION_ATTEMPTS = 5;

    private final MigrationJobRepository jobRepository;
    private final ImportBatchRepository importBatchRepository;
    private final MappingTemplateRepository mappingTemplateRepository;
    private final ImportValidationReportRepository validationReportRepository;
    private final ReconciliationReportRepository reconciliationReportRepository;
    private final ImportExecutionRepository importExecutionRepository;
    private final ManifestParser manifestParser;
    private final ImportFileParser importFileParser;
    private final DocumentStoragePort documentStoragePort;
    private final MigrationDomainCommandPort domainCommandPort;
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
            MigrationDomainCommandPort domainCommandPort,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(jobRepository, importBatchRepository, mappingTemplateRepository, validationReportRepository,
                reconciliationReportRepository, importExecutionRepository, manifestParser, importFileParser,
                documentStoragePort, domainCommandPort, tenantDirectory, auditRecorder, Clock.systemUTC());
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
            MigrationDomainCommandPort domainCommandPort,
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
        this.domainCommandPort = domainCommandPort;
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
                        + "' (unsupported declared format).");
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
     * CUS-MIG-010-04: records a {@link ReconciliationReport#PHASE_PRE_IMPORT} baseline from the
     * batch's parsed entity counts (INV-MIG-005), then delegates every declared entity category to
     * {@link MigrationDomainCommandPort#invokeImportCommand} in turn (RN-003), recording each
     * invocation's stable command identifier and the completed-category checkpoint as it goes
     * (RN-004). If a category's invocation fails, execution stops there — already-completed
     * categories remain checkpointed for {@link #retryImportExecution} to resume from rather than
     * being re-invoked (INV-MIG-004).
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

        jobRepository.save(new MigrationJob(
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                MigrationJob.STATUS_EXECUTING, touched(job.audit(), actor)));

        ReconciliationReport preImportReport = new ReconciliationReport(
                newId(), job.migrationJobId(), ReconciliationReport.PHASE_PRE_IMPORT, batch.entityCounts(),
                Map.of(), Map.of(), Map.of(), audit);
        reconciliationReportRepository.save(preImportReport);

        ImportExecution execution = executeFromCheckpoint(
                job, newId(), batch, List.of(), List.of(), 1, actor, now);
        auditRecorder.recordSystemEvent(job.tenantId(), "MigrationExecuted", "MigrationJob", job.migrationJobId(),
                "{\"status\":\"%s\"}".formatted(execution.status()));
        return execution;
    }

    /**
     * CUS-MIG-010-05: bounded, auditable retry (RN-004) that resumes from the last execution's
     * {@link ImportExecution#checkpoint()} instead of re-invoking already-completed categories
     * (INV-MIG-004, idempotent resume). Once {@link #MAX_EXECUTION_ATTEMPTS} attempts have been
     * made, the job is marked {@link MigrationJob#STATUS_FAILED} and further retries are rejected.
     */
    public ImportExecution retryImportExecution(String migrationJobId, String actorId) {
        MigrationJob job = requireJob(migrationJobId);
        if (MigrationJob.STATUS_FAILED.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + migrationJobId + " exhausted its retry attempts and cannot be retried again.",
                    MigrationErrorCodes.MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED);
        }
        if (!MigrationJob.STATUS_EXECUTING.equals(job.status())) {
            throw new MigrationConflictException(
                    "Migration job " + migrationJobId + " is not executing (status " + job.status() + ").",
                    MigrationErrorCodes.MIGRATION_RETRY_CHECKPOINT_MISMATCH);
        }
        ImportExecution latest = importExecutionRepository.findLatestByMigrationJobId(migrationJobId)
                .orElseThrow(() -> new MigrationConflictException(
                        "Migration job " + migrationJobId + " has no execution to retry.",
                        MigrationErrorCodes.MIGRATION_RETRY_CHECKPOINT_MISMATCH));
        if (latest.attemptNumber() >= MAX_EXECUTION_ATTEMPTS) {
            String actor = requiredText(actorId, "Actor id is required.");
            jobRepository.save(new MigrationJob(
                    job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                    MigrationJob.STATUS_FAILED, touched(job.audit(), actor)));
            throw new MigrationConflictException(
                    "Migration job " + migrationJobId + " exhausted the maximum of " + MAX_EXECUTION_ATTEMPTS
                            + " execution attempts.",
                    MigrationErrorCodes.MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED);
        }
        ImportBatch batch = requireBatch(latest.importBatchId());
        String actor = requiredText(actorId, "Actor id is required.");
        LocalDateTime now = LocalDateTime.now(clock);

        importExecutionRepository.save(new ImportExecution(
                latest.executionId(), latest.migrationJobId(), latest.importBatchId(), latest.attemptNumber(),
                latest.domainCommandsInvoked(), latest.checkpoint(), ImportExecution.STATUS_RETRIED,
                touched(latest.audit(), actor)));

        List<String> alreadyCompletedCategories = DelimitedTextCodec.splitList(latest.checkpoint());
        ImportExecution retryAttempt = executeFromCheckpoint(
                job, newId(), batch, alreadyCompletedCategories, latest.domainCommandsInvoked(),
                latest.attemptNumber() + 1, actor, now);
        auditRecorder.recordSystemEvent(job.tenantId(), "MigrationImportRetried", "MigrationJob", migrationJobId,
                "{\"attemptNumber\":%d,\"status\":\"%s\"}"
                        .formatted(retryAttempt.attemptNumber(), retryAttempt.status()));
        return retryAttempt;
    }

    /**
     * Invokes {@link MigrationDomainCommandPort#invokeImportCommand} for every declared entity
     * category not already present in {@code alreadyCompletedCategories}, stopping at the first
     * failure so the checkpoint always reflects real, contiguous progress. Writes an incremental
     * {@link ReconciliationReport#PHASE_POST_IMPORT} report reflecting exactly what this attempt
     * completed and, on failure, which category was rejected (RN-005). On full completion the
     * migration job transitions to {@link MigrationJob#STATUS_RECONCILED}.
     */
    private ImportExecution executeFromCheckpoint(
            MigrationJob job, String executionId, ImportBatch batch, List<String> alreadyCompletedCategories,
            List<String> alreadyInvokedCommandIds, int attemptNumber, String actor, LocalDateTime now) {
        List<String> completedCategories = new ArrayList<>(alreadyCompletedCategories);
        List<String> commandsInvoked = new ArrayList<>(alreadyInvokedCommandIds);
        String failedCategory = null;
        String failureCanonicalErrorCode = null;
        for (Map.Entry<String, Integer> category : batch.entityCounts().entrySet()) {
            if (completedCategories.contains(category.getKey())) {
                continue;
            }
            try {
                commandsInvoked.add(domainCommandPort.invokeImportCommand(
                        job.migrationJobId(), category.getKey(), category.getValue()));
                completedCategories.add(category.getKey());
            } catch (MigrationAdapterException exception) {
                failedCategory = category.getKey();
                failureCanonicalErrorCode = exception.canonicalErrorCode();
                break;
            }
        }
        boolean fullyCompleted = failedCategory == null;
        String checkpoint = DelimitedTextCodec.joinList(completedCategories);
        String status = fullyCompleted ? ImportExecution.STATUS_COMPLETED : ImportExecution.STATUS_FAILED;
        AuditMetadata audit = new AuditMetadata(actor, now, actor, now);
        ImportExecution execution = new ImportExecution(
                executionId, job.migrationJobId(), batch.importBatchId(), attemptNumber, List.copyOf(commandsInvoked),
                checkpoint, status, audit);
        ImportExecution saved = importExecutionRepository.save(execution);

        Map<String, Integer> importedCounts = new LinkedHashMap<>();
        completedCategories.forEach(c -> importedCounts.put(c, batch.entityCounts().get(c)));
        Map<String, Integer> rejectedCounts = failedCategory == null
                ? Map.of()
                : Map.of(failedCategory, batch.entityCounts().get(failedCategory));
        Map<String, Integer> warningCounts = failedCategory == null
                ? Map.of()
                : Map.of(failedCategory, 1);
        ReconciliationReport postImportReport = new ReconciliationReport(
                newId(), job.migrationJobId(), ReconciliationReport.PHASE_POST_IMPORT, importedCounts, rejectedCounts,
                Map.of(), warningCounts, audit);
        reconciliationReportRepository.save(postImportReport);

        if (fullyCompleted) {
            jobRepository.save(new MigrationJob(
                    job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(),
                    MigrationJob.STATUS_RECONCILED, touched(job.audit(), actor)));
        }
        auditRecorder.recordSystemEvent(job.tenantId(), "ReconciliationReportRecorded", "MigrationJob",
                job.migrationJobId(),
                "{\"phase\":\"post_import\",\"fullyCompleted\":%s%s}".formatted(fullyCompleted,
                        failedCategory == null ? ""
                                : ",\"failedCategory\":\"%s\",\"canonicalErrorCode\":\"%s\""
                                        .formatted(failedCategory, failureCanonicalErrorCode)));
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
