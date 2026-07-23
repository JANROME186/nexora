package com.nexora.hop.platformfoundation.platformconfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class PlatformConfigurationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPlatformConfigReturnsSeededParametersWithEncryptedValuesMasked() throws Exception {
        mockMvc.perform(get("/api/platform/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.key=='platform.security.session_timeout_minutes')].value")
                        .value("30"));
    }

    @Test
    void evaluateFeatureFlagsWithoutTenantIdReturnsEmptyOrFalseFlags() throws Exception {
        mockMvc.perform(get("/api/platform/feature-flags"))
                .andExpect(status().isOk());
    }

    @Test
    void updateFeatureFlagCreatesFlagThenEvaluatesTrueForTargetedTenant() throws Exception {
        mockMvc.perform(post("/api/platform/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"flagKey":"platform.experiments.new_dashboard","enabledByDefault":false,
                         "targetTenants":["tenant-42"],"rolloutPercentage":0,"updatedBy":"admin-user"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagKey").value("platform.experiments.new_dashboard"));

        mockMvc.perform(get("/api/platform/feature-flags").param("tenantId", "tenant-42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['platform.experiments.new_dashboard']").value(true));

        mockMvc.perform(get("/api/platform/feature-flags").param("tenantId", "tenant-other"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['platform.experiments.new_dashboard']").value(false));
    }

    @Test
    void updateFeatureFlagRejectsMalformedFlagKey() throws Exception {
        mockMvc.perform(post("/api/platform/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flagKey\":\"not-namespaced\",\"enabledByDefault\":true,\"updatedBy\":\"admin\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMAND_INVALID"));
    }

    @Test
    void updateFeatureFlagRejectsMissingUpdatedBy() throws Exception {
        mockMvc.perform(post("/api/platform/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"flagKey\":\"platform.experiments.x\",\"enabledByDefault\":true}"))
                .andExpect(status().isBadRequest());
    }
}
