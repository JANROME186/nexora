package com.nexora.hop.platformfoundation.externalqualitycompliance;

import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryAuditScheduleRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryCapaInvestigationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryExternalQualityEvaluationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryQualityEventIntakeRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.AuditManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.CapaManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.ExternalQualityService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.QualityEventIntakeService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditFinding;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityComplianceException;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntake;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalQualityComplianceServiceTest {

    private InMemoryExternalQualityEvaluationRepository eqaRepo;
    private InMemoryCapaInvestigationRepository capaRepo;
    private InMemoryAuditScheduleRepository auditRepo;
    private InMemoryQualityEventIntakeRepository intakeRepo;

    private CapaManagementService capaService;
    private ExternalQualityService eqaService;
    private AuditManagementService auditService;
    private QualityEventIntakeService intakeService;

    private AuditMetadata createAudit() {
        return new AuditMetadata("tester", LocalDateTime.now(), "tester", LocalDateTime.now());
    }

    private TenantId randomTenant() {
        return new TenantId(UUID.randomUUID().toString());
    }

    @BeforeEach
    void setUp() {
        eqaRepo = new InMemoryExternalQualityEvaluationRepository();
        capaRepo = new InMemoryCapaInvestigationRepository();
        auditRepo = new InMemoryAuditScheduleRepository();
        intakeRepo = new InMemoryQualityEventIntakeRepository();

        capaService = new CapaManagementService(capaRepo, null);
        eqaService = new ExternalQualityService(eqaRepo, capaService, null);
        auditService = new AuditManagementService(auditRepo, capaService, null);
        intakeService = new QualityEventIntakeService(intakeRepo, capaService, null);
    }

    @Test
    void testExternalQualityControlScoringAndAutoCapaTrigger() {
        ExternalQualityEvaluation eval = eqaService.createEvaluation(
                randomTenant(),
                "BioRad CAP",
                "CHEM-001",
                "2026-Q1",
                UUID.randomUUID(),
                "SAMPLE-A",
                125.0,
                createAudit()
        );

        assertThat(eval.getPerformanceRating()).isEqualTo(ExternalQualityEvaluation.Rating.PENDING_EVALUATION);

        ExternalQualityEvaluation scored = eqaService.scoreEvaluation(
                eval.getEvaluationId(),
                100.0,
                5.0,
                50,
                UUID.randomUUID(),
                createAudit()
        );

        assertThat(scored.getZScore()).isEqualTo(5.0);
        assertThat(scored.getPerformanceRating()).isEqualTo(ExternalQualityEvaluation.Rating.UNACCEPTABLE);
        assertThat(scored.getCapaInvestigationId()).isNotNull();

        CapaInvestigation triggeredCapa = capaService.getCapa(scored.getCapaInvestigationId());
        assertThat(triggeredCapa).isNotNull();
        assertThat(triggeredCapa.getSourceCategory()).isEqualTo("EXTERNAL_QUALITY_CONTROL");
        assertThat(triggeredCapa.getSourceReferenceId()).isEqualTo(eval.getEvaluationId().toString());
    }

    @Test
    void testExternalQualityControlScoringAcceptableRating() {
        ExternalQualityEvaluation eval = eqaService.createEvaluation(
                randomTenant(),
                "BioRad CAP",
                "HEMA-002",
                "2026-Q1",
                UUID.randomUUID(),
                "SAMPLE-B",
                102.0,
                createAudit()
        );

        ExternalQualityEvaluation scored = eqaService.scoreEvaluation(
                eval.getEvaluationId(),
                100.0,
                2.0,
                40,
                null,
                createAudit()
        );

        assertThat(scored.getZScore()).isEqualTo(1.0);
        assertThat(scored.getPerformanceRating()).isEqualTo(ExternalQualityEvaluation.Rating.ACCEPTABLE);
        assertThat(scored.getCapaInvestigationId()).isNull();
    }

    @Test
    void testCapaInvestigationLifecycle() {
        CapaInvestigation capa = capaService.createCapa(
                randomTenant(),
                "Temperature Excursion Investigation",
                "OPERATIONAL_EVENT",
                "EVT-9081",
                UUID.randomUUID(),
                LocalDate.now().plusDays(20),
                createAudit()
        );

        assertThat(capa.getStatus()).isEqualTo(CapaInvestigation.Status.INITIATED);

        CapaInvestigation rcaRecorded = capaService.recordRootCauseAnalysis(
                capa.getCapaId(),
                "5_WHY",
                "Cooling compressor sensor failure caused temporary delta +4C",
                createAudit()
        );
        assertThat(rcaRecorded.getStatus()).isEqualTo(CapaInvestigation.Status.RCA_COMPLETED);

        CapaInvestigation approved = capaService.approveActionPlan(capa.getCapaId(), createAudit());
        assertThat(approved.getStatus()).isEqualTo(CapaInvestigation.Status.PLAN_APPROVED);

        CapaInvestigation verified = capaService.verifyEffectiveness(
                capa.getCapaId(),
                "effective",
                "Sensor replaced, 14-day stability log confirmed inside tolerance",
                createAudit()
        );
        assertThat(verified.getStatus()).isEqualTo(CapaInvestigation.Status.CLOSED);
        assertThat(verified.getEffectivenessRating()).isEqualTo(CapaInvestigation.EffectivenessRating.EFFECTIVE);
    }

    @Test
    void testAuditScheduleAndCriticalFindingAutoCapa() {
        AuditSchedule audit = auditService.createAuditSchedule(
                randomTenant(),
                "Annual ISO 15189 Accreditation Audit",
                "REGULATORY",
                "ISO 15189:2022",
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                createAudit()
        );

        assertThat(audit.getStatus()).isEqualTo(AuditSchedule.Status.PLANNED);

        AuditSchedule updated = auditService.recordAuditFinding(
                audit.getAuditId(),
                "Clause 5.3",
                "critical",
                "Reagent storage temperature log unverified for 7 days",
                "EV-AUD-01",
                createAudit()
        );

        assertThat(updated.getStatus()).isEqualTo(AuditSchedule.Status.IN_PROGRESS);
        assertThat(updated.getFindings()).hasSize(1);
        AuditFinding finding = updated.getFindings().get(0);
        assertThat(finding.getSeverity()).isEqualTo(AuditFinding.Severity.CRITICAL);
        assertThat(finding.getCapaId()).isNotNull();

        AuditSchedule closed = auditService.closeAuditSchedule(audit.getAuditId(), createAudit());
        assertThat(closed.getStatus()).isEqualTo(AuditSchedule.Status.CLOSED);
    }

    @Test
    void testQualityEventIntakeAndAutoCapa() {
        QualityEventIntake event = intakeService.ingestEvent(
                randomTenant(),
                "LAB_EQUIPMENT_MONITOR",
                "CRITICAL_EQUIPMENT_BREAKDOWN",
                "CRITICAL",
                "Centrifuge Rotor Imbalance Malfunction",
                "Vibration sensor triggered emergency stop during specimen spin",
                "{\"rpm\":4500,\"vibration_g\":8.2}",
                createAudit()
        );

        assertThat(event.getSeverity()).isEqualTo("CRITICAL");
        assertThat(event.getCapaId()).isNotNull();

        List<QualityEventIntake> list = intakeService.listEvents("LAB_EQUIPMENT_MONITOR", "CRITICAL");
        assertThat(list).hasSize(1);
    }

    @Test
    void testValidationErrorsHandling() {
        assertThatThrownBy(() -> eqaService.createEvaluation(randomTenant(), "", "PROG", "Q1", UUID.randomUUID(), "S1", 10.0, null))
                .isInstanceOf(ExternalQualityComplianceException.class);

        assertThatThrownBy(() -> capaService.createCapa(randomTenant(), "", "CAT", "REF", UUID.randomUUID(), null, null))
                .isInstanceOf(ExternalQualityComplianceException.class);

        assertThatThrownBy(() -> auditService.createAuditSchedule(randomTenant(), "", "CAT", "STD", UUID.randomUUID(), null, null, null))
                .isInstanceOf(ExternalQualityComplianceException.class);
    }
}
