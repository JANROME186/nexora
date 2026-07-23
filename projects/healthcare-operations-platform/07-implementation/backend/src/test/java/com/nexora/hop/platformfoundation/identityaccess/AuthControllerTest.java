package com.nexora.hop.platformfoundation.identityaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.nexora.hop.platformfoundation.identityaccess.application.CreateUserCommand;
import com.nexora.hop.platformfoundation.identityaccess.application.AssignRoleCommand;
import com.nexora.hop.platformfoundation.identityaccess.application.IdentityAccessService;
import com.nexora.hop.platformfoundation.identityaccess.domain.UserAccount;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest
@EnabledIfSystemProperty(named = "hop.local-db-tests", matches = "true")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IdentityAccessService service;

    @Test
    void testAuthenticationAndImpersonationFlows() throws Exception {
        // 1. Create a Tenant
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Auth Test Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        // 2. Create a User with password
        UserAccount user = service.createUser(
                new CreateUserCommand(tenantId, "Portal User", "portal.user@nexora.example"),
                "portaluser",
                "secret123"
        );

        // 3. Successful Login
        String loginPayload = """
                {
                    "tenantId": "%s",
                    "username": "portaluser",
                    "password": "secret123",
                    "locale": "es-MX"
                }
                """.formatted(tenantId);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginResponse.get("token").asText();
        assertThat(token).startsWith("local-session:");

        // 4. Failed Login attempt (incorrect password)
        String failedPayload = """
                {
                    "tenantId": "%s",
                    "username": "portaluser",
                    "password": "wrongPassword",
                    "locale": "es-MX"
                }
                """.formatted(tenantId);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(failedPayload))
                .andExpect(status().isUnauthorized());

        // 5. Account Lockout after 5 failed attempts
        // The first failed attempt was #1. Let's do 4 more.
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(failedPayload))
                    .andExpect(status().isUnauthorized());
        }

        // The 6th attempt (or any subsequent attempt) should be forbidden (403) due to account locked
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(failedPayload))
                .andExpect(status().isForbidden());

        // 6. Test logout
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // 7. Support Assisted Login (Impersonation)
        // Let's create a Support staff user, assign them the SUPPORT role
        UserAccount supportUser = service.createUser(
                new CreateUserCommand(tenantId, "Support Agent", "support@nexora.example"),
                "supportagent",
                "support123"
        );
        service.assignRole(supportUser.userId(), new AssignRoleCommand(
                "SUPPORT", "tenant", tenantId, "tester-1"
        ));

        // Create a support token manually (since login is locked for our portal user)
        String supportToken = "local-session:" + tenantId + ":" + supportUser.userId();

        // Support initiates assistance for the patient (user.userId())
        String assistancePayload = """
                {
                    "assistedUserId": "%s",
                    "ticketReference": "TICKET-101"
                }
                """.formatted(user.userId());

        MvcResult assistanceResult = mockMvc.perform(post("/api/auth/assistance")
                .header("Authorization", "Bearer " + supportToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assistancePayload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode assistanceResponse = objectMapper.readTree(assistanceResult.getResponse().getContentAsString());
        String assistedToken = assistanceResponse.get("assistedToken").asText();
        assertThat(assistedToken).startsWith("assistance-session:");

        // Verify the assisted token is sandboxed: trying to access standard administrative APIs should be forbidden.
        // /api/identity/users/{userId} requires SCREEN_USERS which SUPPORT role doesn't have.
        mockMvc.perform(get("/api/identity/users/" + user.userId())
                .header("Authorization", "Bearer " + assistedToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void assistanceForANonexistentAssistedUserReturnsNotFoundInsteadOfServerError() throws Exception {
        JsonNode tenant = postJson("/api/platform/tenants", "{\"name\":\"Auth Not Found Test Tenant\"}");
        String tenantId = tenant.get("tenantId").asText();

        UserAccount supportUser = service.createUser(
                new CreateUserCommand(tenantId, "Support Agent 2", "support2@nexora.example"),
                "supportagent2",
                "support123"
        );
        service.assignRole(supportUser.userId(), new AssignRoleCommand(
                "SUPPORT", "tenant", tenantId, "tester-2"
        ));
        String supportToken = "local-session:" + tenantId + ":" + supportUser.userId();

        String assistancePayload = """
                {
                    "assistedUserId": "00000000-0000-0000-0000-000000000000",
                    "ticketReference": "TICKET-404"
                }
                """;

        mockMvc.perform(post("/api/auth/assistance")
                .header("Authorization", "Bearer " + supportToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(assistancePayload))
                .andExpect(status().isNotFound());
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
