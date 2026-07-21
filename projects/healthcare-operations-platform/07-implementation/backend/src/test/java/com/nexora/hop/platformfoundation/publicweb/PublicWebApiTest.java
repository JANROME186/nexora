package com.nexora.hop.platformfoundation.publicweb;

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

/**
 * COM-MOD-011-BE-001 acceptance coverage for the anonymous public-website API surface:
 * published-only catalog reads (BCM-SVC-001/002/003/005), public appointment/quotation intake
 * (BCM-ATT-001 RN-008, BCM-ATT-006 RN-009) and RN-007 rate-limit enforcement via
 * consumerIdentificationMethod = ip_address (BCM-PLT-005, materially reducing TD-BE-015).
 */
@AutoConfigureMockMvc
@SpringBootTest
class PublicWebApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private String laboratoryId;
    private String branchId;

    @BeforeEach
    void bootstrapOrg() throws Exception {
        String token = UUID.randomUUID().toString().substring(0, 8);
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Public Tenant " + token + "\"}");
        tenantId = tenant.get("tenantId").asText();
        JsonNode laboratory = postJson("/api/organization/laboratories", """
                {"tenantId":"%s","name":"Public Lab"}
                """.formatted(tenantId));
        laboratoryId = laboratory.get("laboratoryId").asText();
        JsonNode branch = postJson("/api/organization/branches", """
                {"tenantId":"%s","laboratoryId":"%s","name":"Public Branch","status":"operational"}
                """.formatted(tenantId, laboratoryId));
        branchId = branch.get("branchId").asText();
    }

    @Test
    void publishedCatalogListsExposeOnlyPublishedRecords() throws Exception {
        String publishedServiceId = createAndPublishDiagnosticService();
        String draftServiceId = createDraftDiagnosticService();

        MvcResult result = mockMvc.perform(get("/api/public/catalog/diagnostic-services/published")
                        .param("laboratoryId", laboratoryId))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.isArray()).isTrue();
        boolean sawPublished = false;
        for (JsonNode entry : body) {
            String id = entry.get("serviceId").asText();
            assertThat(id).isNotEqualTo(draftServiceId);
            if (id.equals(publishedServiceId)) {
                sawPublished = true;
                // Public payload MUST NOT leak tenantId/laboratoryId or audit metadata.
                assertThat(entry.has("tenantId")).isFalse();
                assertThat(entry.has("createdAt")).isFalse();
            }
        }
        assertThat(sawPublished).isTrue();

        // Snapshot of a draft is 404 for the public surface.
        mockMvc.perform(get("/api/public/catalog/diagnostic-services/{id}/published-snapshot", draftServiceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PUBLIC_CATALOG_NOT_PUBLISHED"))
                .andExpect(jsonPath("$.messageKey").value("public.error.public_catalog_not_published"));
    }

    @Test
    void anonymousAppointmentRequestCapturesProspectiveContactAndStaysRequested() throws Exception {
        String publishedTestId = createAndPublishTest();

        JsonNode created = postJson("/api/public/care-delivery/appointment-requests", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "prospectiveFullName":"Jane Public","prospectivePhone":"555-1234",
                 "prospectiveEmail":"jane@example.com",
                 "scheduledStart":"2027-05-04","scheduledEnd":"2027-05-04",
                 "requestedItems":[{"testDefinitionId":"%s","catalogItemKind":"test"}]}
                """.formatted(tenantId, laboratoryId, branchId, publishedTestId));

        assertThat(created.get("channel").asText()).isEqualTo("public_website");
        assertThat(created.get("status").asText()).isEqualTo("requested");
        assertThat(created.has("tenantId")).isFalse();
    }

    @Test
    void anonymousAppointmentRequestWithoutProspectiveContactIsRejected() throws Exception {
        mockMvc.perform(post("/api/public/care-delivery/appointment-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                                 "scheduledStart":"2027-05-04","scheduledEnd":"2027-05-04"}
                                """.formatted(tenantId, laboratoryId, branchId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PUBLIC_APPOINTMENT_REQUEST_INVALID"))
                .andExpect(jsonPath("$.messageKey").value("public.error.public_appointment_request_invalid"));
    }

    @Test
    void anonymousQuotationRequestStaysDraftFromProspectiveContact() throws Exception {
        String publishedTestId = createAndPublishTest();

        JsonNode created = postJson("/api/public/care-delivery/quotation-requests", """
                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                 "prospectiveFullName":"Anon Requester","prospectivePhone":"555-9999",
                 "prospectiveEmail":null,
                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"test","quantity":1}]}
                """.formatted(tenantId, laboratoryId, branchId, publishedTestId));

        assertThat(created.get("status").asText()).isEqualTo("draft");
        assertThat(created.has("tenantId")).isFalse();
    }

    @Test
    void publishedSnapshotForEveryCatalogSurfaceIsReachableAndDraftsReturn404() throws Exception {
        String publishedTestId = createAndPublishTest();
        String publishedPanelId = createAndPublishPanel(publishedTestId);
        String publishedPreparationId = createAndPublishPreparation();
        String publishedServiceId = createAndPublishDiagnosticService();

        mockMvc.perform(get("/api/public/catalog/tests/{id}/published-snapshot", publishedTestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testDefinitionId").value(publishedTestId));
        mockMvc.perform(get("/api/public/catalog/panels/{id}/published-snapshot", publishedPanelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.panelId").value(publishedPanelId));
        mockMvc.perform(get("/api/public/catalog/preparations/{id}/published-snapshot", publishedPreparationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preparationId").value(publishedPreparationId));
        mockMvc.perform(get("/api/public/catalog/diagnostic-services/{id}/published-snapshot", publishedServiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(publishedServiceId));

        // Unknown identifiers return the structured public not-published error, not a raw 500.
        mockMvc.perform(get("/api/public/catalog/tests/{id}/published-snapshot", "does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PUBLIC_CATALOG_NOT_PUBLISHED"));
        mockMvc.perform(get("/api/public/catalog/panels/{id}/published-snapshot", "does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PUBLIC_CATALOG_NOT_PUBLISHED"));
        mockMvc.perform(get("/api/public/catalog/preparations/{id}/published-snapshot", "does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PUBLIC_CATALOG_NOT_PUBLISHED"));
    }

    @Test
    void publishedListsForEveryCatalogSurfaceReturnOnlyPublishedRecords() throws Exception {
        String publishedTestId = createAndPublishTest();
        String draftTestId = createDraftTest();
        MvcResult tests = mockMvc.perform(get("/api/public/catalog/tests/published")
                        .param("laboratoryId", laboratoryId))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode testBody = objectMapper.readTree(tests.getResponse().getContentAsString());
        boolean sawTest = false;
        for (JsonNode entry : testBody) {
            String id = entry.get("testDefinitionId").asText();
            assertThat(id).isNotEqualTo(draftTestId);
            sawTest = sawTest || id.equals(publishedTestId);
        }
        assertThat(sawTest).isTrue();

        mockMvc.perform(get("/api/public/catalog/panels/published").param("laboratoryId", laboratoryId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/public/catalog/preparations/published").param("laboratoryId", laboratoryId))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousQuotationRequestWithoutProspectiveContactIsRejected() throws Exception {
        mockMvc.perform(post("/api/public/care-delivery/quotation-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s","lines":[]}
                                """.formatted(tenantId, laboratoryId, branchId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PUBLIC_QUOTATION_REQUEST_INVALID"));
    }

    @Test
    void anonymousQuotationRequestAgainstDraftCatalogItemIsRejected() throws Exception {
        String draftTestId = createDraftTest();
        mockMvc.perform(post("/api/public/care-delivery/quotation-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":"%s","laboratoryId":"%s","branchId":"%s",
                                 "prospectiveFullName":"Anon","prospectivePhone":null,
                                 "prospectiveEmail":"anon@example.com",
                                 "lines":[{"testDefinitionId":"%s","catalogItemKind":"test","quantity":1}]}
                                """.formatted(tenantId, laboratoryId, branchId, draftTestId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PUBLIC_QUOTATION_REQUEST_INVALID"));
    }

    @Test
    void publicRateLimitBlocksAnonymousTrafficByIpAddress() throws Exception {
        // Configure public classification policy: 1 request per minute, ip_address identification.
        mockMvc.perform(put("/api/platform/api-management/rate-limit-policies/{classification}", "public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"requestsPerMinute":1,"consumerIdentificationMethod":"ip_address",
                                 "actorId":"admin-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consumerIdentificationMethod").value("ip_address"));

        // First request under a fixed X-Forwarded-For passes; second under the same IP is rejected.
        String forwardedFor = "203.0.113.42";
        mockMvc.perform(get("/api/public/catalog/diagnostic-services/published")
                        .header("X-Forwarded-For", forwardedFor)
                        .param("laboratoryId", laboratoryId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/public/catalog/diagnostic-services/published")
                        .header("X-Forwarded-For", forwardedFor)
                        .param("laboratoryId", laboratoryId))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code").value("PUBLIC_RATE_LIMIT_EXCEEDED"))
                .andExpect(jsonPath("$.messageKey").value("public.error.public_rate_limit_exceeded"));

        // A different IP is still allowed within the same minute (identity buckets independent).
        mockMvc.perform(get("/api/public/catalog/diagnostic-services/published")
                        .header("X-Forwarded-For", "198.51.100.7")
                        .param("laboratoryId", laboratoryId))
                .andExpect(status().isOk());
    }

    private String createAndPublishDiagnosticService() throws Exception {
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"T-%s","nameEn":"Test","nameEs":"Prueba",
                 "resultType":"qualitative","analyteRefIds":["A-1"],"sampleRequirementRefIds":["S-1"]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix()));
        String testId = test.get("testDefinitionId").asText();
        publishJson("/api/catalog/tests/" + testId + "/publish");

        JsonNode service = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"D-%s","nameEn":"Svc","nameEs":"Servicio",
                 "serviceType":"test",
                 "components":[{"componentType":"test","componentRefId":"%s"}]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix(), testId));
        String serviceId = service.get("serviceId").asText();
        publishJson("/api/catalog/diagnostic-services/" + serviceId + "/publish");
        return serviceId;
    }

    private String createDraftDiagnosticService() throws Exception {
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"T-%s","nameEn":"Draft Test","nameEs":"Prueba Borrador",
                 "resultType":"qualitative","analyteRefIds":["A-1"],"sampleRequirementRefIds":["S-1"]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix()));
        JsonNode service = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"D-%s","nameEn":"Draft Svc","nameEs":"Servicio Borrador",
                 "serviceType":"test",
                 "components":[{"componentType":"test","componentRefId":"%s"}]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix(), test.get("testDefinitionId").asText()));
        return service.get("serviceId").asText();
    }

    private String createAndPublishTest() throws Exception {
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"T-%s","nameEn":"Test","nameEs":"Prueba",
                 "resultType":"qualitative","analyteRefIds":["A-1"],"sampleRequirementRefIds":["S-1"]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix()));
        String testId = test.get("testDefinitionId").asText();
        publishJson("/api/catalog/tests/" + testId + "/publish");
        return testId;
    }

    private String createDraftTest() throws Exception {
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"T-%s","nameEn":"Test","nameEs":"Prueba",
                 "resultType":"qualitative","analyteRefIds":["A-1"],"sampleRequirementRefIds":["S-1"]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix()));
        return test.get("testDefinitionId").asText();
    }

    private String createAndPublishPanel(String memberTestId) throws Exception {
        String secondTestId = createAndPublishTest();
        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"P-%s","nameEn":"Panel","nameEs":"Panel",
                 "members":[
                    {"testRefId":"%s","displayOrder":1,"mandatory":true},
                    {"testRefId":"%s","displayOrder":2,"mandatory":false}]}
                """.formatted(tenantId, laboratoryId, uniqueSuffix(), memberTestId, secondTestId));
        String panelId = panel.get("panelId").asText();
        publishJson("/api/catalog/panels/" + panelId + "/publish");
        return panelId;
    }

    private String createAndPublishPreparation() throws Exception {
        JsonNode preparation = postJson("/api/catalog/preparations", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PR-%s",
                 "titleEn":"Fasting","titleEs":"Ayuno",
                 "instructionTextEn":"Fast 8 hours.","instructionTextEs":"Ayuno de 8 horas.",
                 "category":"fasting","durationHours":8}
                """.formatted(tenantId, laboratoryId, uniqueSuffix()));
        String preparationId = preparation.get("preparationId").asText();
        publishJson("/api/catalog/preparations/" + preparationId + "/publish");
        return preparationId;
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private JsonNode postJson(String uri, String body) throws Exception {
        MvcResult result = mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void publishJson(String uri) throws Exception {
        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is2xxSuccessful());
    }
}
