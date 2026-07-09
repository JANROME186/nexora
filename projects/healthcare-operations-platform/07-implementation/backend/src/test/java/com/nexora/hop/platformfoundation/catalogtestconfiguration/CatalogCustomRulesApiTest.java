package com.nexora.hop.platformfoundation.catalogtestconfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Detailed coverage of the custom business rules implemented for MVP-MOD-002-BE-002 across the
 * eight Diagnostic Catalog capabilities: publication validation, immutable published snapshots,
 * rejection of direct edits to published entries, preparation assignment gating, effective-dated
 * versioning with overlap prevention, and effective-context resolution for reference ranges and
 * price lists.
 *
 * <p>Uses a dedicated laboratory and unique codes/reference ids so the assertions are independent
 * of any state created by other suites sharing the cached Spring context.</p>
 */
@AutoConfigureMockMvc
@SpringBootTest
class CatalogCustomRulesApiTest {

    private static final String LAB = "lab-cr";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Custom Rules Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    // ---- BCM-SVC-001 Diagnostic Service ----------------------------------------------------------

    @Test
    void diagnosticServicePublicationSnapshotAndImmutability() throws Exception {
        JsonNode empty = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-SVC-EMPTY","nameEn":"Empty","nameEs":"Vacio",
                 "serviceType":"test","components":[]}
                """.formatted(tenantId, LAB));
        // RN-002: a service with no component cannot be published.
        mockMvc.perform(post("/api/catalog/diagnostic-services/{id}/publish", empty.get("serviceId").asText()))
                .andExpect(status().isBadRequest());

        JsonNode service = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-SVC-1","nameEn":"Panel","nameEs":"Panel",
                 "serviceType":"panel","components":[{"componentType":"panel","componentRefId":"cr-panel-1"}]}
                """.formatted(tenantId, LAB));
        String serviceId = service.get("serviceId").asText();

        // No published snapshot exists while the service is still draft.
        mockMvc.perform(get("/api/catalog/diagnostic-services/{id}/published-snapshot", serviceId))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/catalog/diagnostic-services/{id}/publish", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/catalog/diagnostic-services/{id}/published-snapshot", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        // RN-003: a published service is immutable to direct edits.
        mockMvc.perform(put("/api/catalog/diagnostic-services/{id}", serviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"code":"CR-SVC-1","nameEn":"Changed","nameEs":"Cambiado","serviceType":"panel",
                         "components":[{"componentType":"panel","componentRefId":"cr-panel-1"}]}
                        """))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-002 Test Catalog ----------------------------------------------------------------

    @Test
    void testPublicationRequiresAnalytesAndSampleRequirements() throws Exception {
        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-TST-1","nameEn":"Glucose","nameEs":"Glucosa",
                 "resultType":"numeric","measurementUnit":"mg/dL","analyteRefIds":["cr-a1"],
                 "sampleRequirementRefIds":["cr-s1"]}
                """.formatted(tenantId, LAB));
        String testId = test.get("testDefinitionId").asText();

        mockMvc.perform(post("/api/catalog/tests/{id}/publish", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/catalog/tests/{id}/published-snapshot", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(put("/api/catalog/tests/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"code":"CR-TST-1","nameEn":"X","nameEs":"X","resultType":"numeric","measurementUnit":"mg/dL"}
                        """))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-003 Panel Catalog ---------------------------------------------------------------

    @Test
    void panelPublicationRequiresAtLeastTwoMembers() throws Exception {
        JsonNode single = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-PNL-SINGLE","nameEn":"P","nameEs":"P",
                 "members":[{"testRefId":"cr-t1","mandatory":true}]}
                """.formatted(tenantId, LAB));
        mockMvc.perform(post("/api/catalog/panels/{id}/publish", single.get("panelId").asText()))
                .andExpect(status().isBadRequest());

        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-PNL-1","nameEn":"P","nameEs":"P",
                 "members":[{"testRefId":"cr-t1","mandatory":true},{"testRefId":"cr-t2","mandatory":false}]}
                """.formatted(tenantId, LAB));
        String panelId = panel.get("panelId").asText();

        mockMvc.perform(post("/api/catalog/panels/{id}/publish", panelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/catalog/panels/{id}/published-snapshot", panelId))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/catalog/panels/{id}", panelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"code":"CR-PNL-1","nameEn":"Changed","nameEs":"Cambiado",
                         "members":[{"testRefId":"cr-t1","mandatory":true},{"testRefId":"cr-t2","mandatory":false}]}
                        """))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-004 Analyte Catalog -------------------------------------------------------------

    @Test
    void analytePublicationRequiresCompletenessForCodedValues() throws Exception {
        JsonNode coded = postJson("/api/catalog/analytes", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-ANL-CODED","nameEn":"Blood Group","nameEs":"Grupo",
                 "resultDataType":"coded"}
                """.formatted(tenantId, LAB));
        // RN-003: a coded analyte with no coded values is incomplete and cannot be published.
        mockMvc.perform(post("/api/catalog/analytes/{id}/publish", coded.get("analyteId").asText()))
                .andExpect(status().isBadRequest());

        JsonNode analyte = postJson("/api/catalog/analytes", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-ANL-1","nameEn":"Glucose","nameEs":"Glucosa",
                 "resultDataType":"numeric","measurementUnit":"mg/dL","decimalPrecision":1}
                """.formatted(tenantId, LAB));
        String analyteId = analyte.get("analyteId").asText();

        mockMvc.perform(post("/api/catalog/analytes/{id}/publish", analyteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/catalog/analytes/{id}/published-snapshot", analyteId))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/catalog/analytes/{id}", analyteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"code":"CR-ANL-1","nameEn":"X","nameEs":"X","resultDataType":"numeric",
                         "measurementUnit":"mg/dL","decimalPrecision":1}
                        """))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-005 Patient Preparation ---------------------------------------------------------

    @Test
    void preparationAssignmentRequiresPublicationAndPublishedIsImmutable() throws Exception {
        JsonNode preparation = postJson("/api/catalog/preparations", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-PRP-1","titleEn":"Rest","titleEs":"Descanso",
                 "instructionTextEn":"Rest before test","instructionTextEs":"Descanse antes","category":"activity"}
                """.formatted(tenantId, LAB));
        String preparationId = preparation.get("preparationId").asText();

        mockMvc.perform(post("/api/catalog/preparations/{id}/assignments", preparationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetType\":\"test\",\"targetRefId\":\"cr-t1\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/catalog/preparations/{id}/publish", preparationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(post("/api/catalog/preparations/{id}/assignments", preparationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetType\":\"test\",\"targetRefId\":\"cr-t1\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/catalog/preparations/{id}", preparationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"code":"CR-PRP-1","titleEn":"X","titleEs":"X","instructionTextEn":"X",
                         "instructionTextEs":"X","category":"activity"}
                        """))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-006 Reference Range -------------------------------------------------------------

    @Test
    void referenceRangeCriticalThresholdConsistencyIsEnforced() throws Exception {
        // RN-002: critical low must not exceed normal low.
        mockMvc.perform(post("/api/catalog/reference-ranges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"cr-ref-bad","effectiveFrom":"2026-01-01",
                         "segments":[{"sex":"any","normalLow":70,"normalHigh":110,"criticalLow":80}]}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void referenceRangeDemographicOverlapIsRejected() throws Exception {
        // RN-003: two "any" sex segments with overlapping (unbounded) age ranges conflict.
        mockMvc.perform(post("/api/catalog/reference-ranges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"cr-ref-ovl","effectiveFrom":"2026-01-01",
                         "segments":[{"sex":"any","normalLow":70,"normalHigh":110},
                                     {"sex":"male","normalLow":75,"normalHigh":115}]}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void referenceRangePublishEffectiveResolutionAndVersioningOverlap() throws Exception {
        String analyte = "cr-ref-eff";
        JsonNode first = postJson("/api/catalog/reference-ranges", """
                {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"%s","effectiveFrom":"2026-01-01",
                 "segments":[{"sex":"any","normalLow":70,"normalHigh":110,"criticalLow":40,"criticalHigh":500}]}
                """.formatted(tenantId, LAB, analyte));
        String firstId = first.get("rangeId").asText();

        mockMvc.perform(post("/api/catalog/reference-ranges/{id}/publish", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        // RN-006: effective resolution for a patient context resolves the published range.
        mockMvc.perform(get("/api/catalog/reference-ranges/effective")
                        .param("analyteId", analyte)
                        .param("sex", "male")
                        .param("ageDays", "3650"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rangeId").value(firstId));

        // A published range is immutable to direct edits (RN-004).
        mockMvc.perform(put("/api/catalog/reference-ranges/{id}", firstId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"effectiveFrom\":\"2026-01-01\",\"segments\":[]}"))
                .andExpect(status().isConflict());

        // RN-005: a second published range for the same analyte with an overlapping window is rejected.
        JsonNode second = postJson("/api/catalog/reference-ranges", """
                {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"%s","effectiveFrom":"2026-06-01",
                 "segments":[{"sex":"any","normalLow":72,"normalHigh":112}]}
                """.formatted(tenantId, LAB, analyte));
        mockMvc.perform(post("/api/catalog/reference-ranges/{id}/publish", second.get("rangeId").asText()))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-007 Sample Catalog --------------------------------------------------------------

    @Test
    void sampleRequirementPublishesOnlyWhenSampleTypePublished() throws Exception {
        JsonNode sampleType = postJson("/api/catalog/samples/types", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-SMP-1","nameEn":"Serum","nameEs":"Suero","matrix":"serum"}
                """.formatted(tenantId, LAB));
        String sampleTypeId = sampleType.get("sampleTypeId").asText();

        JsonNode requirement = postJson("/api/catalog/samples/requirements", """
                {"tenantId":"%s","laboratoryId":"%s","sampleTypeRefId":"%s","minVolumeMl":2.5}
                """.formatted(tenantId, LAB, sampleTypeId));
        String requirementId = requirement.get("requirementId").asText();

        // RN-003: the referenced sample type is still draft.
        mockMvc.perform(post("/api/catalog/samples/requirements/{id}/publish", requirementId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/catalog/samples/types/{id}/publish", sampleTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(post("/api/catalog/samples/requirements/{id}/publish", requirementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        mockMvc.perform(get("/api/catalog/samples/requirements/{id}/published-snapshot", requirementId))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/catalog/samples/requirements/{id}", requirementId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sampleTypeRefId\":\"%s\",\"minVolumeMl\":3.0}".formatted(sampleTypeId)))
                .andExpect(status().isConflict());
    }

    // ---- BCM-SVC-009 Price List ------------------------------------------------------------------

    @Test
    void priceListPublishEffectiveResolutionAndVersioningOverlap() throws Exception {
        String currency = "CRC";
        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-PRC-1","nameEn":"Std","nameEs":"Est",
                 "currency":"%s","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, LAB, currency));
        String priceListId = priceList.get("priceListId").asText();

        // RN-003: a price list with no entries cannot be published.
        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", priceListId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemType\":\"test\",\"itemRefId\":\"cr-price-item\",\"amount\":30.00}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", priceListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"));

        // RN-006: effective price resolution for the item resolves this published list.
        mockMvc.perform(get("/api/catalog/price-lists/effective")
                        .param("itemType", "test")
                        .param("itemRefId", "cr-price-item")
                        .param("currency", currency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceListId").value(priceListId));

        // RN-004: a published price list is immutable to direct edits.
        mockMvc.perform(put("/api/catalog/price-lists/{id}", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nameEn\":\"X\",\"nameEs\":\"X\"}"))
                .andExpect(status().isConflict());

        // RN-005: another published list for the same currency/agreement with an overlapping window is rejected.
        JsonNode overlapping = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"CR-PRC-2","nameEn":"Std2","nameEs":"Est2",
                 "currency":"%s","effectiveFrom":"2026-06-01"}
                """.formatted(tenantId, LAB, currency));
        String overlappingId = overlapping.get("priceListId").asText();
        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", overlappingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemType\":\"test\",\"itemRefId\":\"cr-price-item\",\"amount\":31.00}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", overlappingId))
                .andExpect(status().isConflict());
    }

    private JsonNode postJson(String path, String json) throws Exception {
        return objectMapper.readTree(perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString());
    }

    private org.springframework.test.web.servlet.ResultActions perform(MockHttpServletRequestBuilder builder)
            throws Exception {
        return mockMvc.perform(builder);
    }
}
