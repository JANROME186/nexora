package com.nexora.hop.platformfoundation.integrationinteroperability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class IntegrationInteroperabilityLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void integrationInteroperabilitySchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'integration_interoperability'
                   and table_name in (
                       'integration_endpoints', 'integration_message_records',
                       'api_surface_registrations', 'partner_api_keys', 'rate_limit_policies')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(5);
    }

    @Test
    void endpointAndApiManagementRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC Integration Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode endpoint = postJson("/api/platform/integration/endpoints", """
                {"tenantId":"%s","laboratoryId":"lab-jdbc","endpointName":"LIS Feed","protocol":"hl7v2",
                 "direction":"inbound","actorId":"integrator-1"}
                """.formatted(tenantId));
        String endpointId = endpoint.get("endpointId").asText();

        mockMvc.perform(get("/api/platform/integration/endpoints/{id}", endpointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("registered"));

        JsonNode message = postJson("/api/platform/integration/endpoints/" + endpointId + "/messages", """
                {"externalMessageId":"EXT-JDBC-001","rawPayload":"patientId=P1","actorId":"integrator-1"}
                """);
        mockMvc.perform(get("/api/platform/integration/messages/{id}", message.get("messageId").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalizationStatus").value("acknowledged"));

        postJson("/api/platform/api-management/operations/jdbcOp/classification", """
                {"ownerCapability":"BCM-RES-001","classification":"partner","apiVersion":"v1","actorId":"admin"}
                """);
        JsonNode key = postJson("/api/platform/api-management/partner-keys", """
                {"tenantId":"%s","consumerName":"JDBC Partner","grantedScopes":["jdbcOp"],"actorId":"admin"}
                """.formatted(tenantId));

        mockMvc.perform(get("/api/platform/api-management/partner-keys").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].keyId").value(key.get("keyId").asText()));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/platform/api-management/rate-limit-policies/{classification}", "partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestsPerMinute\":30,\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestsPerMinute").value(30));

        // update the same policy again to exercise the on-conflict update path
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/platform/api-management/rate-limit-policies/{classification}", "partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestsPerMinute\":45,\"actorId\":\"admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestsPerMinute").value(45));
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
