package com.nexora.hop.platformfoundation.organizationmanagement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** TD-DB-003: MockMvc coverage for the country/locale/currency reference-data read API. */
@AutoConfigureMockMvc
@SpringBootTest
class ReferenceDataApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsCountriesWithEsMxAndEnUsVariants() throws Exception {
        mockMvc.perform(get("/api/organization/reference-data/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.countryCode == 'MX')].nameEsMx").value("México"))
                .andExpect(jsonPath("$[?(@.countryCode == 'MX')].nameEnUs").value("Mexico"))
                .andExpect(jsonPath("$[?(@.countryCode == 'US')].nameEnUs").value("United States"));
    }

    @Test
    void listsLocalesWithDefaultFlag() throws Exception {
        mockMvc.perform(get("/api/organization/reference-data/locales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.localeCode == 'es-MX')].isDefault").value(true))
                .andExpect(jsonPath("$[?(@.localeCode == 'en-US')].isDefault").value(false));
    }

    @Test
    void listsCurrenciesWithMinorUnitDigits() throws Exception {
        mockMvc.perform(get("/api/organization/reference-data/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.currencyCode == 'MXN')].nameEsMx").value("Peso mexicano"))
                .andExpect(jsonPath("$[?(@.currencyCode == 'USD')].minorUnitDigits").value(2));
    }
}
