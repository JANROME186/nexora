package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexora.hop.platformfoundation.identityaccess.application.IdentityAccessService;
import com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Standalone endpoint coverage for IAM administration operations that do not need
 * a full Spring context or database.
 */
class IdentityAccessControllerStandaloneTest {

    private IdentityAccessService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(IdentityAccessService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new IdentityAccessController(service)).build();
    }

    @Test
    void createServiceAccountReturnsTheCredentialProjection() throws Exception {
        ServiceAccountCredential credential = new ServiceAccountCredential(
                "svc-1",
                "tenant-1",
                "integration-client",
                "hashed-secret",
                "ROLE_API_PARTNER",
                "ACTIVE",
                Instant.parse("2026-07-26T20:00:00Z"));
        when(service.createServiceAccount("tenant-1", "integration-client", "client-secret", "ROLE_API_PARTNER"))
                .thenReturn(credential);

        String payload = """
                {"tenantId":"tenant-1","clientId":"integration-client","clientSecret":"client-secret","roleCode":"ROLE_API_PARTNER"}
                """;

        mockMvc.perform(post("/api/identity/service-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/identity/service-accounts/svc-1"))
                .andExpect(jsonPath("$.serviceAccountId").value("svc-1"))
                .andExpect(jsonPath("$.tenantId").value("tenant-1"))
                .andExpect(jsonPath("$.clientId").value("integration-client"))
                .andExpect(jsonPath("$.roleCode").value("ROLE_API_PARTNER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").value("2026-07-26T20:00:00Z"));
    }
}
