package com.nexora.hop.platformfoundation.auditcompliance.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;

@Service
public class AuditComplianceService implements AuditRecorder {

    public static final String SYSTEM_ACTOR_ID = "system";
    public static final String SYSTEM_ACTOR_TYPE = "service";

    private final AuditEventRepository repository;
    private final Clock clock;

    @Autowired
    public AuditComplianceService(AuditEventRepository repository) {
        this(repository, Clock.systemUTC());
    }

    private AuditComplianceService(AuditEventRepository repository, Clock clock) {
        this.repository = repository;
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
