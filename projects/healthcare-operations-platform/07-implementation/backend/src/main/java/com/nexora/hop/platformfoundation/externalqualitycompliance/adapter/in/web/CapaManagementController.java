package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web;

import com.nexora.hop.platformfoundation.externalqualitycompliance.application.CapaManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quality/capa")
public class CapaManagementController {

    private final CapaManagementService service;

    public CapaManagementController(CapaManagementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CapaInvestigationResponse>> listCapaInvestigations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sourceCategory) {
        List<CapaInvestigationResponse> list = service.listCapas(status, sourceCategory).stream()
                .map(CapaInvestigationResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CapaInvestigationResponse> createCapaInvestigation(
            @RequestBody CreateCapaInvestigationRequest request) {
        String title = request != null ? request.title() : "";
        String cat = request != null ? request.sourceCategory() : "";
        String ref = request != null ? request.sourceReferenceId() : "";
        UUID invId = request != null && request.assignedInvestigatorId() != null ? request.assignedInvestigatorId() : UUID.randomUUID();
        LocalDate targetDate = request != null ? request.targetCompletionDate() : LocalDate.now().plusDays(30);

        CapaInvestigation capa = service.createCapa(
                currentTenantId(),
                title,
                cat,
                ref,
                invId,
                targetDate,
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(CapaInvestigationResponse.from(capa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapaInvestigationResponse> getCapaInvestigation(@PathVariable UUID id) {
        CapaInvestigation capa = service.getCapa(id);
        return ResponseEntity.ok(CapaInvestigationResponse.from(capa));
    }

    @PutMapping("/{id}/rca")
    public ResponseEntity<CapaInvestigationResponse> recordRootCauseAnalysis(
            @PathVariable UUID id,
            @RequestBody RecordRcaRequest request) {
        String methodology = request != null ? request.rootCauseMethodology() : "5_WHY";
        String summary = request != null ? request.rootCauseSummary() : "";

        CapaInvestigation capa = service.recordRootCauseAnalysis(id, methodology, summary, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.ok(CapaInvestigationResponse.from(capa));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CapaInvestigationResponse> approveCapaActionPlan(@PathVariable UUID id) {
        CapaInvestigation capa = service.approveActionPlan(id, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.ok(CapaInvestigationResponse.from(capa));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<CapaInvestigationResponse> verifyCapaEffectiveness(
            @PathVariable UUID id,
            @RequestBody VerifyEffectivenessRequest request) {
        String rating = request != null ? request.effectivenessRating() : "effective";
        String notes = request != null ? request.closureNotes() : "";

        CapaInvestigation capa = service.verifyEffectiveness(id, rating, notes, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.ok(CapaInvestigationResponse.from(capa));
    }

    private static TenantId currentTenantId() {
        return CurrentTenantContext.current()
                .map(TenantId::new)
                .orElseGet(() -> new TenantId(UUID.randomUUID().toString()));
    }

    public record CreateCapaInvestigationRequest(
            String title,
            String sourceCategory,
            String sourceReferenceId,
            UUID assignedInvestigatorId,
            LocalDate targetCompletionDate
    ) {}

    public record RecordRcaRequest(
            String rootCauseMethodology,
            String rootCauseSummary
    ) {}

    public record VerifyEffectivenessRequest(
            String effectivenessRating,
            String closureNotes
    ) {}

    public record CapaInvestigationResponse(
            UUID capaId,
            String capaNumber,
            String title,
            String sourceCategory,
            String status,
            UUID assignedInvestigatorId,
            LocalDate targetCompletionDate,
            String effectivenessRating
    ) {
        static CapaInvestigationResponse from(CapaInvestigation capa) {
            return new CapaInvestigationResponse(
                    capa.getCapaId(),
                    capa.getCapaNumber(),
                    capa.getTitle(),
                    capa.getSourceCategory(),
                    capa.getStatus().name().toLowerCase(),
                    capa.getAssignedInvestigatorId(),
                    capa.getTargetCompletionDate(),
                    capa.getEffectivenessRating().name().toLowerCase()
            );
        }
    }
}
