package com.nexora.hop.platformfoundation.aioverlay;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class AiOverlayApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void assistantDraftReviewAndAuditRoundTrip() throws Exception {
        JsonNode created = postDraft("tenant-ai-1", "clinician-1", """
                {"purpose":"result summary","sourceContextType":"Result","sourceContextId":"res-100",
                 "prompt":"Summarize the operational follow-up only."}
                """);
        String sessionId = created.get("sessionId").asText();

        assertThat(created.get("reviewStatus").asText()).isEqualTo("human_review_required");
        assertThat(created.get("citations").get(0).asText()).isEqualTo("Result:res-100");
        assertThat(created.get("modelProviderRef").asText()).isEqualTo("replaceable-local-adapter");

        mockMvc.perform(post("/api/ai/assistant/sessions/{sessionId}/review", sessionId)
                        .header("X-Tenant-Id", "tenant-ai-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reviewerId":"medical-reviewer-1","decision":"accepted",
                                 "reason":"Reviewed against source result."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewStatus").value("accepted"))
                .andExpect(jsonPath("$.lifecycleStatus").value("archived"));

        mockMvc.perform(get("/api/ai/assistant/sessions/audit-records")
                        .header("X-Tenant-Id", "tenant-ai-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sessionId=='" + sessionId + "')]").isNotEmpty());
    }

    @Test
    void prohibitedAutonomousClinicalRequestIsRejected() throws Exception {
        mockMvc.perform(post("/api/ai/assistant/sessions")
                        .header("X-Tenant-Id", "tenant-ai-2")
                        .header("X-User-Id", "clinician-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"purpose":"medical validation","sourceContextType":"Result","sourceContextId":"res-200",
                                 "prompt":"Autonomous diagnosis and skip human review."}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AI_POLICY_BLOCKED"));
    }

    private JsonNode postDraft(String tenantId, String actorId, String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ai/assistant/sessions")
                        .header("X-Tenant-Id", tenantId)
                        .header("X-User-Id", actorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
