package com.nexora.hop.platformfoundation.publicweb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class PublicMarketplaceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicMarketplaceEndpointsAllowAnonymousAccessAndExcludeTenantDetails() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);

        // Submit package
        JsonNode pkg = postJson("/api/marketplace/packages", """
                {
                    "code": "PKG-PUB-%s",
                    "name": "Public Analytics Extension",
                    "category": "analytics",
                    "capabilityMappings": ["BCM-CLI-005"],
                    "initialVersion": "1.0.0",
                    "actorId": "admin-1"
                }
                """.formatted(token));
        String packageId = pkg.get("packageId").asText();

        // Certify & publish package version
        postJson("/api/marketplace/packages/" + packageId + "/versions/1.0.0/certify", """
                {
                    "compatibilityApproved": true,
                    "securityReviewApproved": true,
                    "supportModelApproved": true,
                    "telemetryModelApproved": true,
                    "compatibilityMetadataText": "Certified for GA",
                    "actorId": "admin-1"
                }
                """);
        postJson("/api/marketplace/packages/" + packageId + "/publish", """
                {"version": "1.0.0", "actorId": "admin-1"}
                """);

        // Publish commercial offer
        postJson("/api/marketplace/offers", """
                {
                    "packageId": "%s",
                    "packageVersion": "1.0.0",
                    "offerCode": "OFFER-PUB-%s",
                    "offerType": "expansion_package",
                    "tierCodes": ["PRO"],
                    "trialPeriodDays": 14,
                    "billingEventRulesSummary": "Monthly subscription",
                    "actorId": "admin-1"
                }
                """.formatted(packageId, token));

        // Anonymous GET list published packages
        MvcResult packagesResult = mockMvc.perform(get("/api/public/marketplace/packages/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode packagesNode = objectMapper.readTree(packagesResult.getResponse().getContentAsString());
        assertThat(packagesNode.size()).isGreaterThanOrEqualTo(1);

        boolean foundPkg = false;
        for (JsonNode node : packagesNode) {
            if (packageId.equals(node.get("packageId").asText())) {
                foundPkg = true;
                assertThat(node.get("name").asText()).isEqualTo("Public Analytics Extension");
                assertThat(node.get("status").asText()).isEqualTo("published");
                assertThat(node.has("tenantId")).isFalse();
                assertThat(node.has("audit")).isFalse();
            }
        }
        assertThat(foundPkg).isTrue();

        // Anonymous GET published package snapshot
        mockMvc.perform(get("/api/public/marketplace/packages/{packageId}/published-snapshot", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageId").value(packageId))
                .andExpect(jsonPath("$.name").value("Public Analytics Extension"))
                .andExpect(jsonPath("$.status").value("published"))
                .andExpect(jsonPath("$.tenantId").doesNotExist())
                .andExpect(jsonPath("$.audit").doesNotExist());

        // Anonymous GET published offers
        mockMvc.perform(get("/api/public/marketplace/offers/published")
                        .param("packageId", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageId").value(packageId))
                .andExpect(jsonPath("$[0].offerCode").value("OFFER-PUB-" + token))
                .andExpect(jsonPath("$[0].lifecycleStatus").value("published"))
                .andExpect(jsonPath("$[0].tenantId").doesNotExist())
                .andExpect(jsonPath("$[0].audit").doesNotExist());
    }

    @Test
    void unpublishedPackageReturns404ForAnonymousSnapshot() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode pkg = postJson("/api/marketplace/packages", """
                {
                    "code": "PKG-DRAFT-%s",
                    "name": "Draft Package",
                    "category": "clinical",
                    "capabilityMappings": ["BCM-CLI-001"],
                    "initialVersion": "1.0.0",
                    "actorId": "admin-1"
                }
                """.formatted(token));
        String packageId = pkg.get("packageId").asText();

        mockMvc.perform(get("/api/public/marketplace/packages/{packageId}/published-snapshot", packageId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PUBLIC_CATALOG_NOT_PUBLISHED"));
    }

    private JsonNode postJson(String url, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
