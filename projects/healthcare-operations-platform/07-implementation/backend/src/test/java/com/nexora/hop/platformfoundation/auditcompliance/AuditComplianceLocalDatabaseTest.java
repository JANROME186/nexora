package com.nexora.hop.platformfoundation.auditcompliance;

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

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class AuditComplianceLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void criticalActionsPersistAppendOnlyAuditEventsInPostgres() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Persisted Audit Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode user = postJson("/api/identity/users", """
                {"tenantId":"%s","displayName":"Persisted Audit User","email":"persisted.audit@example.test"}
                """.formatted(tenantId));
        String userId = user.get("userId").asText();

        mockMvc.perform(post("/api/identity/users/{userId}/role-assignments", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"roleCode":"tenant_admin","scope":{"type":"tenant","id":"%s"},"actorUserId":"audit-tester-1"}
                        """.formatted(tenantId)))
                .andExpect(status().isNoContent());

        Integer auditEvents = jdbcTemplate.queryForObject(
                "select count(*) from audit.audit_events where tenant_id = ? and action in ('TenantCreated', 'UserCreated', 'RoleAssigned')",
                Integer.class,
                tenantId);
        Integer roleAuditEvents = jdbcTemplate.queryForObject(
                "select count(*) from audit.audit_events where tenant_id = ? and action = 'RoleAssigned'",
                Integer.class,
                tenantId);

        assertThat(auditEvents).isEqualTo(3);
        assertThat(roleAuditEvents).isOne();
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
