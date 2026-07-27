package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web;

import com.nexora.hop.platformfoundation.externalqualitycompliance.application.QualityEventIntakeService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntake;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quality/events")
public class QualityEventIntakeController {

    private final QualityEventIntakeService service;

    public QualityEventIntakeController(QualityEventIntakeService service) {
        this.service = service;
    }

    @PostMapping("/intake")
    public ResponseEntity<QualityEventIntakeResponse> ingestQualityEvent(
            @RequestBody IngestQualityEventRequest request) {
        String source = request != null ? request.sourceSystem() : "";
        String type = request != null ? request.eventType() : "";
        String sev = request != null ? request.severity() : "MEDIUM";
        String title = request != null ? request.title() : type;
        String desc = request != null ? request.description() : "";
        String payload = request != null ? request.payloadJson() : "{}";

        QualityEventIntake event = service.ingestEvent(
                currentTenantId(),
                source,
                type,
                sev,
                title,
                desc,
                payload,
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(QualityEventIntakeResponse.from(event));
    }

    @GetMapping
    public ResponseEntity<List<QualityEventIntakeResponse>> listEvents(
            @RequestParam(required = false) String sourceSystem,
            @RequestParam(required = false) String severity) {
        List<QualityEventIntakeResponse> list = service.listEvents(sourceSystem, severity).stream()
                .map(QualityEventIntakeResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    private static TenantId currentTenantId() {
        return CurrentTenantContext.current()
                .map(TenantId::new)
                .orElseGet(() -> new TenantId(UUID.randomUUID().toString()));
    }

    public record IngestQualityEventRequest(
            String sourceSystem,
            String eventType,
            String severity,
            String title,
            String description,
            String payloadJson
    ) {}

    public record QualityEventIntakeResponse(
            UUID eventId,
            String sourceSystem,
            String eventType,
            String severity,
            String title,
            UUID capaId,
            Instant ingestedAt
    ) {
        static QualityEventIntakeResponse from(QualityEventIntake event) {
            return new QualityEventIntakeResponse(
                    event.getEventId(),
                    event.getSourceSystem(),
                    event.getEventType(),
                    event.getSeverity(),
                    event.getTitle(),
                    event.getCapaId(),
                    event.getIngestedAt()
            );
        }
    }
}
