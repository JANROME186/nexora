package com.nexora.hop.platformfoundation.aioverlay;

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
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class AiOverlayLocalDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void aiOverlaySchemaIsInitializedInPostgres() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from information_schema.tables
                 where table_schema = 'ai_overlay'
                   and table_name = 'ai_interactions'
                """, Integer.class);

        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void assistantDraftPersistsAgainstRealPostgres() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ai/assistant/sessions")
                        .header("X-Tenant-Id", "tenant-ai-pg")
                        .header("X-User-Id", "clinician-pg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"purpose":"case summary","sourceContextType":"Case","sourceContextId":"case-pg-1",
                                 "prompt":"Summarize the operational case status."}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());

        Integer rowCount = jdbcTemplate.queryForObject("""
                select count(*)
                  from ai_overlay.ai_interactions
                 where tenant_id = ? and session_id = ?
                """, Integer.class, "tenant-ai-pg", created.get("sessionId").asText());
        assertThat(rowCount).isEqualTo(1);
    }
}
