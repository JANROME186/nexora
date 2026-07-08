package com.nexora.hop.platformfoundation.organizationmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class OrganizationManagementApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void tenantLaboratoryAndBranchCanBeCreatedAndQueried() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Nexora Diagnostics\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode laboratory = postJson("/api/organization/laboratories", """
                {"tenantId":"%s","name":"Central Lab"}
                """.formatted(tenantId));
        String laboratoryId = laboratory.get("laboratoryId").asText();

        JsonNode branch = postJson("/api/organization/branches", """
                {"laboratoryId":"%s","name":"North Branch"}
                """.formatted(laboratoryId));
        String branchId = branch.get("branchId").asText();

        assertThat(tenantId).isNotBlank();
        assertThat(laboratory.get("tenantId").asText()).isEqualTo(tenantId);
        assertThat(branch.get("tenantId").asText()).isEqualTo(tenantId);
        assertThat(branch.get("laboratoryId").asText()).isEqualTo(laboratoryId);

        mockMvc.perform(get("/api/platform/tenants/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nexora Diagnostics"));

        mockMvc.perform(get("/api/organization/laboratories/{laboratoryId}", laboratoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId));

        mockMvc.perform(get("/api/organization/branches/{branchId}", branchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laboratoryId").value(laboratoryId));
    }

    @Test
    void laboratoryCreationRejectsMissingTenant() throws Exception {
        mockMvc.perform(post("/api/organization/laboratories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantId\":\"missing-tenant\",\"name\":\"Central Lab\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void branchCreationRejectsMissingLaboratory() throws Exception {
        mockMvc.perform(post("/api/organization/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"laboratoryId\":\"missing-laboratory\",\"name\":\"North Branch\"}"))
                .andExpect(status().isNotFound());
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
