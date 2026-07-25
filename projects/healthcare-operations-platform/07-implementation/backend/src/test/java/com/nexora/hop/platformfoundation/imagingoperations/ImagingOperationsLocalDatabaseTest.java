package com.nexora.hop.platformfoundation.imagingoperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class ImagingOperationsLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void imagingOperationsSchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'imaging_operations'
                   and table_name in (
                       'imaging_appointment_slots', 'imaging_reception_intakes', 'imaging_studies',
                       'dicom_adapter_configurations', 'pacs_integration_endpoints', 'radiology_dictations',
                       'radiology_reports', 'imaging_delivery_packages')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(8);
    }

    @Test
    void fullImagingWorkflowRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);

        // 1. Create Study
        JsonNode study = postJson("/api/v1/imaging/studies", """
                {
                    "accessionNumber": "ACC-PG-%s",
                    "patientId": "pat-pg-100",
                    "modality": "CT",
                    "studyDescription": "CHEST CT WITH CONTRAST"
                }
                """.formatted(token), "tenant-pg-01");
        String studyId = study.get("studyId").asText();
        assertThat(studyId).isNotNull();

        // 2. Query Study
        MvcResult getStudyResult = mockMvc.perform(get("/api/v1/imaging/studies/" + studyId)
                        .header("X-Tenant-Id", "tenant-pg-01"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode fetchedStudy = objectMapper.readTree(getStudyResult.getResponse().getContentAsString());
        assertThat(fetchedStudy.get("accessionNumber").asText()).isEqualTo("ACC-PG-" + token);

        // 3. DICOM Config
        JsonNode dicomConfig = postJson("/api/v1/imaging/dicom-configs", """
                {
                    "aeTitle": "AE_PG_%s",
                    "host": "10.0.0.50",
                    "port": 104,
                    "modalityType": "CT"
                }
                """.formatted(token), "tenant-pg-01");
        assertThat(dicomConfig.get("configurationId").asText()).isNotNull();

        // 4. Dictation & Report
        JsonNode dictation = postJson("/api/v1/imaging/dictations", """
                {
                    "studyId": "%s",
                    "dictationText": "Postgres integration dictation text.",
                    "audioReferenceUrl": "s3://audio/pg-%s.mp3"
                }
                """.formatted(studyId, token), "tenant-pg-01");
        assertThat(dictation.get("dictationId").asText()).isNotNull();

        JsonNode report = postJson("/api/v1/imaging/reports", """
                {
                    "studyId": "%s",
                    "findingsText": "Lungs clear.",
                    "impressionText": "Normal chest CT."
                }
                """.formatted(studyId), "tenant-pg-01");
        String reportId = report.get("reportId").asText();
        assertThat(reportId).isNotNull();

        // Sign Report
        MvcResult signResult = mockMvc.perform(post("/api/v1/imaging/reports/" + reportId + "/sign")
                        .header("X-Tenant-Id", "tenant-pg-01")
                        .header("X-User-Id", "rad-pg-01"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode signedReport = objectMapper.readTree(signResult.getResponse().getContentAsString());
        assertThat(signedReport.get("reportStatus").asText()).isEqualTo("FINAL_SIGNED");

        // 5. Delivery Package
        JsonNode deliveryPkg = postJson("/api/v1/imaging/delivery-packages", """
                {
                    "studyId": "%s",
                    "patientId": "pat-pg-100",
                    "deliveryFormat": "PATIENT_PORTAL_LINK"
                }
                """.formatted(studyId), "tenant-pg-01");
        assertThat(deliveryPkg.get("packageId").asText()).isNotNull();
    }

    private JsonNode postJson(String url, String body, String tenantId) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .header("X-Tenant-Id", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
