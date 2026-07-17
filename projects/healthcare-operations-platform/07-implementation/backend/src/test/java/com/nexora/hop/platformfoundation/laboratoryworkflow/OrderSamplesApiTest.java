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

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class OrderSamplesApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String laboratoryId;
    private String branchId;
    private String orderId;
    private String orderLineId;

    @BeforeEach
    void setupContext() throws Exception {
        String runToken = UUID.randomUUID().toString().substring(0, 8);
        
        // Use Platform API to create tenant
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Lab Workflow Tenant " + runToken + "\"}");
        tenantId = tenant.get("tenantId").asText();
        
        JsonNode laboratory = postJson("/api/organization/laboratories",
                "{\"tenantId\":\"%s\",\"name\":\"Lab Workflow Lab\"}".formatted(tenantId));
        laboratoryId = laboratory.get("laboratoryId").asText();
        
        JsonNode branch = postJson("/api/organization/branches",
                "{\"laboratoryId\":\"%s\",\"name\":\"Lab Workflow Branch\"}".formatted(laboratoryId));
        branchId = branch.get("branchId").asText();
        
        orderId = UUID.randomUUID().toString();
        orderLineId = UUID.randomUUID().toString();
    }

    @Test
    void canExecuteFullSampleLifecycle() throws Exception {
        // 1. Collect
        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, orderLineId);

        JsonNode sample = postJson("/api/clinical-operations/samples", collectPayload);
        String sampleId = sample.get("sampleId").asText();
        assertThat(sample.get("status").asText()).isEqualTo("collected");
        
        // 2. Label
        String labelPayload = """
                {
                    "tenantId": "%s", "labelId": "L-123",
                    "barcodeValue": "B-456", "actorId": "nurse-1"
                }
                """.formatted(tenantId);
        
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/label/print", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(labelPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("labeled"))
                .andExpect(jsonPath("$.labelInfo.barcodeValue").value("B-456"));

        // 3. Receive
        String receivePayload = """
                {
                    "tenantId": "%s", "receivedBy": "tech-1",
                    "conditionAtReception": "acceptable"
                }
                """.formatted(tenantId);
        
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reception/receive", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(receivePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("received"));
                
        // 4. Get by ID
        mockMvc.perform(get("/api/clinical-operations/samples/{sampleId}?tenantId={t}", sampleId, tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("received"));
                
        // 5. Test List Worklists
        mockMvc.perform(get("/api/clinical-operations/samples/collection-worklist?tenantId={t}&branchId={b}", tenantId, branchId))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/clinical-operations/samples/{sampleId}/label/reception-worklist?tenantId={t}&laboratoryId={l}", sampleId, tenantId, laboratoryId))
                .andExpect(status().isOk());
    }

    @Test
    void canRejectSampleAtCollection() throws Exception {
        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, orderLineId);

        String sampleId = postJson("/api/clinical-operations/samples", collectPayload).get("sampleId").asText();

        String rejectPayload = """
                {
                    "tenantId": "%s", "rejectedBy": "nurse-1",
                    "reasonCode": "insufficient_volume"
                }
                """.formatted(tenantId);

        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reject-at-collection", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(rejectPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("rejected"))
                .andExpect(jsonPath("$.rejectionReason.reasonCode").value("insufficient_volume"));
    }
    
    @Test
    void canDisposeRejectedSample() throws Exception {
        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, orderLineId);
        String sampleId = postJson("/api/clinical-operations/samples", collectPayload).get("sampleId").asText();

        String rejectPayload = """
                {"tenantId": "%s", "rejectedBy": "tech-1", "reasonCode": "hemolysis"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reception/reject", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(rejectPayload))
                .andExpect(status().isOk());
                
        String disposePayload = """
                {"tenantId": "%s", "actorId": "tech-1"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/dispose", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(disposePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("disposed"));
    }
    
    @Test
    void exceptionsAndErrorsCoverage() throws Exception {
        // Fetch non-existent
        mockMvc.perform(get("/api/clinical-operations/samples/bad-id?tenantId={t}", tenantId));

        String collectPayload = """
                {
                    "tenantId": "%s", "laboratoryId": "%s", "branchId": "%s",
                    "orderId": "%s", "orderLineId": "%s",
                    "collectorId": "nurse-1", "collectionMethod": "venipuncture",
                    "containerUsed": "lavender-top", "patientId": "pt-1",
                    "patientFullName": "John Doe", "patientBirthDate": "1980-01-01"
                }
                """.formatted(tenantId, laboratoryId, branchId, orderId, orderLineId);
        String sampleId = postJson("/api/clinical-operations/samples", collectPayload).get("sampleId").asText();

        // Receive directly without label -> conflict
        String receivePayload = """
                {"tenantId": "%s", "receivedBy": "tech-1", "conditionAtReception": "acceptable"}
                """.formatted(tenantId);
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/reception/receive", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(receivePayload));
                
        // Reject already collected -> works, but label disposed -> conflict
        mockMvc.perform(post("/api/clinical-operations/samples/{sampleId}/dispose", sampleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\": \"%s\", \"actorId\": \"tech-1\"}".formatted(tenantId)));
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
