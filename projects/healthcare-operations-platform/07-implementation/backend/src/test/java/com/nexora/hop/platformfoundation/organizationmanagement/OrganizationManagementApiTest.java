package com.nexora.hop.platformfoundation.organizationmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class OrganizationManagementApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void tenantLaboratoryAndBranchCanBeCreatedAndQueried() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", """
                {"code":"nexora-diagnostics","legalName":"Nexora Diagnostics","tier":"PROFESSIONAL"}
                """);
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
        assertThat(tenant.get("status").asText()).isEqualTo("PENDING_PROVISIONING");
        assertThat(tenant.get("tier").asText()).isEqualTo("PROFESSIONAL");
        assertThat(laboratory.get("tenantId").asText()).isEqualTo(tenantId);
        assertThat(branch.get("tenantId").asText()).isEqualTo(tenantId);
        assertThat(branch.get("laboratoryId").asText()).isEqualTo(laboratoryId);

        mockMvc.perform(get("/api/platform/tenants/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalName").value("Nexora Diagnostics"))
                .andExpect(jsonPath("$.code").value("nexora-diagnostics"));

        mockMvc.perform(get("/api/organization/laboratories/{laboratoryId}", laboratoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId));

        mockMvc.perform(get("/api/organization/branches/{branchId}", branchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.laboratoryId").value(laboratoryId));
    }

    @Test
    void listTenantsReturnsProvisionedTenants() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", """
                {"code":"list-tenants-case","legalName":"List Tenants Case"}
                """);
        String tenantId = tenant.get("tenantId").asText();

        mockMvc.perform(get("/api/platform/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.tenantId=='" + tenantId + "')]").exists());
    }

    @Test
    void updateTenantStatusTransitionsAndPersistsTheNewStatus() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", """
                {"code":"triage-case","legalName":"Triage Case"}
                """);
        String tenantId = tenant.get("tenantId").asText();

        mockMvc.perform(put("/api/platform/tenants/{tenantId}/status", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SUSPENDED\",\"reason\":\"cross-tenant impact triage\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));

        mockMvc.perform(get("/api/platform/tenants/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    void updateTenantStatusRejectsUnknownStatusValue() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", """
                {"code":"invalid-status-case","legalName":"Invalid Status Case"}
                """);
        String tenantId = tenant.get("tenantId").asText();

        mockMvc.perform(put("/api/platform/tenants/{tenantId}/status", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"NOT_A_REAL_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void provisionTenantRejectsDuplicateCode() throws Exception {
        postJson("/api/platform/tenants", "{\"code\":\"duplicate-code\",\"legalName\":\"First\"}");

        mockMvc.perform(post("/api/platform/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"duplicate-code\",\"legalName\":\"Second\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TENANT_CODE_CONFLICT"));
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
