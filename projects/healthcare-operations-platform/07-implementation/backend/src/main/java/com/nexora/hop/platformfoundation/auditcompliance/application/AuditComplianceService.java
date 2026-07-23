package com.nexora.hop.platformfoundation.auditcompliance.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;
import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Service
public class AuditComplianceService implements AuditRecorder {

    public static final String SYSTEM_ACTOR_ID = "system";
    public static final String SYSTEM_ACTOR_TYPE = "service";

    private final AuditEventRepository repository;
    private final DocumentManagementService documentService;
    private final Clock clock;

    @Autowired
    public AuditComplianceService(
            AuditEventRepository repository,
            @Autowired(required = false) DocumentManagementService documentService) {
        this(repository, documentService, Clock.systemUTC());
    }

    public AuditComplianceService(
            AuditEventRepository repository,
            DocumentManagementService documentService,
            Clock clock) {
        this.repository = repository;
        this.documentService = documentService;
        this.clock = clock;
    }

    public AuditEvent recordEvent(RecordAuditEventCommand command) {
        String actorId = requiredText(command.actorId(), "Actor id is required.");
        String actorType = requiredText(command.actorType(), "Actor type is required.");
        String action = requiredText(command.action(), "Action is required.");
        String subjectType = requiredText(command.subjectType(), "Subject type is required.");
        String subjectId = requiredText(command.subjectId(), "Subject id is required.");
        String metadataJson = StringUtils.hasText(command.metadataJson()) ? command.metadataJson().trim() : "{}";

        AuditEvent event = new AuditEvent(
                UUID.randomUUID().toString(),
                Instant.now(clock),
                trimToNull(command.tenantId()),
                actorId,
                actorType,
                action,
                subjectType,
                subjectId,
                metadataJson);
        return repository.append(event);
    }

    @Override
    public void recordSystemEvent(
            String tenantId,
            String action,
            String subjectType,
            String subjectId,
            String metadataJson) {
        recordEvent(new RecordAuditEventCommand(
                tenantId,
                SYSTEM_ACTOR_ID,
                SYSTEM_ACTOR_TYPE,
                action,
                subjectType,
                subjectId,
                metadataJson));
    }

    public List<AuditEvent> searchEvents(String tenantId, String subjectId) {
        return repository.search(trimToNull(tenantId), trimToNull(subjectId));
    }

    public List<AuditEvent> searchEventsFiltered(
            String tenantId,
            String subjectId,
            String category,
            String complianceCorrelationId,
            String qualityInvestigationId,
            Instant fromDate,
            Instant toDate) {
        List<AuditEvent> base = searchEvents(tenantId, subjectId);
        return base.stream()
                .filter(e -> !StringUtils.hasText(category) || (e.subjectType() != null && e.subjectType().equalsIgnoreCase(category.trim())))
                .filter(e -> !StringUtils.hasText(complianceCorrelationId) || (e.metadataJson() != null && e.metadataJson().contains(complianceCorrelationId.trim())))
                .filter(e -> !StringUtils.hasText(qualityInvestigationId) || (e.metadataJson() != null && e.metadataJson().contains(qualityInvestigationId.trim())))
                .filter(e -> fromDate == null || (e.occurredAt() != null && !e.occurredAt().isBefore(fromDate)))
                .filter(e -> toDate == null || (e.occurredAt() != null && !e.occurredAt().isAfter(toDate)))
                .toList();
    }

    public AuditExportResult exportAuditEvents(
            String category,
            String complianceCorrelationId,
            Instant fromDate,
            Instant toDate,
            String format) {
        List<AuditEvent> events = searchEventsFiltered(null, null, category, complianceCorrelationId, null, fromDate, toDate);
        String formatType = StringUtils.hasText(format) ? format.toLowerCase().trim() : "csv";

        StringBuilder sb = new StringBuilder();
        if ("json".equals(formatType)) {
            sb.append("[\n");
            for (int i = 0; i < events.size(); i++) {
                AuditEvent e = events.get(i);
                sb.append("  {\"eventId\":\"").append(e.auditEventId())
                        .append("\",\"occurredAt\":\"").append(e.occurredAt())
                        .append("\",\"action\":\"").append(e.action())
                        .append("\",\"subjectType\":\"").append(e.subjectType())
                        .append("\",\"subjectId\":\"").append(e.subjectId()).append("\"}");
                if (i < events.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
        } else {
            sb.append("auditEventId,occurredAt,action,subjectType,subjectId,tenantId,actorId\n");
            for (AuditEvent e : events) {
                sb.append(e.auditEventId()).append(",")
                        .append(e.occurredAt()).append(",")
                        .append(e.action()).append(",")
                        .append(e.subjectType()).append(",")
                        .append(e.subjectId()).append(",")
                        .append(e.tenantId()).append(",")
                        .append(e.actorId()).append("\n");
            }
        }

        byte[] exportBytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        UUID storedDocId = UUID.randomUUID();

        if (documentService != null) {
            try {
                var doc = documentService.uploadDocument(
                        null,
                        null,
                        "AUDIT_TRAIL",
                        storedDocId,
                        1,
                        exportBytes,
                        "json".equals(formatType) ? "application/json" : "text/csv",
                        "AUDIT_EXPORT",
                        null,
                        new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
                );
                storedDocId = doc.getDocumentId();
            } catch (Exception ignored) {
            }
        }

        return new AuditExportResult(UUID.randomUUID(), events.size(), storedDocId, Instant.now(clock));
    }

    public record AuditExportResult(
            UUID exportId,
            int recordCount,
            UUID storedDocumentId,
            Instant generatedAt
    ) {}

    private static String requiredText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidAuditCommandException(message);
        }
        return value.trim();
    }

    private static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
