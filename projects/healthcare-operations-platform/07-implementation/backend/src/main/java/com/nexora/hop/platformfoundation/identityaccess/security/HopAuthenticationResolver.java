package com.nexora.hop.platformfoundation.identityaccess.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HopAuthenticationResolver {

  static final String AUTHORIZATION = "Authorization";
  static final String AUTH_TOKEN = "X-HOP-AUTH-TOKEN";
  static final String USER_ID = "X-HOP-USER-ID";
  static final String TENANT_ID = "X-HOP-TENANT-ID";
  static final String BRANCH_ID = "X-HOP-BRANCH-ID";
  static final String ROLES = "X-HOP-ROLES";

  private final HopSecurityProperties properties;

  public HopAuthenticationResolver(HopSecurityProperties properties) {
    this.properties = properties;
  }

  public Optional<AuthenticatedUserContext> resolve(HttpServletRequest request) {
    String token =
        bearerToken(request.getHeader(AUTHORIZATION)).orElse(request.getHeader(AUTH_TOKEN));
    if (token == null || token.isBlank()) {
      return localFixtureContext();
    }
    if (properties.localFixtureToken().equals(token)) {
      return Optional.of(localFixture());
    }
    if (token.startsWith("local-session:")) {
      return localSessionContext(token, request);
    }
    return Optional.empty();
  }

  private Optional<AuthenticatedUserContext> localSessionContext(
      String token, HttpServletRequest request) {
    String[] parts = token.split(":", 3);
    if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
      return Optional.empty();
    }
    return Optional.of(
        new AuthenticatedUserContext(
            headerOrDefault(request, USER_ID, parts[2]),
            headerOrDefault(request, TENANT_ID, parts[1]),
            headerOrDefault(request, BRANCH_ID, properties.localFixtureBranchId()),
            roleCodes(request),
            true));
  }

  private Optional<AuthenticatedUserContext> localFixtureContext() {
    if (!properties.localFixtureEnabled()) {
      return Optional.empty();
    }
    return Optional.of(localFixture());
  }

  private AuthenticatedUserContext localFixture() {
    return new AuthenticatedUserContext(
        properties.localFixtureUserId(),
        properties.localFixtureTenantId(),
        properties.localFixtureBranchId(),
        List.of(properties.localFixtureRoleCode()),
        true);
  }

  private List<String> roleCodes(HttpServletRequest request) {
    String roles = request.getHeader(ROLES);
    if (roles == null || roles.isBlank()) {
      return List.of(properties.localFixtureRoleCode());
    }
    return Arrays.stream(roles.split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .distinct()
        .toList();
  }

  private static String headerOrDefault(
      HttpServletRequest request, String header, String fallback) {
    String value = request.getHeader(header);
    return value == null || value.isBlank() ? fallback : value;
  }

  private static Optional<String> bearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return Optional.empty();
    }
    String token = authorizationHeader.substring("Bearer ".length()).trim();
    return token.isBlank() ? Optional.empty() : Optional.of(token);
  }
}
