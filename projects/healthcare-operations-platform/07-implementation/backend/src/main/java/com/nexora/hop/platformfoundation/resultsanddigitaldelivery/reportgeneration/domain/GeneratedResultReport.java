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
    private int version;
    private Status status;
    private AuditMetadata audit;

    public enum Status {
        GENERATED,
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
    public int getVersion() { return version; }
    public Status getStatus() { return status; }
    public AuditMetadata getAudit() { return audit; }
}
