package com.nexora.hop.platformfoundation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class PlatformFoundationMvpSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void platformFoundationPrimaryFlowWorksEndToEnd() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Smoke Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode laboratory = postJson("/api/organization/laboratories", """
                {"tenantId":"%s","name":"Smoke Laboratory"}
                """.formatted(tenantId));
        String laboratoryId = laboratory.get("laboratoryId").asText();

        JsonNode branch = postJson("/api/organization/branches", """
                {"laboratoryId":"%s","name":"Smoke Branch"}
                """.formatted(laboratoryId));
        String branchId = branch.get("branchId").asText();

        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Smoke User","email":"smoke.user@example.test"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"branch-operator","scope":{"type":"branch","id":"%s"}}
                        """.formatted(branchId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/platform/tenants/{tenantId}", tenantId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/organization/laboratories/{laboratoryId}", laboratoryId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/organization/branches/{branchId}", branchId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/identity/users/{userId}", userId))
                .andExpect(status().isOk());

        JsonNode auditEvents = getJson("/api/audit/events?tenantId=" + tenantId);
        List<String> actions = valuesAsText(auditEvents, "action");

        assertThat(actions).contains(
                "TenantCreated",
                "LaboratoryCreated",
                "BranchCreated",
                "UserCreated",
                "RoleAssigned");
    }

    private JsonNode postJson(String path, String json) throws Exception {
        String body = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }

    private JsonNode getJson(String path) throws Exception {
        String body = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
    }

    private List<String> valuesAsText(JsonNode arrayNode, String fieldName) {
        return java.util.stream.IntStream.range(0, arrayNode.size())
                .mapToObj(index -> arrayNode.get(index).get(fieldName).asText())
                .toList();
    }
}
