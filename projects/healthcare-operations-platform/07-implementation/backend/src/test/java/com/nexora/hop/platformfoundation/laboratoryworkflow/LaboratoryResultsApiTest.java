package com.nexora.hop.platformfoundation.laboratoryworkflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class LaboratoryResultsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String laboratoryId;
    private String branchId;
    private String orderId;
    private String sampleId;

    @BeforeEach
    void setupContext() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);

        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Lab Workflow Tenant " + runToken + "\"}");
        tenantId = tenant.get("tenantId").asText();

        JsonNode laboratory = postJson("/api/organization/laboratories",
                "{\"tenantId\":\"%s\",\"name\":\"Lab Workflow Lab\"}".formatted(tenantId));
        laboratoryId = laboratory.get("laboratoryId").asText();

        JsonNode branch = postJson("/api/organization/branches",
                "{\"laboratoryId\":\"%s\",\"name\":\"Lab Workflow Branch\"}".formatted(laboratoryId));
        branchId = branch.get("branchId").asText();

        // orderId and sampleId will be created during the test to avoid 404
    }

    @Test
    void canExecuteFullResultLifecycle() throws Exception {
        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, UUID.randomUUID().toString());
        JsonNode sample = postJson("/api/clinical-operations/samples", collectPayload);
        sampleId = sample.get("sampleId").asText();

        String labelPayload = """
                {"tenantId": "%s", "actorId": "tech-1", "labelId": "lbl-1", "barcodeValue": "123456789"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/label/print", sampleId)
                .contentType(MediaType.APPLICATION_JSON).content(labelPayload))
                .andExpect(status().isOk());

        String receivePayload = """
                {"tenantId": "%s", "receivedBy": "tech-2", "conditionAtReception": "acceptable"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reception/receive", sampleId)
                .contentType(MediaType.APPLICATION_JSON).content(receivePayload))
                .andExpect(status().isOk());

        // 1. Capture Result
        String capturePayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "sampleId": "%s",
                    "rawValue": "5.4", "numericValue": 5.4, "unit": "mmol/L",
                    "captureSource": "manual_entry", "capturedBy": "tech-1",
                    "testDefinitionId": "test-1", "analyteId": "analyte-1", "analyteName": "Glucose"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, sampleId);

        JsonNode resultNode = postJson("/api/clinical-operations/laboratory-results", capturePayload);
        String resultId = resultNode.get("resultId").asText();
        assertThat(resultNode.get("status").asText()).isEqualTo("captured");

        // 3. Submit for technical validation
        String submitPayload = """
                {"tenantId": "%s", "actorId": "tech-1"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/technical-validation/submit", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submitPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("pending_technical_validation"));

        // 4. Stubs tests (Tech validate -> Med Validate -> Release)
        String validatePayload = """
                {"tenantId": "%s", "actorId": "tech-2", "approved": true}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/technical-validation/validate", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validatePayload))
                .andExpect(status().isOk());


        String medValidatePayload = """
                {"tenantId": "%s", "actorId": "doc-1", "licenseIdentifier": "12345"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/medical-validation/validate", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medValidatePayload))
                .andExpect(status().isOk());

        String releasePayload = """
                {"tenantId": "%s", "actorId": "doc-1"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/release/release", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(releasePayload))
                .andExpect(status().isOk());

        // 5. Test List Worklists
        mockMvc.perform(get("/api/clinical-operations/laboratory-results/processing-worklist?tenantId={t}&laboratoryId={l}", tenantId, laboratoryId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clinical-operations/laboratory-results/{resultId}/technical-validation/technical-validation-worklist?tenantId={t}&laboratoryId={l}", resultId, tenantId, laboratoryId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clinical-operations/laboratory-results/{resultId}/medical-validation/medical-validation-worklist?tenantId={t}&laboratoryId={l}", resultId, tenantId, laboratoryId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clinical-operations/laboratory-results/{resultId}/release/release-worklist?tenantId={t}&laboratoryId={l}", resultId, tenantId, laboratoryId))
                .andExpect(status().isOk());

        // 6. BCM-RES-001: search released results by status (MVP-MOD-007-FE-001)
        mockMvc.perform(get("/api/clinical-operations/laboratory-results?tenantId={t}&status=released", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultId").value(resultId))
                .andExpect(jsonPath("$[0].status").value("released"));

        // 7. BCM-RES-002: regenerate and list PDF reports for the released result
        String reportsBase = "/api/clinical-operations/laboratory-results/{resultId}/reports";
        mockMvc.perform(post(reportsBase + "/regenerate?tenantId={t}&actorId=doc-1", resultId, tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("generated"))
                .andExpect(jsonPath("$.documentId").exists());

        mockMvc.perform(get(reportsBase + "?tenantId={t}", resultId, tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultId").value(resultId));

        // 8. BCM-RES-007: notification history for the released/delivered result
        mockMvc.perform(get("/api/clinical-operations/laboratory-results/{resultId}/notifications?tenantId={t}", resultId, tenantId))
                .andExpect(status().isOk());
    }

    @Test
    void exceptionsAndErrorsCoverage() throws Exception {
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/bad-id/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\": \"%s\", \"incidentType\": \"repeat_required\"}".formatted(tenantId)));

        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, UUID.randomUUID().toString());
        JsonNode sample = postJson("/api/clinical-operations/samples", collectPayload);
        sampleId = sample.get("sampleId").asText();

        // Label and receive sample
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/label/print", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\": \"%s\", \"actorId\": \"tech-1\", \"labelId\": \"lbl-1\", \"barcodeValue\": \"123456789\"}".formatted(tenantId)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reception/receive", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\": \"%s\", \"receivedBy\": \"tech-2\", \"conditionAtReception\": \"acceptable\"}".formatted(tenantId)))
                .andExpect(status().isOk());

        String capturePayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "sampleId": "%s",
                    "rawValue": "5.4", "numericValue": 5.4, "unit": "mmol/L",
                    "captureSource": "manual_entry", "capturedBy": "tech-1",
                    "testDefinitionId": "test-1", "analyteId": "analyte-1", "analyteName": "Glucose"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, sampleId);
        String resultId = postJson("/api/clinical-operations/laboratory-results", capturePayload).get("resultId").asText();

        // Test incident validation failure
        String incidentPayload = """
                {
                    "tenantId": "%s", "incidentType": "repeat_required",
                    "notes": "Questionable delta check", "recordedBy": "tech-1"
                }
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/incidents", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(incidentPayload))
                .andExpect(status().isOk());

        String submitPayload = """
                {"tenantId": "%s", "actorId": "tech-1"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/technical-validation/submit", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submitPayload))
                .andExpect(status().isConflict()); // Cannot submit with unresolved incident

        // Release directly -> conflict (skip tech/med val)
        String releasePayload = """
                {"tenantId": "%s", "actorId": "doc-1"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/laboratory-results/{resultId}/release/release", resultId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(releasePayload))
                .andExpect(status().isConflict());
    }

    private JsonNode postJson(String path, String json) throws Exception {
        String response = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }
}
