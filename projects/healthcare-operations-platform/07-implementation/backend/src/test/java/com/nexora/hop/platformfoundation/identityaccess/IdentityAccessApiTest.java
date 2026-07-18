package com.nexora.hop.platformfoundation.identityaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class IdentityAccessApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userCanBeCreatedQueriedAndAssignedARole() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Nexora Diagnostics\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Ada Lovelace","email":"ada@nexora.example"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        assertThat(user.get("tenantId").asText()).isEqualTo(tenantId);
        assertThat(user.get("status").asText()).isEqualTo("created");

        mockMvc.perform(get("/api/identity/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@nexora.example"));

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant-admin","scope":{"type":"tenant","id":"%s"},"actorUserId":"tester-1"}
                        """.formatted(tenantId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void userCreationRejectsMissingTenant() throws Exception {
        mockMvc.perform(post("/api/identity/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\":\"missing-tenant\",\"displayName\":\"Ada Lovelace\",\"email\":\"ada@nexora.example\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void roleAssignmentRejectsMissingUser() throws Exception {
        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", "missing-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant-admin","scope":{"type":"tenant","id":"some-tenant"},"actorUserId":"tester-1"}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void roleAssignmentRejectsMissingScope() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Nexora Diagnostics\"}");
        String tenantId = tenant.get("tenantId").asText();
        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Ada Lovelace","email":"ada@nexora.example"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleCode\":\"tenant-admin\",\"actorUserId\":\"tester-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void roleAssignmentRejectsMissingActorUserId() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Nexora Diagnostics\"}");
        String tenantId = tenant.get("tenantId").asText();
        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Ada Lovelace","email":"ada@nexora.example"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant-admin","scope":{"type":"tenant","id":"%s"}}
                        """.formatted(tenantId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void roleAssignmentWithSuppliedActorSucceedsAndIsAudited() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Nexora Diagnostics\"}");
        String tenantId = tenant.get("tenantId").asText();
        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Ada Lovelace","email":"ada@nexora.example"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant-admin","scope":{"type":"tenant","id":"%s"},"actorUserId":"tester-1"}
                        """.formatted(tenantId)))
                .andExpect(status().isNoContent());

        JsonNode auditEvents = getJson("/api/audit/events?tenantId=" + tenantId);
        boolean roleAssignedEventPresent = java.util.stream.IntStream.range(0, auditEvents.size())
                .mapToObj(auditEvents::get)
                .anyMatch(event -> "RoleAssigned".equals(event.get("action").asText()));
        assertThat(roleAssignedEventPresent).isTrue();
    }

    private JsonNode getJson(String path) throws Exception {
        String body = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(body);
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
}
