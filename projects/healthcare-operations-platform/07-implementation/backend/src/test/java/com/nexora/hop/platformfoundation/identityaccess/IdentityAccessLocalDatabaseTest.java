package com.nexora.hop.platformfoundation.identityaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class IdentityAccessLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void userAndRoleAssignmentArePersistedInPostgres() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Persisted Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Persisted User","email":"persisted.user@nexora.example"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant-admin","scope":{"type":"tenant","id":"%s"},"actorUserId":"tester-1"}
                        """.formatted(tenantId)))
                .andExpect(status().isNoContent());

        Integer users = jdbcTemplate.queryForObject(
                "select count(*) from identity.user_accounts where user_id = ? and tenant_id = ?",
                Integer.class,
                userId,
                tenantId);
        Integer roleAssignments = jdbcTemplate.queryForObject(
                "select count(*) from identity.role_assignments where user_id = ? and role_code = ?",
                Integer.class,
                userId,
                "tenant-admin");
        String createdBy = jdbcTemplate.queryForObject(
                "select created_by from identity.role_assignments where user_id = ? and role_code = ?",
                String.class,
                userId,
                "tenant-admin");

        assertThat(users).isOne();
        assertThat(roleAssignments).isOne();
        assertThat(createdBy).isEqualTo("tester-1");
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
