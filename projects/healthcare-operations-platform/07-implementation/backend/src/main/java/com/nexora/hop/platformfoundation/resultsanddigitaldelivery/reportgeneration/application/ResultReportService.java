package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.application;

import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.documentmanagement.domain.RetentionPolicy;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StoredDocument;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReport;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReportRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.shared.ResultsDeliveryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for PDF report generation (BCM-RES-002).
 *
 * <p>Report content rendering uses a deterministic placeholder payload persisted through
 * {@link DocumentManagementService}; integration with a dedicated PDF-rendering engine is
 * tracked as follow-up work and does not change this service's persistence, versioning or
 * business-rule contract.
 */
@Service
public class ResultReportService {

    private static final int RETENTION_YEARS = 10;

    private final GeneratedResultReportRepository repository;
    private final LaboratoryResultsRepository laboratoryResultsRepository;
    private final DocumentManagementService documentManagementService;

    public ResultReportService(
            GeneratedResultReportRepository repository,
            LaboratoryResultsRepository laboratoryResultsRepository,
            DocumentManagementService documentManagementService) {
        this.repository = repository;
        this.laboratoryResultsRepository = laboratoryResultsRepository;
        this.documentManagementService = documentManagementService;
    }

    public List<GeneratedResultReport> listReports(String resultId, String tenantId) {
        return repository.findByResultId(new ResultId(resultId), new TenantId(tenantId));
    }

    /**
     * Regenerate a PDF report for a released result.
     *
     * <p>RN-001: the release-state precondition is enforced here — only released or amended
     * results are eligible for report (re)generation.
     */
    public GeneratedResultReport regenerateReport(String resultId, String tenantId, String actorId) {
        LaboratoryResult result = laboratoryResultsRepository.findById(resultId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found."));

        if (result.status() != ResultStatus.released && result.status() != ResultStatus.amended) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.REPORT_RESULT_NOT_RELEASED);
        }

        ResultId resultIdObj = new ResultId(resultId);
        TenantId tenantIdObj = new TenantId(tenantId);
        List<GeneratedResultReport> existing = repository.findByResultId(resultIdObj, tenantIdObj);
        int nextVersion = existing.size() + 1;

        AuditMetadata audit = new AuditMetadata(actorId, LocalDateTime.now(), actorId, LocalDateTime.now());

        for (GeneratedResultReport report : existing) {
            if (report.getStatus() == GeneratedResultReport.Status.GENERATED) {
                report.supersede(audit);
                repository.save(report);
            }
        }

        UUID reportId = UUID.randomUUID();
        try {
            byte[] content = renderReportContent(result, nextVersion);
            StoredDocument document = documentManagementService.storeDocument(
                    tenantIdObj,
                    new LaboratoryId(result.laboratoryId()),
                    "result-report",
                    reportId,
                    nextVersion,
                    content,
                    "application/pdf",
                    RetentionPolicy.standard(LocalDate.now().plusYears(RETENTION_YEARS)),
                    audit);

            GeneratedResultReport report = new GeneratedResultReport(
                    reportId, resultIdObj, tenantIdObj, document.getDocumentId(),
                    document.getContentHash(), nextVersion, audit);
            repository.save(report);
            return report;
        } catch (RuntimeException e) {
            GeneratedResultReport failed = GeneratedResultReport.failed(reportId, resultIdObj, tenantIdObj, nextVersion, audit);
            repository.save(failed);
            throw e;
        }
    }

    private byte[] renderReportContent(LaboratoryResult result, int version) {
        String summary = "Result Report\n"
                + "resultId=" + result.resultId() + "\n"
                + "tenantId=" + result.tenantId() + "\n"
                + "status=" + result.status() + "\n"
                + "version=" + version + "\n";
        return summary.getBytes(StandardCharsets.UTF_8);
    }
}
