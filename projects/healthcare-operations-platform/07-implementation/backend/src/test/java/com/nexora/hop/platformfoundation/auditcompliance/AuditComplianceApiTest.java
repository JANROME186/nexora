package com.nexora.hop.platformfoundation.auditcompliance;

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
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;

@AutoConfigureMockMvc
@SpringBootTest
class AuditComplianceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criticalPlatformFoundationActionsGenerateQueryableAuditEvents() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Audited Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode laboratory = postJson("/api/organization/laboratories", """
                {"tenantId":"%s","name":"Audited Lab"}
                """.formatted(tenantId));
        String laboratoryId = laboratory.get("laboratoryId").asText();

        JsonNode branch = postJson("/api/organization/branches", """
                {"laboratoryId":"%s","name":"Audited Branch"}
                """.formatted(laboratoryId));

        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Audited User","email":"audited.user@example.test"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant_admin","scope":{"type":"tenant","id":"%s"}}
                        """.formatted(tenantId)))
                .andExpect(status().isNoContent());

        JsonNode auditEvents = getJson("/api/audit/events?tenantId=" + tenantId);
        List<String> actions = valuesAsText(auditEvents, "action");

        assertThat(actions).contains(
                "TenantCreated",
                "LaboratoryCreated",
                "BranchCreated",
                "UserCreated",
                "RoleAssigned");
        assertThat(valuesAsText(auditEvents, "subjectId")).contains(
                tenantId,
                laboratoryId,
                branch.get("branchId").asText(),
                userId);
    }

    @Test
    void auditEventsCanBeFilteredBySubject() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Filtered Audit Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode auditEvents = getJson("/api/audit/events?subjectId=" + tenantId);

        assertThat(auditEvents.size()).isOne();
        assertThat(auditEvents.get(0).get("action").asText()).isEqualTo("TenantCreated");
        assertThat(auditEvents.get(0).get("subjectType").asText()).isEqualTo("Tenant");
    }

    @Test
    void auditRepositoryContractIsAppendOnly() {
        List<String> methodNames = List.of(AuditEventRepository.class.getDeclaredMethods()).stream()
                .map(method -> method.getName().toLowerCase())
                .toList();

        assertThat(methodNames).contains("append", "search");
        assertThat(methodNames).doesNotContain("update", "delete", "save");
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
