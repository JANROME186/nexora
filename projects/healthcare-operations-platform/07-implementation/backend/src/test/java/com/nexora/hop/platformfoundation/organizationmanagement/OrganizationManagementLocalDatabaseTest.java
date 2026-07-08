package com.nexora.hop.platformfoundation.organizationmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class OrganizationManagementLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void organizationCommandsArePersistedInPostgres() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Persisted Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode laboratory = postJson("/api/organization/laboratories", """
                {"tenantId":"%s","name":"Persisted Laboratory"}
                """.formatted(tenantId));
        String laboratoryId = laboratory.get("laboratoryId").asText();

        JsonNode branch = postJson("/api/organization/branches", """
                {"laboratoryId":"%s","name":"Persisted Branch"}
                """.formatted(laboratoryId));
        String branchId = branch.get("branchId").asText();

        Integer tenants = jdbcTemplate.queryForObject(
                "select count(*) from organization.tenants where tenant_id = ?",
                Integer.class,
                tenantId);
        Integer laboratories = jdbcTemplate.queryForObject(
                "select count(*) from organization.laboratories where laboratory_id = ? and tenant_id = ?",
                Integer.class,
                laboratoryId,
                tenantId);
        Integer branches = jdbcTemplate.queryForObject(
                "select count(*) from organization.branches where branch_id = ? and laboratory_id = ? and tenant_id = ?",
                Integer.class,
                branchId,
                laboratoryId,
                tenantId);

        assertThat(tenants).isOne();
        assertThat(laboratories).isOne();
        assertThat(branches).isOne();
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
