package com.nexora.hop.platformfoundation.identityaccess.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexora.hop.platformfoundation.identityaccess.application.AuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HopAuthorizationInterceptorTest {

  private final EndpointPermissionRegistry endpointPermissionRegistry =
      new EndpointPermissionRegistry();

  @AfterEach
  void clearContext() {
    AuthenticatedUserContextHolder.clear();
  }

  @Test
  void deniesMappedEndpointWhenAuthenticationIsMissingAndNoFixtureIsAllowed() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/people/patients");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("AUTHENTICATION_REQUIRED");
  }

  @Test
  void deniesMappedEndpointWhenRoleDoesNotGrantRequiredPermission() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/revenue/cashier/sessions");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "FRONT_DESK");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
    assertThat(response.getContentAsString()).contains("PERMISSION_DENIED");
  }

  @Test
  void allowsMappedEndpointWhenRoleGrantsRequiredPermissionAndBindsContext() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/revenue/cashier/sessions");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "CASHIER");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(AuthenticatedUserContextHolder.current()).isPresent();
    assertThat(AuthenticatedUserContextHolder.current().get().userId()).isEqualTo("user-a");
  }

  private HopAuthorizationInterceptor interceptor(HopSecurityProperties properties) {
    return new HopAuthorizationInterceptor(
        endpointPermissionRegistry,
        new HopAuthenticationResolver(properties),
        new AuthorizationService(),
        properties);
  }

  private static MockHttpServletRequest request(String method, String path) {
    var request = new MockHttpServletRequest(method, path);
    request.setRequestURI(path);
    return request;
  }

  private static HopSecurityProperties properties(boolean localFixtureEnabled) {
    return new HopSecurityProperties(
        true,
        localFixtureEnabled,
        "local-dev-token",
        "tenant-local",
        "branch-local",
        "local-dev-fixture-user",
        "ADMIN");
  }
}
