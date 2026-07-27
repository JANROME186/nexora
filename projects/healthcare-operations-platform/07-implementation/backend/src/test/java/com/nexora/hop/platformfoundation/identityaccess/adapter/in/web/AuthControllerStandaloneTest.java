package com.nexora.hop.platformfoundation.identityaccess.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.hop.platformfoundation.identityaccess.application.AuthenticationFailedException;
import com.nexora.hop.platformfoundation.identityaccess.application.IdentityAccessService;
import com.nexora.hop.platformfoundation.identityaccess.application.MfaRequiredException;
import com.nexora.hop.platformfoundation.identityaccess.application.MfaVerificationFailedException;
import com.nexora.hop.platformfoundation.identityaccess.security.AuthenticatedUserContext;
import com.nexora.hop.platformfoundation.identityaccess.security.HopAuthenticationResolver;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Standalone (no Spring context, no database) MockMvc coverage for {@link AuthController}'s
 * MFA (TD-IAM-003) and service-account (TD-IAM-003) endpoints, complementing the DB-gated
 * end-to-end {@code AuthControllerTest}.
 */
class AuthControllerStandaloneTest {

    private IdentityAccessService service;
    private HopAuthenticationResolver authenticationResolver;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = mock(IdentityAccessService.class);
        authenticationResolver = mock(HopAuthenticationResolver.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service, authenticationResolver))
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    @Test
    void loginPassesTheMfaCodeThroughToTheService() throws Exception {
        when(service.login(eq("tenant-1"), eq("ada"), eq("secret123"), any(), eq("654321")))
                .thenReturn("local-session:tenant-1:user-1");

        String payload = """
                {"tenantId":"tenant-1","username":"ada","password":"secret123","mfaCode":"654321"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("local-session:tenant-1:user-1"));
    }

    @Test
    void loginReturns401WhenMfaCodeIsRequiredButMissing() throws Exception {
        when(service.login(anyString(), anyString(), anyString(), any(), isNull()))
                .thenThrow(new MfaRequiredException("A multi-factor verification code is required."));

        String payload = """
                {"tenantId":"tenant-1","username":"ada","password":"secret123"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginReturns401WhenTheMfaCodeDoesNotVerify() throws Exception {
        when(service.login(anyString(), anyString(), anyString(), any(), eq("000000")))
                .thenThrow(new MfaVerificationFailedException("The multi-factor verification code is invalid."));

        String payload = """
                {"tenantId":"tenant-1","username":"ada","password":"secret123","mfaCode":"000000"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mfaEnrollReturnsASecretWhenTheCallerIsAuthenticated() throws Exception {
        when(authenticationResolver.resolve(any()))
                .thenReturn(Optional.of(new AuthenticatedUserContext("user-1", "tenant-1", "branch-1", List.of("ADMIN"), true)));
        when(service.enrollMfa("user-1")).thenReturn("JBSWY3DPEHPK3PXP");

        mockMvc.perform(post("/api/auth/mfa/enroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").value("JBSWY3DPEHPK3PXP"));
    }

    @Test
    void mfaEnrollReturns401WhenTheCallerIsNotAuthenticated() throws Exception {
        when(authenticationResolver.resolve(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/mfa/enroll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void serviceTokenAuthenticatesAServiceAccountByClientCredentials() throws Exception {
        when(service.authenticateServiceAccount("integration-client", "client-secret-value"))
                .thenReturn("service-session:tenant-1:svc-1");

        String payload = """
                {"clientId":"integration-client","clientSecret":"client-secret-value"}
                """;

        mockMvc.perform(post("/api/auth/service-token").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("service-session:tenant-1:svc-1"));
    }

    @Test
    void serviceTokenReturns401ForInvalidClientCredentials() throws Exception {
        when(service.authenticateServiceAccount("integration-client", "wrong-secret"))
                .thenThrow(new AuthenticationFailedException("Invalid login credentials."));

        String payload = """
                {"clientId":"integration-client","clientSecret":"wrong-secret"}
                """;

        mockMvc.perform(post("/api/auth/service-token").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isUnauthorized());
    }
}
