package com.nexora.hop.platformfoundation.marketplaceentitlements;

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
class MarketplaceEntitlementsLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void marketplaceEntitlementsSchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'marketplace_entitlements'
                   and table_name in (
                       'marketplace_packages', 'package_versions', 'commercial_offers',
                       'tenant_entitlements', 'package_installations', 'billing_event_records')
                """, Integer.class);

        assertThat(tableCount).isEqualTo(6);
    }

    @Test
    void packageOfferEntitlementAndInstallationRoundTripAgainstRealPostgres() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"JDBC Marketplace Tenant " + token + "\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode created = postJson("/api/marketplace/packages", """
                {"code":"pkg-jdbc-%s","name":"JDBC Package","category":"platform",
                 "capabilityMappings":["BCM-PLT-011"],"initialVersion":"1.0.0","actorId":"operator-1"}
                """.formatted(token));
        String packageId = created.get("packageId").asText();

        postJson("/api/marketplace/packages/" + packageId + "/versions/1.0.0/certify", """
                {"compatibilityApproved":true,"securityReviewApproved":true,
                 "supportModelApproved":true,"telemetryModelApproved":true,"actorId":"operator-1"}
                """);
        postJson("/api/marketplace/packages/" + packageId + "/publish", """
                {"version":"1.0.0","actorId":"operator-1"}
                """);

        mockMvc.perform(get("/api/marketplace/packages/{id}/versions/{version}", packageId, "1.0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifecycleStatus").value("published"));

        JsonNode entitlement = postJson("/api/marketplace/entitlements/" + tenantId, """
                {"packageId":"%s","actorId":"operator-1"}
                """.formatted(packageId));
        String entitlementId = entitlement.get("entitlementId").asText();

        mockMvc.perform(get("/api/marketplace/entitlements/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entitlementId").value(entitlementId));

        JsonNode installation = postJson("/api/marketplace/installations/" + tenantId, """
                {"packageId":"%s","version":"1.0.0","entitlementId":"%s","actorId":"tenant-admin-1"}
                """.formatted(packageId, entitlementId));

        mockMvc.perform(get("/api/marketplace/installations/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].installationId").value(installation.get("installationId").asText()));

        postJson("/api/marketplace/billing/events", """
                {"tenantId":"%s","entitlementId":"%s","eventType":"subscription_charge",
                 "amountMinorUnits":999,"currency":"MXN","actorId":"billing-service"}
                """.formatted(tenantId, entitlementId));

        Integer billingEventCount = jdbcTemplate.queryForObject(
                "select count(*) from marketplace_entitlements.billing_event_records where tenant_id = ?",
                Integer.class, tenantId);
        assertThat(billingEventCount).isEqualTo(1);
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
