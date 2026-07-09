package com.nexora.hop.platformfoundation.catalogtestconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Validates that the JDBC adapters compiled for catalog-test-configuration actually persist to the
 * real Postgres schema created by db/catalog-test-configuration/schema.sql. Mirrors
 * OrganizationManagementLocalDatabaseTest.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class CatalogTestConfigurationLocalDatabaseTest {

    private static final String LAB = "lab-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void diagnosticServiceAndPriceListEntriesArePersistedInPostgres() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Catalog DB Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        JsonNode service = postJson("/api/catalog/diagnostic-services", """
                {"tenantId":"%s","laboratoryId":"%s","code":"DB-SVC-1","nameEn":"Basic","nameEs":"Basico",
                 "serviceType":"test","components":[]}
                """.formatted(tenantId, LAB));
        String serviceId = service.get("serviceId").asText();

        JsonNode priceList = postJson("/api/catalog/price-lists", """
                {"tenantId":"%s","laboratoryId":"%s","code":"DB-PRC-1","nameEn":"Standard","nameEs":"Estandar",
                 "currency":"USD","effectiveFrom":"2026-01-01"}
                """.formatted(tenantId, LAB));
        String priceListId = priceList.get("priceListId").asText();

        mockMvc.perform(post("/api/catalog/price-lists/{id}/entries", priceListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemType\":\"service\",\"itemRefId\":\"" + serviceId + "\",\"amount\":19.99}"))
                .andExpect(status().isCreated());

        Integer serviceCount = jdbcTemplate.queryForObject(
                "select count(*) from catalog.diagnostic_services where service_id = ?", Integer.class, serviceId);
        Integer priceListCount = jdbcTemplate.queryForObject(
                "select count(*) from catalog.price_lists where price_list_id = ?", Integer.class, priceListId);
        Integer entryCount = jdbcTemplate.queryForObject(
                "select count(*) from catalog.price_entries where price_list_id = ?", Integer.class, priceListId);

        assertThat(serviceCount).isOne();
        assertThat(priceListCount).isOne();
        assertThat(entryCount).isOne();
    }

    private JsonNode postJson(String path, String json) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
