package com.nexora.hop.platformfoundation.externalqualitycompliance.application;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityComplianceException;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events.ExternalQualityEvaluationScoredEvent;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalQualityService {

    private final ExternalQualityEvaluationRepository repository;
    private final CapaManagementService capaService;
    private final ApplicationEventPublisher eventPublisher;

    public ExternalQualityService(
            ExternalQualityEvaluationRepository repository,
            CapaManagementService capaService,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.capaService = capaService;
        this.eventPublisher = eventPublisher;
    }

    public ExternalQualityEvaluation createEvaluation(
            TenantId tenantId,
            String providerName,
            String programCode,
            String surveyCycle,
            UUID testDefinitionId,
            String sampleCode,
            double measuredValue,
            AuditMetadata audit) {

        ExternalQualityEvaluation evaluation = new ExternalQualityEvaluation(
                UUID.randomUUID(),
                tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString()),
                providerName,
                programCode,
                surveyCycle,
                testDefinitionId,
                sampleCode,
                measuredValue,
                audit
        );

        return repository.save(evaluation);
    }

    public ExternalQualityEvaluation getEvaluation(UUID evaluationId) {
        return repository.findById(evaluationId)
                .orElseThrow(() -> new ExternalQualityComplianceException("EQA_NOT_FOUND", "quality.error.evaluation_not_found", "External quality evaluation not found with ID: " + evaluationId));
    }

    public List<ExternalQualityEvaluation> listEvaluations(String programCode, String rating) {
        return repository.findAll(programCode, rating);
    }

    public ExternalQualityEvaluation scoreEvaluation(
            UUID evaluationId,
            double peerGroupMean,
            double peerGroupSd,
            Integer peerGroupCount,
            UUID storedDocumentId,
            AuditMetadata audit) {

        ExternalQualityEvaluation evaluation = getEvaluation(evaluationId);
        evaluation.applyScoring(peerGroupMean, peerGroupSd, peerGroupCount, storedDocumentId, audit);

        UUID capaId = null;
        if (evaluation.getPerformanceRating() == ExternalQualityEvaluation.Rating.UNACCEPTABLE
                || evaluation.getPerformanceRating() == ExternalQualityEvaluation.Rating.WARNING) {
            if (capaService != null) {
                try {
                    CapaInvestigation capa = capaService.createCapa(
                            evaluation.getTenantId(),
                            "EQA Non-Conformity: " + evaluation.getProgramCode() + " / " + evaluation.getSampleCode(),
                            "EXTERNAL_QUALITY_CONTROL",
                            evaluation.getEvaluationId().toString(),
                            UUID.randomUUID(),
                            LocalDate.now().plusDays(14),
                            audit
                    );
                    capaId = capa.getCapaId();
                    evaluation.linkCapa(capaId);
                } catch (Exception ignored) {
                }
            }
        }

        ExternalQualityEvaluation saved = repository.save(evaluation);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new ExternalQualityEvaluationScoredEvent(
                    saved.getEvaluationId(),
                    saved.getProgramCode(),
                    saved.getSurveyCycle(),
                    saved.getZScore() != null ? saved.getZScore() : 0.0,
                    saved.getPerformanceRating().name(),
                    saved.getCapaInvestigationId(),
                    Instant.now()
            ));
        }

        return saved;
    }
}
