package com.nexora.hop.platformfoundation.observability;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * COM-MOD-012-QA-001 resilience finding: the standard Spring Boot readiness probe group only
 * includes {@code readinessState} by default, so it stays UP even when the database is
 * unreachable. application-local.properties now also includes {@code db} in the readiness group so an
 * orchestrator stops routing traffic to a pod whose database connection is down; this test
 * confirms that group configuration resolves and reports UP against the real local Postgres
 * datasource (it previously failed application context startup with "Health contributor 'db'
 * ... does not exist" when the same include was placed in the base application.properties, which has no
 * datasource bean in non-local profiles).
 */
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class ObservabilityReadinessLocalDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readinessGroupIncludesDatabaseAndReportsUpAgainstTheRealLocalDatabase() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\"}"));
    }
}
