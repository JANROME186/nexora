package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web;

import com.nexora.hop.platformfoundation.externalqualitycompliance.application.AuditManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quality/audits")
public class AuditManagementController {

    private final AuditManagementService service;

    public AuditManagementController(AuditManagementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AuditScheduleResponse>> listAuditSchedules(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        List<AuditScheduleResponse> list = service.listAuditSchedules(category, status).stream()
                .map(AuditScheduleResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<AuditScheduleResponse> createAuditSchedule(
            @RequestBody CreateAuditScheduleRequest request) {
        String title = request != null ? request.title() : "";
        String cat = request != null ? request.category() : "INTERNAL";
        String std = request != null ? request.standardReference() : "ISO 15189";
        UUID leadId = request != null && request.leadAuditorId() != null ? request.leadAuditorId() : UUID.randomUUID();
        LocalDate start = request != null ? request.plannedStartDate() : LocalDate.now();
        LocalDate end = request != null ? request.plannedEndDate() : LocalDate.now().plusDays(7);

        AuditSchedule audit = service.createAuditSchedule(
                currentTenantId(),
                title,
                cat,
                std,
                leadId,
                start,
                end,
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(AuditScheduleResponse.from(audit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditScheduleResponse> getAuditSchedule(@PathVariable UUID id) {
        AuditSchedule audit = service.getAuditSchedule(id);
        return ResponseEntity.ok(AuditScheduleResponse.from(audit));
    }

    @PostMapping("/{id}/findings")
    public ResponseEntity<AuditScheduleResponse> recordAuditFinding(
            @PathVariable UUID id,
            @RequestBody RecordAuditFindingRequest request) {
        String clause = request != null ? request.clauseReference() : "";
        String severity = request != null ? request.severity() : "minor";
        String obs = request != null ? request.observation() : "";
        String ev = request != null ? request.evidenceReference() : "";

        AuditSchedule audit = service.recordAuditFinding(id, clause, severity, obs, ev, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.CREATED).body(AuditScheduleResponse.from(audit));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<AuditScheduleResponse> closeAuditSchedule(@PathVariable UUID id) {
        AuditSchedule audit = service.closeAuditSchedule(id, new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now()));
        return ResponseEntity.ok(AuditScheduleResponse.from(audit));
    }

    private static TenantId currentTenantId() {
        return CurrentTenantContext.current()
                .map(TenantId::new)
                .orElseGet(() -> new TenantId(UUID.randomUUID().toString()));
    }

    public record CreateAuditScheduleRequest(
            String title,
            String category,
            String standardReference,
            UUID leadAuditorId,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate
    ) {}

    public record RecordAuditFindingRequest(
            String clauseReference,
            String severity,
            String observation,
            String evidenceReference
    ) {}

    public record AuditScheduleResponse(
            UUID auditId,
            String auditCode,
            String title,
            String category,
            String status,
            UUID leadAuditorId,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate
    ) {
        static AuditScheduleResponse from(AuditSchedule audit) {
            return new AuditScheduleResponse(
                    audit.getAuditId(),
                    audit.getAuditCode(),
                    audit.getTitle(),
                    audit.getCategory(),
                    audit.getStatus().name().toLowerCase(),
                    audit.getLeadAuditorId(),
                    audit.getPlannedStartDate(),
                    audit.getPlannedEndDate()
            );
        }
    }
}
