package com.nexora.hop.platformfoundation.externalqualitycompliance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web.AuditManagementController;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web.CapaManagementController;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web.ExternalQualityController;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.in.web.QualityEventIntakeController;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryAuditScheduleRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryCapaInvestigationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryExternalQualityEvaluationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory.InMemoryQualityEventIntakeRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.AuditManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.CapaManagementService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.ExternalQualityService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.application.QualityEventIntakeService;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExternalQualityComplianceControllerTest {

    private MockMvc mockMvcEqa;
    private MockMvc mockMvcCapa;
    private MockMvc mockMvcAudit;
    private MockMvc mockMvcIntake;

    private ExternalQualityService eqaService;
    private CapaManagementService capaService;
    private AuditManagementService auditService;
    private QualityEventIntakeService intakeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuditMetadata createAudit() {
        return new AuditMetadata("admin", LocalDateTime.now(), "admin", LocalDateTime.now());
    }

    private TenantId randomTenant() {
        return new TenantId(UUID.randomUUID().toString());
    }

    @BeforeEach
    void setUp() {
        var eqaRepo = new InMemoryExternalQualityEvaluationRepository();
        var capaRepo = new InMemoryCapaInvestigationRepository();
        var auditRepo = new InMemoryAuditScheduleRepository();
        var intakeRepo = new InMemoryQualityEventIntakeRepository();

        capaService = new CapaManagementService(capaRepo, null);
        eqaService = new ExternalQualityService(eqaRepo, capaService, null);
        auditService = new AuditManagementService(auditRepo, capaService, null);
        intakeService = new QualityEventIntakeService(intakeRepo, capaService, null);

        mockMvcEqa = MockMvcBuilders.standaloneSetup(new ExternalQualityController(eqaService)).build();
        mockMvcCapa = MockMvcBuilders.standaloneSetup(new CapaManagementController(capaService)).build();
        mockMvcAudit = MockMvcBuilders.standaloneSetup(new AuditManagementController(auditService)).build();
        mockMvcIntake = MockMvcBuilders.standaloneSetup(new QualityEventIntakeController(intakeService)).build();
    }

    @Test
    void testExternalQualityEndpoints() throws Exception {
        ExternalQualityController.CreateExternalQualityEvaluationRequest createReq =
                new ExternalQualityController.CreateExternalQualityEvaluationRequest(
                        "CAP Program", "CHEM-01", "2026-Q1", UUID.randomUUID(), "SMP-101", 110.0);

        String createJson = objectMapper.writeValueAsString(createReq);

        String responseContent = mockMvcEqa.perform(post("/api/quality/external-controls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.programCode").value("CHEM-01"))
                .andExpect(jsonPath("$.performanceRating").value("pending_evaluation"))
                .andReturn().getResponse().getContentAsString();

        String evalId = objectMapper.readTree(responseContent).get("evaluationId").asText();

        ExternalQualityController.ScoreExternalQualityEvaluationRequest scoreReq =
                new ExternalQualityController.ScoreExternalQualityEvaluationRequest(100.0, 4.0, 50, null);

        mockMvcEqa.perform(put("/api/quality/external-controls/" + evalId + "/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zScore").value(2.5))
                .andExpect(jsonPath("$.performanceRating").value("warning"));

        mockMvcEqa.perform(get("/api/quality/external-controls")
                        .param("programCode", "CHEM-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evaluationId").value(evalId));
    }

    @Test
    void testCapaEndpoints() throws Exception {
        CapaInvestigation capa = capaService.createCapa(
                randomTenant(),
                "Reagent Contamination CAPA",
                "INTERNAL_QC",
                "QC-99",
                UUID.randomUUID(),
                LocalDate.now().plusDays(15),
                createAudit()
        );

        mockMvcCapa.perform(get("/api/quality/capa/" + capa.getCapaId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Reagent Contamination CAPA"));

        CapaManagementController.RecordRcaRequest rcaReq =
                new CapaManagementController.RecordRcaRequest("FISHBONE", "Batch lot contaminated by seal leakage");

        mockMvcCapa.perform(put("/api/quality/capa/" + capa.getCapaId() + "/rca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rcaReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("rca_completed"));

        mockMvcCapa.perform(post("/api/quality/capa/" + capa.getCapaId() + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("plan_approved"));

        CapaManagementController.VerifyEffectivenessRequest verifyReq =
                new CapaManagementController.VerifyEffectivenessRequest("effective", "Seals replaced and lot re-tested");

        mockMvcCapa.perform(post("/api/quality/capa/" + capa.getCapaId() + "/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("closed"))
                .andExpect(jsonPath("$.effectivenessRating").value("effective"));
    }

    @Test
    void testAuditEndpoints() throws Exception {
        AuditSchedule audit = auditService.createAuditSchedule(
                randomTenant(),
                "Internal Safety Audit",
                "INTERNAL",
                "ISO 15189",
                UUID.randomUUID(),
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                createAudit()
        );

        AuditManagementController.RecordAuditFindingRequest findingReq =
                new AuditManagementController.RecordAuditFindingRequest("5.4", "major", "Eyewash station pressure low", "EV-02");

        mockMvcAudit.perform(post("/api/quality/audits/" + audit.getAuditId() + "/findings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(findingReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("in_progress"));

        mockMvcAudit.perform(post("/api/quality/audits/" + audit.getAuditId() + "/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("closed"));
    }

    @Test
    void testQualityEventIntakeEndpoints() throws Exception {
        QualityEventIntakeController.IngestQualityEventRequest req =
                new QualityEventIntakeController.IngestQualityEventRequest(
                        "LIS_ENGINE",
                        "SAMPLE_SPILL",
                        "HIGH",
                        "Biohazard Spill in Processing Bay 2",
                        "Sample tube cap dislodged inside centrifuge bucket",
                        "{\"sampleCode\":\"SMP-77\"}"
                );

        mockMvcIntake.perform(post("/api/quality/events/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.capaId").exists());

        mockMvcIntake.perform(get("/api/quality/events")
                        .param("severity", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity").value("HIGH"));
    }
}
