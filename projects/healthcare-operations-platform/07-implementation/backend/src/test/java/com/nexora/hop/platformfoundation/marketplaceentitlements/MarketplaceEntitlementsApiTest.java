package com.nexora.hop.platformfoundation.marketplaceentitlements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * End-to-end coverage of BCM-PLT-011's generated marketplace surface: package
 * submit/certify/publish, offer publish/accept, entitlement grant/revoke, installation
 * install/activate/suspend/resume/upgrade/rollback/uninstall, compatibility evaluation and the
 * billing-adapter boundary.
 */
@AutoConfigureMockMvc
@SpringBootTest
class MarketplaceEntitlementsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Marketplace Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void fullPackageLifecycleFromSubmissionToPublication() throws Exception {
        String code = "pkg-" + UUID.randomUUID().toString().substring(0, 8);
        JsonNode created = postJson("/api/marketplace/packages", """
                {"code":"%s","name":"Advanced Reporting","category":"platform",
                 "capabilityMappings":["BCM-PLT-011"],"initialVersion":"1.0.0","actorId":"operator-1"}
                """.formatted(code));
        String packageId = created.get("packageId").asText();
        assertThat(created.get("status").asText()).isEqualTo("submitted");

        mockMvc.perform(post("/api/marketplace/packages/{id}/publish", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":\"1.0.0\",\"actorId\":\"operator-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PACKAGE_VERSION_CONFLICT"));

        mockMvc.perform(post("/api/marketplace/packages/{id}/versions/{version}/certify", packageId, "1.0.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"compatibilityApproved":true,"securityReviewApproved":true,
                                 "supportModelApproved":true,"telemetryModelApproved":true,"actorId":"operator-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifecycleStatus").value("certified"));

        mockMvc.perform(post("/api/marketplace/packages/{id}/publish", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":\"1.0.0\",\"actorId\":\"operator-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/marketplace/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.packageId=='" + packageId + "')]").isNotEmpty());
    }

    @Test
    void offerAcceptanceGrantsATenantEntitlementAndInstallationLifecycleCompletes() throws Exception {
        String packageId = publishedPackage();

        JsonNode offer = postJson("/api/marketplace/offers", """
                {"packageId":"%s","packageVersion":"1.0.0","offerCode":"base-plan","offerType":"base_plan",
                 "tierCodes":["standard"],"trialPeriodDays":14,"billingEventRulesSummary":"monthly",
                 "actorId":"operator-1"}
                """.formatted(packageId));
        String offerId = offer.get("offerId").asText();

        JsonNode acceptance = postJson("/api/marketplace/offers/" + offerId + "/accept", """
                {"tenantId":"%s","actorId":"tenant-admin-1"}
                """.formatted(tenantId));
        String entitlementId = acceptance.get("entitlementId").asText();
        assertThat(entitlementId).isNotBlank();

        mockMvc.perform(get("/api/marketplace/entitlements/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("active"));

        JsonNode installation = postJson("/api/marketplace/installations/" + tenantId, """
                {"packageId":"%s","version":"1.0.0","entitlementId":"%s","actorId":"tenant-admin-1"}
                """.formatted(packageId, entitlementId));
        String installationId = installation.get("installationId").asText();
        assertThat(installation.get("lifecycleStatus").asText()).isEqualTo("installed");

        mockMvc.perform(post("/api/marketplace/installations/{tenantId}/{id}/activate", tenantId, installationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifecycleStatus").value("active"))
                .andExpect(jsonPath("$.rollbackCheckpointVersion").value("1.0.0"));

        mockMvc.perform(post("/api/marketplace/installations/{tenantId}/{id}/upgrade", tenantId, installationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetVersion\":\"1.1.0\",\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.1.0"));

        mockMvc.perform(post("/api/marketplace/installations/{tenantId}/{id}/upgrade/rollback", tenantId, installationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.0"));

        mockMvc.perform(post("/api/marketplace/installations/{tenantId}/{id}/suspend", tenantId, installationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifecycleStatus").value("suspended"));

        mockMvc.perform(post("/api/marketplace/installations/{tenantId}/{id}/uninstall", tenantId, installationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifecycleStatus").value("uninstalled"));

        mockMvc.perform(post("/api/marketplace/entitlements/{tenantId}/{id}/revoke", tenantId, entitlementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"tenant cancelled\",\"actorId\":\"tenant-admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("revoked"));
    }

    @Test
    void installationWithoutAnEntitlementIsRejected() throws Exception {
        String packageId = publishedPackage();
        mockMvc.perform(post("/api/marketplace/installations/" + tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"packageId":"%s","version":"1.0.0","actorId":"tenant-admin-1"}
                                """.formatted(packageId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ENTITLEMENT_REQUIRED"))
                .andExpect(jsonPath("$.messageKey").value("marketplace.error.entitlement_required"));
    }

    @Test
    void compatibilityEvaluateReturnsIncompatibleForADifferentMajorVersion() throws Exception {
        mockMvc.perform(post("/api/marketplace/compatibility/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"packageId\":\"pkg-x\",\"version\":\"9.0.0\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("incompatible"))
                .andExpect(jsonPath("$.effect").value("block_installation"));
    }

    @Test
    void billingEventIsAcceptedAndProviderOutageIsSimulatedDeterministically() throws Exception {
        JsonNode accepted = postJson("/api/marketplace/billing/events", """
                {"tenantId":"%s","entitlementId":"ent-x","eventType":"subscription_charge",
                 "amountMinorUnits":1999,"currency":"USD","actorId":"billing-service"}
                """.formatted(tenantId));
        assertThat(accepted.get("adapterStatus").asText()).isEqualTo("accepted");

        mockMvc.perform(post("/api/marketplace/billing/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","eventType":"SIMULATE_PROVIDER_DOWN","amountMinorUnits":100,
                                 "currency":"USD","actorId":"billing-service"}
                                """.formatted(tenantId)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PROVIDER_ADAPTER_UNAVAILABLE"));
    }

    @Test
    void packageSubmissionWithoutCapabilityMappingIsRejected() throws Exception {
        mockMvc.perform(post("/api/marketplace/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"bad-pkg","name":"Bad","category":"platform",
                                 "capabilityMappings":[],"initialVersion":"1.0.0","actorId":"operator-1"}
                                """))
                .andExpect(status().isBadRequest());
    }

    private String publishedPackage() throws Exception {
        String code = "pkg-" + UUID.randomUUID().toString().substring(0, 8);
        JsonNode created = postJson("/api/marketplace/packages", """
                {"code":"%s","name":"Advanced Reporting","category":"platform",
                 "capabilityMappings":["BCM-PLT-011"],"initialVersion":"1.0.0","actorId":"operator-1"}
                """.formatted(code));
        String packageId = created.get("packageId").asText();
        postJson("/api/marketplace/packages/" + packageId + "/versions/1.0.0/certify", """
                {"compatibilityApproved":true,"securityReviewApproved":true,
                 "supportModelApproved":true,"telemetryModelApproved":true,"actorId":"operator-1"}
                """);
        postJson("/api/marketplace/packages/" + packageId + "/publish", """
                {"version":"1.0.0","actorId":"operator-1"}
                """);
        return packageId;
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
