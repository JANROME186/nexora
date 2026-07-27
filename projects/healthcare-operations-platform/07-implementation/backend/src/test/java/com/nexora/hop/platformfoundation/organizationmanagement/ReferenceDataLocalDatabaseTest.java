package com.nexora.hop.platformfoundation.organizationmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Validates that {@code JdbcReferenceDataRepository} actually reads the seeded
 * organization.countries/locales/currencies rows from the real Postgres schema created by
 * db/platform-foundation/schema.sql (TD-DB-003). Mirrors OrganizationManagementLocalDatabaseTest.
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class ReferenceDataLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void countriesLocalesAndCurrenciesArePersistedInPostgres() throws Exception {
        JsonNode countries = getJson("/api/organization/reference-data/countries");
        JsonNode locales = getJson("/api/organization/reference-data/locales");
        JsonNode currencies = getJson("/api/organization/reference-data/currencies");

        assertThat(countries.isArray()).isTrue();
        assertThat(countries.size()).isGreaterThanOrEqualTo(2);
        assertThat(locales.size()).isGreaterThanOrEqualTo(2);
        assertThat(currencies.size()).isGreaterThanOrEqualTo(2);

        boolean hasMexico = false;
        for (JsonNode country : countries) {
            if ("MX".equals(country.get("countryCode").asText())) {
                hasMexico = true;
                assertThat(country.get("nameEsMx").asText()).isEqualTo("México");
                assertThat(country.get("nameEnUs").asText()).isEqualTo("Mexico");
            }
        }
        assertThat(hasMexico).isTrue();
    }

    private JsonNode getJson(String path) throws Exception {
        MvcResult result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
