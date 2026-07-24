package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class ExternalQualityEvaluation {

    public enum Rating {
        PENDING_EVALUATION,
        ACCEPTABLE,
        WARNING,
        UNACCEPTABLE;

        public static Rating fromString(String val) {
            if (val == null || val.isBlank()) return PENDING_EVALUATION;
            try {
                return Rating.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return PENDING_EVALUATION;
            }
        }
    }

    private final UUID evaluationId;
    private final TenantId tenantId;
    private final String providerName;
    private final String programCode;
    private final String surveyCycle;
    private final UUID testDefinitionId;
    private final String sampleCode;
    private final double measuredValue;

    private Double peerGroupMean;
    private Double peerGroupSd;
    private Integer peerGroupCount;
    private Double zScore;
    private Rating performanceRating;
    private UUID capaInvestigationId;
    private UUID storedDocumentId;
    private Instant evaluatedAt;
    private AuditMetadata audit;

    public ExternalQualityEvaluation(
            UUID evaluationId,
            TenantId tenantId,
            String providerName,
            String programCode,
            String surveyCycle,
            UUID testDefinitionId,
            String sampleCode,
            double measuredValue,
            AuditMetadata audit) {
        if (providerName == null || providerName.isBlank()) {
            throw new ExternalQualityDomainException("EQA_PROVIDER_REQUIRED", "quality.error.provider_required", "Provider name is required");
        }
        if (programCode == null || programCode.isBlank()) {
            throw new ExternalQualityDomainException("EQA_PROGRAM_REQUIRED", "quality.error.program_required", "Program code is required");
        }
        if (sampleCode == null || sampleCode.isBlank()) {
            throw new ExternalQualityDomainException("EQA_SAMPLE_REQUIRED", "quality.error.sample_required", "Sample code is required");
        }

        this.evaluationId = evaluationId != null ? evaluationId : UUID.randomUUID();
        this.tenantId = tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString());
        this.providerName = providerName.trim();
        this.programCode = programCode.trim();
        this.surveyCycle = surveyCycle != null ? surveyCycle.trim() : "";
        this.testDefinitionId = testDefinitionId != null ? testDefinitionId : UUID.randomUUID();
        this.sampleCode = sampleCode.trim();
        this.measuredValue = measuredValue;
        this.performanceRating = Rating.PENDING_EVALUATION;
        this.evaluatedAt = Instant.now();
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public ExternalQualityEvaluation(
            UUID evaluationId,
            TenantId tenantId,
            String providerName,
            String programCode,
            String surveyCycle,
            UUID testDefinitionId,
            String sampleCode,
            double measuredValue,
            Double peerGroupMean,
            Double peerGroupSd,
            Integer peerGroupCount,
            Double zScore,
            Rating performanceRating,
            UUID capaInvestigationId,
            UUID storedDocumentId,
            Instant evaluatedAt,
            AuditMetadata audit) {
        this.evaluationId = evaluationId;
        this.tenantId = tenantId;
        this.providerName = providerName;
        this.programCode = programCode;
        this.surveyCycle = surveyCycle;
        this.testDefinitionId = testDefinitionId;
        this.sampleCode = sampleCode;
        this.measuredValue = measuredValue;
        this.peerGroupMean = peerGroupMean;
        this.peerGroupSd = peerGroupSd;
        this.peerGroupCount = peerGroupCount;
        this.zScore = zScore;
        this.performanceRating = performanceRating != null ? performanceRating : Rating.PENDING_EVALUATION;
        this.capaInvestigationId = capaInvestigationId;
        this.storedDocumentId = storedDocumentId;
        this.evaluatedAt = evaluatedAt != null ? evaluatedAt : Instant.now();
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public void applyScoring(double peerGroupMean, double peerGroupSd, Integer peerGroupCount, UUID storedDocumentId, AuditMetadata updateAudit) {
        if (peerGroupSd <= 0) {
            throw new ExternalQualityDomainException("EQA_INVALID_SD", "quality.error.invalid_sd", "Peer group SD must be greater than zero");
        }
        this.peerGroupMean = peerGroupMean;
        this.peerGroupSd = peerGroupSd;
        this.peerGroupCount = peerGroupCount;
        this.storedDocumentId = storedDocumentId;
        this.zScore = (this.measuredValue - peerGroupMean) / peerGroupSd;

        double absZ = Math.abs(this.zScore);
        if (absZ <= 2.0) {
            this.performanceRating = Rating.ACCEPTABLE;
        } else if (absZ <= 3.0) {
            this.performanceRating = Rating.WARNING;
        } else {
            this.performanceRating = Rating.UNACCEPTABLE;
        }
        this.evaluatedAt = Instant.now();
        if (updateAudit != null) {
            this.audit = updateAudit;
        }
    }

    public void linkCapa(UUID capaId) {
        this.capaInvestigationId = capaId;
    }

    public UUID getEvaluationId() { return evaluationId; }
    public TenantId getTenantId() { return tenantId; }
    public String getProviderName() { return providerName; }
    public String getProgramCode() { return programCode; }
    public String getSurveyCycle() { return surveyCycle; }
    public UUID getTestDefinitionId() { return testDefinitionId; }
    public String getSampleCode() { return sampleCode; }
    public double getMeasuredValue() { return measuredValue; }
    public Double getPeerGroupMean() { return peerGroupMean; }
    public Double getPeerGroupSd() { return peerGroupSd; }
    public Integer getPeerGroupCount() { return peerGroupCount; }
    public Double getZScore() { return zScore; }
    public Rating getPerformanceRating() { return performanceRating; }
    public UUID getCapaInvestigationId() { return capaInvestigationId; }
    public UUID getStoredDocumentId() { return storedDocumentId; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
    public AuditMetadata getAudit() { return audit; }
}
