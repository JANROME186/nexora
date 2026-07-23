package com.nexora.hop.platformfoundation.platformconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class PlatformConfigurationLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void seededConfigParametersArePersistedInPostgres() {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from platform_configuration.config_parameters where config_key = ?",
                Integer.class,
                "platform.operations.maintenance_mode");

        assertThat(count).isOne();
    }

    @Test
    void updatedFeatureFlagIsPersistedInPostgres() throws Exception {
        mockMvc.perform(post("/api/platform/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"flagKey":"platform.experiments.persisted_flag","enabledByDefault":true,
                         "targetTenants":[],"rolloutPercentage":10,"updatedBy":"admin-user"}
                        """))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from platform_configuration.feature_flags where flag_key = ? and enabled_by_default = true",
                Integer.class,
                "platform.experiments.persisted_flag");

        assertThat(count).isOne();
    }
}
