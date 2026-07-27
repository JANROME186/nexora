package com.nexora.hop.platformfoundation.identityaccess.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/** TD-IAM-003: service-account (non-interactive) session resolution. */
class HopAuthenticationResolverTest {

    private final HopSecurityProperties properties = new HopSecurityProperties(
            true, false, "local-dev-token", "tenant-local", "branch-local", "local-dev-fixture-user", "ADMIN");

    @Test
    void resolvesAnActiveServiceAccountToItsPersistedRoleCode() {
        IdentityRepository repository = mock(IdentityRepository.class);
        ServiceAccountCredential credential = new ServiceAccountCredential(
                "svc-1", "tenant-a", "integration-client", "hash", "MARKETPLACE_OPERATOR", "active", Instant.now());
        when(repository.findServiceAccountCredentialById("svc-1")).thenReturn(Optional.of(credential));

        HopAuthenticationResolver resolver = new HopAuthenticationResolver(properties, repository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer service-session:tenant-a:svc-1");

        Optional<AuthenticatedUserContext> context = resolver.resolve(request);

        assertThat(context).isPresent();
        assertThat(context.get().userId()).isEqualTo("svc-1");
        assertThat(context.get().tenantId()).isEqualTo("tenant-a");
        assertThat(context.get().roleCodes()).containsExactly("MARKETPLACE_OPERATOR");
        assertThat(context.get().fixture()).isFalse();
    }

    @Test
    void rejectsAServiceSessionTokenWhoseTenantDoesNotMatchThePersistedCredential() {
        IdentityRepository repository = mock(IdentityRepository.class);
        ServiceAccountCredential credential = new ServiceAccountCredential(
                "svc-1", "tenant-a", "integration-client", "hash", "MARKETPLACE_OPERATOR", "active", Instant.now());
        when(repository.findServiceAccountCredentialById("svc-1")).thenReturn(Optional.of(credential));

        HopAuthenticationResolver resolver = new HopAuthenticationResolver(properties, repository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer service-session:tenant-b:svc-1");

        assertThat(resolver.resolve(request)).isEmpty();
    }

    @Test
    void rejectsASuspendedServiceAccount() {
        IdentityRepository repository = mock(IdentityRepository.class);
        ServiceAccountCredential credential = new ServiceAccountCredential(
                "svc-1", "tenant-a", "integration-client", "hash", "MARKETPLACE_OPERATOR", "suspended", Instant.now());
        when(repository.findServiceAccountCredentialById("svc-1")).thenReturn(Optional.of(credential));

        HopAuthenticationResolver resolver = new HopAuthenticationResolver(properties, repository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer service-session:tenant-a:svc-1");

        assertThat(resolver.resolve(request)).isEmpty();
    }

    @Test
    void rejectsAServiceSessionTokenForAnUnknownServiceAccount() {
        IdentityRepository repository = mock(IdentityRepository.class);
        when(repository.findServiceAccountCredentialById("svc-unknown")).thenReturn(Optional.empty());

        HopAuthenticationResolver resolver = new HopAuthenticationResolver(properties, repository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer service-session:tenant-a:svc-unknown");

        assertThat(resolver.resolve(request)).isEmpty();
    }

    @Test
    void rejectsAServiceSessionTokenWhenNoIdentityRepositoryIsWired() {
        HopAuthenticationResolver resolver = new HopAuthenticationResolver(properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer service-session:tenant-a:svc-1");

        assertThat(resolver.resolve(request)).isEmpty();
    }
}
