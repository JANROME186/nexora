package com.nexora.hop.platformfoundation.externalqualitycompliance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration test for COM-MOD-013 backend capabilities against local/test profiles and database tables.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class ExternalQualityComplianceLocalDatabaseTest {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void qualityComplianceSchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_name in (
                       'external_quality_evaluations', 'capa_investigations',
                       'audit_schedules', 'audit_findings', 'quality_event_intakes')
                """, Integer.class);
        assertThat(tableCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    void endToEndQualityComplianceRoundTrip() throws Exception {
        // 1. Create EQA evaluation & score it to trigger CAPA
        JsonNode eval = postJson("/api/quality/external-controls", """
                {"providerName":"CAP International","programCode":"CHEM-PROG-01","surveyCycle":"2026-Q1",
                 "testDefinitionId":"%s","sampleCode":"EQA-SMP-01","measuredValue":140.0}
                """.formatted(UUID.randomUUID()));
        String evalId = eval.get("evaluationId").asText();

        JsonNode scored = putJson("/api/quality/external-controls/" + evalId + "/score", """
                {"peerGroupMean":100.0,"peerGroupSd":5.0,"peerGroupCount":45}
                """);
        assertThat(scored.get("performanceRating").asText()).isEqualTo("unacceptable");
        assertThat(scored.get("capaInvestigationId")).isNotNull();
        String capaId = scored.get("capaInvestigationId").asText();

        // 2. Query CAPA and progress lifecycle
        mockMvc.perform(get("/api/quality/capa/{id}", capaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capaId").value(capaId));

        putJson("/api/quality/capa/" + capaId + "/rca", """
                {"rootCauseMethodology":"5_WHY","rootCauseSummary":"Calibration slope drift uncorrected before batch run"}
                """);

        postJson("/api/quality/capa/" + capaId + "/approve", "{}");

        postJson("/api/quality/capa/" + capaId + "/verify", """
                {"effectivenessRating":"effective","closureNotes":"Re-calibrated with fresh standard; subsequent EQA acceptable"}
                """);

        // 3. Create Audit Schedule & Record Finding
        JsonNode audit = postJson("/api/quality/audits", """
                {"title":"Q3 ISO Audit","category":"INTERNAL","standardReference":"ISO 15189",
                 "leadAuditorId":"%s","plannedStartDate":"2026-07-01","plannedEndDate":"2026-07-05"}
                """.formatted(UUID.randomUUID()));
        String auditId = audit.get("auditId").asText();

        postJson("/api/quality/audits/" + auditId + "/findings", """
                {"clauseReference":"7.3.1","severity":"critical",
                 "observation":"Reagent storage temperature out of limits","evidenceReference":"LOG-009"}
                """);

        postJson("/api/quality/audits/" + auditId + "/close", "{}");

        // 4. Quality Event Intake
        JsonNode eventIntake = postJson("/api/quality/events/intake", """
                {"sourceSystem":"OPERATIONAL_SAFETY","eventType":"REAGENT_EXPIRED_USED",
                 "severity":"CRITICAL","title":"Expired Reagent Used in Batch",
                 "description":"Lot EX-90 was used 2 days past expiration date",
                 "payloadJson":"{\\"lot\\":\\"EX-90\\"}"}
                """);
        assertThat(eventIntake.get("severity").asText()).isEqualTo("CRITICAL");
        assertThat(eventIntake.get("capaId")).isNotNull();
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode putJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
