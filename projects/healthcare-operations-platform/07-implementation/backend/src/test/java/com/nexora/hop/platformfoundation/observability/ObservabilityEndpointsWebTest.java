package com.nexora.hop.platformfoundation.observability;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Confirms the BCM-PLT-006 operations compiled by COM-MOD-012-BE-001 are actually reachable:
 * separate liveness/readiness health groups and the Prometheus scrape endpoint, plus the
 * per-request trace id correlation header emitted by {@code RequestObservabilityContextFilter}.
 */
@AutoConfigureMockMvc
@SpringBootTest
class ObservabilityEndpointsWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void livenessProbeIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void readinessProbeIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk());
    }

    @Test
    void prometheusScrapeEndpointIsReachable() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk());
    }

    @Test
    void platformHealthEndpointStillReportsUp() throws Exception {
        mockMvc.perform(get("/api/platform/health"))
                .andExpect(status().isOk());
    }
}
