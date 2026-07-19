package com.nexora.hop.platformfoundation.integrationinteroperability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

@AutoConfigureMockMvc
@SpringBootTest
class IntegrationInteroperabilityApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Integration Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void registersEndpointReceivesAndAcknowledgesAMessageIdempotently() throws Exception {
        String endpointId = registerEndpoint("hl7v2", "inbound");

        JsonNode first = postJson("/api/platform/integration/endpoints/" + endpointId + "/messages", """
                {"externalMessageId":"EXT-001","rawPayload":"patientId=P1;status=ok","actorId":"integrator-1"}
                """);
        assertThat(first.get("normalizationStatus").asText()).isEqualTo("acknowledged");
        assertThat(first.get("retryCount").asInt()).isZero();

        // idempotent replay: same external message id returns the already-processed record
        JsonNode replay = postJson("/api/platform/integration/endpoints/" + endpointId + "/messages", """
                {"externalMessageId":"EXT-001","rawPayload":"patientId=P1;status=ok","actorId":"integrator-1"}
                """);
        assertThat(replay.get("messageId").asText()).isEqualTo(first.get("messageId").asText());

        mockMvc.perform(get("/api/platform/integration/messages/{messageId}", first.get("messageId").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canonicalFields.patientId").value("P1"));
    }

    @Test
    void normalizationFailureIsCapturedAsCanonicalErrorCodeAndCanBeRetried() throws Exception {
        String endpointId = registerEndpoint("fhir", "inbound");

        JsonNode failed = postJson("/api/platform/integration/endpoints/" + endpointId + "/messages", """
                {"externalMessageId":"EXT-BAD","rawPayload":"this payload is INVALID","actorId":"integrator-1"}
                """);
        assertThat(failed.get("normalizationStatus").asText()).isEqualTo("normalization_failed");
        assertThat(failed.get("canonicalErrorCode").asText()).isEqualTo("INTEGRATION_NORMALIZATION_FAILED");

        String messageId = failed.get("messageId").asText();
        mockMvc.perform(post("/api/platform/integration/messages/{messageId}/retry", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rawPayload\":\"patientId=P2\",\"actorId\":\"integrator-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalizationStatus").value("acknowledged"))
                .andExpect(jsonPath("$.retryCount").value(1));
    }

    @Test
    void retiredEndpointRejectsNewMessagesAndListReturnsRegisteredEndpoints() throws Exception {
        String endpointId = registerEndpoint("astm", "outbound");

        mockMvc.perform(get("/api/platform/integration/endpoints").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].endpointId").value(endpointId));

        mockMvc.perform(post("/api/platform/integration/endpoints/{endpointId}/retire", endpointId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("retired"));

        mockMvc.perform(post("/api/platform/integration/endpoints/{endpointId}/messages", endpointId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"externalMessageId\":\"EXT-002\",\"rawPayload\":\"x=1\",\"actorId\":\"a\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INTEGRATION_ENDPOINT_NOT_ACTIVE"));
    }

    @Test
    void integrationEndpointsReturnStructuredErrorCodes() throws Exception {
        mockMvc.perform(get("/api/platform/integration/endpoints/{id}", "missing-endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INTEGRATION_ENDPOINT_NOT_FOUND"));

        mockMvc.perform(post("/api/platform/integration/endpoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"lab-1","endpointName":"Bad",
                                 "protocol":"unsupported","direction":"inbound","actorId":"a"}
                                """.formatted(tenantId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INTEGRATION_PROTOCOL_INVALID"));
    }

    @Test
    void classifiesApiOperationIssuesPartnerKeyAndSetsRateLimitPolicy() throws Exception {
        String operationId = "getResultReports-" + UUID.randomUUID().toString().substring(0, 8);
        JsonNode classified = postJson("/api/platform/api-management/operations/" + operationId + "/classification", """
                {"ownerCapability":"BCM-RES-002","classification":"partner","apiVersion":"v1","actorId":"admin-1"}
                """);
        assertThat(classified.get("classification").asText()).isEqualTo("partner");

        mockMvc.perform(get("/api/platform/api-management/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.operationId=='" + operationId + "')]").isNotEmpty());

        JsonNode key = postJson("/api/platform/api-management/partner-keys", """
                {"tenantId":"%s","consumerName":"Partner Lab","grantedScopes":["%s"],
                 "actorId":"admin-1"}
                """.formatted(tenantId, operationId));
        assertThat(key.get("status").asText()).isEqualTo("active");

        mockMvc.perform(post("/api/platform/api-management/partner-keys/{keyId}/revoke", key.get("keyId").asText())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("revoked"));

        mockMvc.perform(get("/api/platform/api-management/partner-keys").param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("revoked"));

        mockMvc.perform(put("/api/platform/api-management/rate-limit-policies/{classification}", "partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestsPerMinute\":60,\"actorId\":\"admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestsPerMinute").value(60));
    }

    @Test
    void partnerKeyRejectsScopeNotClassifiedPartnerAndDeprecationRequiresCompleteWindow() throws Exception {
        postJson("/api/platform/api-management/operations/internalOnlyOp/classification", """
                {"ownerCapability":"BCM-RES-001","classification":"internal","apiVersion":"v1","actorId":"admin-1"}
                """);

        mockMvc.perform(post("/api/platform/api-management/partner-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","consumerName":"Partner Lab","grantedScopes":["internalOnlyOp"],
                                 "actorId":"admin-1"}
                                """.formatted(tenantId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH"));

        postJson("/api/platform/api-management/operations/deprecatingOp/classification", """
                {"ownerCapability":"BCM-RES-001","classification":"public","apiVersion":"v1","actorId":"admin-1"}
                """);
        mockMvc.perform(post("/api/platform/api-management/operations/deprecatingOp/deprecation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("API_DEPRECATION_WINDOW_MISSING"));
    }

    @Test
    void retryingAMessageTooSoonAfterARetryFailureIsRejectedWithABoundedBackoffError() throws Exception {
        String endpointId = registerEndpoint("fhir", "inbound");
        JsonNode failed = postJson("/api/platform/integration/endpoints/" + endpointId + "/messages", """
                {"externalMessageId":"EXT-BACKOFF","rawPayload":"this payload is INVALID","actorId":"integrator-1"}
                """);
        String messageId = failed.get("messageId").asText();

        // The first retry after the initial receipt failure is immediately allowed but still fails.
        mockMvc.perform(post("/api/platform/integration/messages/{messageId}/retry", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rawPayload\":\"still INVALID\",\"actorId\":\"integrator-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalizationStatus").value("retrying"))
                .andExpect(jsonPath("$.retryCount").value(1));

        // A second retry attempted immediately after a retry failure is bounded by backoff.
        mockMvc.perform(post("/api/platform/integration/messages/{messageId}/retry", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rawPayload\":\"still INVALID\",\"actorId\":\"integrator-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INTEGRATION_RETRY_NOT_YET_DUE"))
                .andExpect(jsonPath("$.messageKey").value("integration.error.integration_retry_not_yet_due"));
    }

    @Test
    void apiOperationRetirementIsGatedByTheElapsedDeprecationWindow() throws Exception {
        String operationId = "getResultReports-" + UUID.randomUUID().toString().substring(0, 8);
        postJson("/api/platform/api-management/operations/" + operationId + "/classification", """
                {"ownerCapability":"BCM-RES-002","classification":"public","apiVersion":"v1","actorId":"admin-1"}
                """);
        mockMvc.perform(post("/api/platform/api-management/operations/{id}/deprecation", operationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deprecationWindowFrom":"2020-01-01T00:00:00","deprecationWindowTo":"2020-02-01T00:00:00",
                                 "migrationNote":"Use v2 instead.","actorId":"admin-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deprecationStatus").value("deprecation_scheduled"));

        // Elapsed window (2020, well in the past): retirement succeeds.
        mockMvc.perform(post("/api/platform/api-management/operations/{id}/retirement", operationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deprecationStatus").value("retired"));

        // Retiring an already-retired operation is rejected.
        mockMvc.perform(post("/api/platform/api-management/operations/{id}/retirement", operationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("API_DEPRECATION_WINDOW_NOT_ELAPSED"));
    }

    @Test
    void retiringAnOperationBeforeItsDeprecationWindowElapsesIsRejected() throws Exception {
        String operationId = "futureDeprecation-" + UUID.randomUUID().toString().substring(0, 8);
        postJson("/api/platform/api-management/operations/" + operationId + "/classification", """
                {"ownerCapability":"BCM-RES-002","classification":"public","apiVersion":"v1","actorId":"admin-1"}
                """);
        String farFuture = java.time.LocalDateTime.now().plusYears(1).toString();
        String farFutureEnd = java.time.LocalDateTime.now().plusYears(2).toString();
        postJson("/api/platform/api-management/operations/" + operationId + "/deprecation", """
                {"deprecationWindowFrom":"%s","deprecationWindowTo":"%s","migrationNote":"Use v2.","actorId":"admin-1"}
                """.formatted(farFuture, farFutureEnd));

        mockMvc.perform(post("/api/platform/api-management/operations/{id}/retirement", operationId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"actorId\":\"admin-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("API_DEPRECATION_WINDOW_NOT_ELAPSED"));
    }

    @Test
    void partnerApiKeyRateLimitIsEnforcedOncePolicyAndKeyAreConfigured() throws Exception {
        String operationId = "partnerOp-" + UUID.randomUUID().toString().substring(0, 8);
        postJson("/api/platform/api-management/operations/" + operationId + "/classification", """
                {"ownerCapability":"BCM-RES-002","classification":"partner","apiVersion":"v1","actorId":"admin-1"}
                """);
        mockMvc.perform(put("/api/platform/api-management/rate-limit-policies/{classification}", "partner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestsPerMinute\":1,\"actorId\":\"admin-1\"}"))
                .andExpect(status().isOk());
        JsonNode key = postJson("/api/platform/api-management/partner-keys", """
                {"tenantId":"%s","consumerName":"Partner Lab","grantedScopes":["%s"],"actorId":"admin-1"}
                """.formatted(tenantId, operationId));
        String keyId = key.get("keyId").asText();

        mockMvc.perform(get("/api/platform/api-management/operations")
                        .header("X-Partner-Api-Key", keyId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/platform/api-management/operations")
                        .header("X-Partner-Api-Key", keyId))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code").value("API_RATE_LIMIT_EXCEEDED"));
    }

    @Test
    void unknownPartnerApiKeyHeaderIsRejected() throws Exception {
        mockMvc.perform(get("/api/platform/api-management/operations")
                        .header("X-Partner-Api-Key", "not-a-real-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH"));
    }

    private String registerEndpoint(String protocol, String direction) throws Exception {
        JsonNode endpoint = postJson("/api/platform/integration/endpoints", """
                {"tenantId":"%s","laboratoryId":"lab-1","endpointName":"LIS Feed","protocol":"%s",
                 "direction":"%s","actorId":"integrator-1"}
                """.formatted(tenantId, protocol, direction));
        return endpoint.get("endpointId").asText();
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
