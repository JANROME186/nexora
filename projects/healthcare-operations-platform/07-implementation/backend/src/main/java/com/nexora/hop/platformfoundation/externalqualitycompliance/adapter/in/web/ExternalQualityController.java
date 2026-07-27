package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web;

import com.nexora.hop.platformfoundation.externalqualitycompliance.application.ExternalQualityService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quality/external-controls")
public class ExternalQualityController {

    private final ExternalQualityService service;

    public ExternalQualityController(ExternalQualityService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExternalQualityEvaluationResponse>> listExternalQualityEvaluations(
            @RequestParam(required = false) String programCode,
            @RequestParam(required = false) String rating) {
        List<ExternalQualityEvaluationResponse> list = service.listEvaluations(programCode, rating).stream()
                .map(ExternalQualityEvaluationResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ExternalQualityEvaluationResponse> createExternalQualityEvaluation(
            @RequestBody CreateExternalQualityEvaluationRequest request) {
        String provider = request != null ? request.providerName() : "";
        String program = request != null ? request.programCode() : "";
        String cycle = request != null ? request.surveyCycle() : "";
        UUID testDefId = request != null && request.testDefinitionId() != null ? request.testDefinitionId() : UUID.randomUUID();
        String sample = request != null ? request.sampleCode() : "";
        double val = request != null ? request.measuredValue() : 0.0;

        ExternalQualityEvaluation eval = service.createEvaluation(
                currentTenantId(),
                provider,
                program,
                cycle,
                testDefId,
                sample,
                val,
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ExternalQualityEvaluationResponse.from(eval));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<ExternalQualityEvaluationResponse> scoreExternalQualityEvaluation(
            @PathVariable UUID id,
            @RequestBody ScoreExternalQualityEvaluationRequest request) {
        double mean = request != null ? request.peerGroupMean() : 0.0;
        double sd = request != null ? request.peerGroupSd() : 1.0;
        Integer count = request != null ? request.peerGroupCount() : 10;
        UUID docId = request != null ? request.storedDocumentId() : null;

        ExternalQualityEvaluation eval = service.scoreEvaluation(id, mean, sd, count, docId, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.ok(ExternalQualityEvaluationResponse.from(eval));
    }

    private static TenantId currentTenantId() {
        return CurrentTenantContext.current()
                .map(TenantId::new)
                .orElseGet(() -> new TenantId(UUID.randomUUID().toString()));
    }

    public record CreateExternalQualityEvaluationRequest(
            String providerName,
            String programCode,
            String surveyCycle,
            UUID testDefinitionId,
            String sampleCode,
            double measuredValue
    ) {}

    public record ScoreExternalQualityEvaluationRequest(
            double peerGroupMean,
            double peerGroupSd,
            Integer peerGroupCount,
            UUID storedDocumentId
    ) {}

    public record ExternalQualityEvaluationResponse(
            UUID evaluationId,
            String providerName,
            String programCode,
            String surveyCycle,
            String sampleCode,
            double measuredValue,
            Double zScore,
            String performanceRating,
            UUID capaInvestigationId,
            Instant evaluatedAt
    ) {
        static ExternalQualityEvaluationResponse from(ExternalQualityEvaluation eval) {
            return new ExternalQualityEvaluationResponse(
                    eval.getEvaluationId(),
                    eval.getProviderName(),
                    eval.getProgramCode(),
                    eval.getSurveyCycle(),
                    eval.getSampleCode(),
                    eval.getMeasuredValue(),
                    eval.getZScore(),
                    eval.getPerformanceRating().name().toLowerCase(),
                    eval.getCapaInvestigationId(),
                    eval.getEvaluatedAt()
            );
        }
    }
}
