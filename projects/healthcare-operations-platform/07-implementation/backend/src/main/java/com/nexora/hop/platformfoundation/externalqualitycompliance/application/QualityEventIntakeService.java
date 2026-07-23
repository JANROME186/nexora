package com.nexora.hop.platformfoundation.externalqualitycompliance.application;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntake;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntakeRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events.QualityEventIngestedEvent;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class QualityEventIntakeService {

    private final QualityEventIntakeRepository repository;
    private final CapaManagementService capaService;
    private final ApplicationEventPublisher eventPublisher;

    public QualityEventIntakeService(
            QualityEventIntakeRepository repository,
            CapaManagementService capaService,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.capaService = capaService;
        this.eventPublisher = eventPublisher;
    }

    public QualityEventIntake ingestEvent(
            TenantId tenantId,
            String sourceSystem,
            String eventType,
            String severity,
            String title,
            String description,
            String payloadJson,
            AuditMetadata audit) {

        QualityEventIntake event = new QualityEventIntake(
                UUID.randomUUID(),
                tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString()),
                sourceSystem,
                eventType,
                severity,
                title,
                description,
                payloadJson,
                null,
                Instant.now(),
                audit
        );

        UUID capaId = null;
        if ("HIGH".equalsIgnoreCase(event.getSeverity()) || "CRITICAL".equalsIgnoreCase(event.getSeverity())) {
            if (capaService != null) {
                try {
                    CapaInvestigation capa = capaService.createCapa(
                            event.getTenantId(),
                            "Quality Investigation (" + event.getSeverity() + "): " + event.getTitle(),
                            "CLINICAL_OPERATIONAL_EVENT",
                            event.getEventId().toString(),
                            UUID.randomUUID(),
                            LocalDate.now().plusDays(10),
                            audit
                    );
                    capaId = capa.getCapaId();
                    event.linkCapa(capaId);
                } catch (Exception ignored) {
                }
            }
        }

        QualityEventIntake saved = repository.save(event);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new QualityEventIngestedEvent(
                    saved.getEventId(),
                    saved.getSourceSystem(),
                    saved.getEventType(),
                    saved.getSeverity(),
                    saved.getCapaId(),
                    Instant.now()
            ));
        }

        return saved;
    }

    public List<QualityEventIntake> listEvents(String sourceSystem, String severity) {
        return repository.findAll(sourceSystem, severity);
    }
}
