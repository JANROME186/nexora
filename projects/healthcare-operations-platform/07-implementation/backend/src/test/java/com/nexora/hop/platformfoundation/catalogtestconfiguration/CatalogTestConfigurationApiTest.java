package com.nexora.hop.platformfoundation.catalogtestconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Functional coverage for the catalog-test-configuration bounded context compiled for
 * MVP-MOD-002-BE-001. Exercises the generatable create/list/get/deprecate flows for each of the
 * eight Diagnostic Catalog capabilities and confirms that every custom rule declared as a
 * generation-plan.yaml custom_implementation_point responds as an explicit 501 hook reserved for
 * MVP-MOD-002-BE-002.
 */
@AutoConfigureMockMvc
@SpringBootTest
class CatalogTestConfigurationApiTest {

    private static final String LAB = "lab-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;

    @BeforeEach
    void createTenant() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Catalog Test Tenant\"}");
        tenantId = tenant.get("tenantId").asText();
    }

    @Test
    void diagnosticServiceCanBeCreatedListedAndDeprecatedAndPublishIsAHook() throws Exception {
        JsonNode service = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"SVC-1","nameEn":"Basic Panel","nameEs":"Panel Basico",
                 "serviceType":"panel","components":[{"componentType":"panel","componentRefId":"panel-1"}]}
                """.formatted(tenantId, LAB));
        String serviceId = service.get("serviceId").asText();
        assertThat(service.get("status").asText()).isEqualTo("draft");

        mockMvc.perform(get("/api/catalog/diagnostic-services").param("laboratoryId", LAB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceId").exists());

        mockMvc.perform(post("/api/catalog/diagnostic-services/{id}/publish", serviceId))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.backlogItem").value("MVP-MOD-002-BE-002"));

        mockMvc.perform(post("/api/catalog/diagnostic-services/{id}/deprecate", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("deprecated"));
    }

    @Test
    void testDefinitionRequiresMeasurementUnitForNumericResultType() throws Exception {
        mockMvc.perform(post("/api/catalog/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","code":"TST-1","nameEn":"Glucose","nameEs":"Glucosa",
                         "resultType":"numeric"}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());

        JsonNode test = postJson("/api/catalog/tests", """
                {"tenantId":"%s","laboratoryId":"%s","code":"TST-1","nameEn":"Glucose","nameEs":"Glucosa",
                 "resultType":"numeric","measurementUnit":"mg/dL"}
                """.formatted(tenantId, LAB));
        String testId = test.get("testDefinitionId").asText();

        mockMvc.perform(post("/api/catalog/tests/{id}/publish", testId))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void panelRequiresMinimumMembersHookOnPublish() throws Exception {
        JsonNode panel = postJson("/api/catalog/panels", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PNL-1","nameEn":"Chem Panel","nameEs":"Panel Quimico",
                 "members":[{"testRefId":"test-1","mandatory":true},{"testRefId":"test-2","mandatory":false}]}
                """.formatted(tenantId, LAB));
        String panelId = panel.get("panelId").asText();

        mockMvc.perform(post("/api/catalog/panels/{id}/publish", panelId))
                .andExpect(status().isNotImplemented());

        mockMvc.perform(post("/api/catalog/panels/{id}/deprecate", panelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("deprecated"));
    }

    @Test
    void analyteRequiresUnitAndPrecisionForNumericType() throws Exception {
        mockMvc.perform(post("/api/catalog/analytes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","code":"ANL-1","nameEn":"Glucose","nameEs":"Glucosa",
                         "resultDataType":"numeric"}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());

        JsonNode analyte = postJson("/api/catalog/analytes", """
                {"tenantId":"%s","laboratoryId":"%s","code":"ANL-1","nameEn":"Glucose","nameEs":"Glucosa",
                 "resultDataType":"numeric","measurementUnit":"mg/dL","decimalPrecision":1,
                 "minValue":70,"maxValue":110}
                """.formatted(tenantId, LAB));
        String analyteId = analyte.get("analyteId").asText();

        mockMvc.perform(post("/api/catalog/analytes/{id}/publish", analyteId))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void preparationRequiresDurationForFastingCategoryAndAssignIsAHook() throws Exception {
        mockMvc.perform(post("/api/catalog/preparations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","code":"PRP-1","titleEn":"Fasting","titleEs":"Ayuno",
                         "instructionTextEn":"Fast for hours","instructionTextEs":"Ayunar por horas",
                         "category":"fasting"}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());

        JsonNode preparation = postJson("/api/catalog/preparations", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PRP-1","titleEn":"Fasting","titleEs":"Ayuno",
                 "instructionTextEn":"Fast for hours","instructionTextEs":"Ayunar por horas",
                 "category":"fasting","durationHours":8}
                """.formatted(tenantId, LAB));
        String preparationId = preparation.get("preparationId").asText();

        mockMvc.perform(post("/api/catalog/preparations/{id}/assignments", preparationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetType\":\"test\",\"targetRefId\":\"test-1\"}"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void referenceRangeValidatesSegmentBoundsAndUpdateIsAHook() throws Exception {
        mockMvc.perform(post("/api/catalog/reference-ranges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"analyte-1","effectiveFrom":"2026-01-01",
                         "segments":[{"sex":"any","normalLow":110,"normalHigh":70}]}
                        """.formatted(tenantId, LAB)))
                .andExpect(status().isBadRequest());

        JsonNode range = postJson("/api/catalog/reference-ranges", """
                {"tenantId":"%s","laboratoryId":"%s","analyteRefId":"analyte-1","effectiveFrom":"2026-01-01",
                 "segments":[{"sex":"any","normalLow":70,"normalHigh":110}]}
                """.formatted(tenantId, LAB));
        String rangeId = range.get("rangeId").asText();

        mockMvc.perform(put("/api/catalog/reference-ranges/{id}", rangeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"effectiveFrom\":\"2026-01-01\",\"segments\":[]}"))
                .andExpect(status().isNotImplemented());

        mockMvc.perform(get("/api/catalog/reference-ranges/effective").param("analyteId", "analyte-1"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void sampleTypeAndRequirementValidateMinimumVolumeAndPublishIsAHook() throws Exception {
        JsonNode sampleType = postJson("/api/catalog/samples/types", """
                {"tenantId":"%s","laboratoryId":"%s","code":"SMP-1","nameEn":"Serum","nameEs":"Suero","matrix":"serum"}
                """.formatted(tenantId, LAB));
        String sampleTypeId = sampleType.get("sampleTypeId").asText();

        mockMvc.perform(post("/api/catalog/samples/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tenantId":"%s","laboratoryId":"%s","sampleTypeRefId":"%s","minVolumeMl":-1}
                        """.formatted(tenantId, LAB, sampleTypeId)))
                .andExpect(status().isBadRequest());

        JsonNode requirement = postJson("/api/catalog/samples/requirements", """
                {"tenantId":"%s","laboratoryId":"%s","sampleTypeRefId":"%s","minVolumeMl":2.5}
                """.formatted(tenantId, LAB, sampleTypeId));
        String requirementId = requirement.get("requirementId").asText();

        mockMvc.perform(post("/api/catalog/samples/requirements/{id}/publish", requirementId))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void priceListAddsEntriesAndPublishUpdateAndEffectiveSnapshotAreHooks() throws Exception {
        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"PRC-1","nameEn":"Standard","nameEs":"Estandar",
                 "currency":"USD","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, LAB));
        String priceListId = priceList.get("priceListId").asText();

        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemType\":\"test\",\"itemRefId\":\"test-1\",\"amount\":-5}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemType\":\"test\",\"itemRefId\":\"test-1\",\"amount\":25.50}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/catalog/price-lists/{id}", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nameEn\":\"Updated\",\"nameEs\":\"Actualizado\"}"))
                .andExpect(status().isNotImplemented());

        mockMvc.perform(post("/api/catalog/price-lists/{id}/publish", priceListId))
                .andExpect(status().isNotImplemented());

        mockMvc.perform(get("/api/catalog/price-lists/effective")
                        .param("itemType", "test")
                        .param("itemRefId", "test-1"))
                .andExpect(status().isNotImplemented());

        mockMvc.perform(post("/api/catalog/price-lists/{id}/deprecate", priceListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("deprecated"));
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
