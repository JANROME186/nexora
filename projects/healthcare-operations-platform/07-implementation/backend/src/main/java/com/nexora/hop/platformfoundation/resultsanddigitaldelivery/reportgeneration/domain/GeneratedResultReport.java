package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.util.UUID;

public class GeneratedResultReport {

    private UUID reportId;
    private ResultId resultId;
    private TenantId tenantId;
    private UUID storedDocumentId;
    private String integrityChecksum;
    private int version;
    private Status status;
    private AuditMetadata audit;

    public enum Status {
        GENERATED,
        GENERATION_FAILED,
        SUPERSEDED
    }

    protected GeneratedResultReport() {}

    public GeneratedResultReport(
            UUID reportId,
            ResultId resultId,
            TenantId tenantId,
            UUID storedDocumentId,
            int version,
            AuditMetadata audit) {

        this.reportId = reportId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.storedDocumentId = storedDocumentId;
        this.version = version;
        this.status = Status.GENERATED;
        this.audit = audit;
    }

    public GeneratedResultReport(
            UUID reportId,
            ResultId resultId,
            TenantId tenantId,
            UUID storedDocumentId,
            String integrityChecksum,
            int version,
            AuditMetadata audit) {

        this(reportId, resultId, tenantId, storedDocumentId, version, audit);
        this.integrityChecksum = integrityChecksum;
    }

    /** Records a failed report-generation attempt (BCM-RES-002 regenerate action). */
    public static GeneratedResultReport failed(
            UUID reportId, ResultId resultId, TenantId tenantId, int version, AuditMetadata audit) {
        GeneratedResultReport report = new GeneratedResultReport();
        report.reportId = reportId;
        report.resultId = resultId;
        report.tenantId = tenantId;
        report.version = version;
        report.status = Status.GENERATION_FAILED;
        report.audit = audit;
        return report;
    }

    public void supersede(AuditMetadata updateAudit) {
        if (this.status != Status.GENERATED) {
            throw new IllegalStateException("Only active reports can be superseded");
        }
        this.status = Status.SUPERSEDED;
        this.audit = updateAudit;
    }

    public UUID getReportId() { return reportId; }
    public ResultId getResultId() { return resultId; }
    public TenantId getTenantId() { return tenantId; }
    public UUID getStoredDocumentId() { return storedDocumentId; }
    public String getIntegrityChecksum() { return integrityChecksum; }
    public int getVersion() { return version; }
    public Status getStatus() { return status; }
    public AuditMetadata getAudit() { return audit; }
}
