package com.nexora.hop.platformfoundation.identityaccess.security;

import com.nexora.hop.platformfoundation.identityaccess.domain.IdentityRepository;
import com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final IdentityRepository identityRepository;

  public HopAuthenticationResolver(HopSecurityProperties properties) {
    this(properties, null);
  }

  @Autowired
  public HopAuthenticationResolver(HopSecurityProperties properties, IdentityRepository identityRepository) {
    this.properties = properties;
    this.identityRepository = identityRepository;
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
    if (token.startsWith("assistance-session:")) {
      return assistanceSessionContext(token, request);
    }
    if (token.startsWith("service-session:")) {
      return serviceAccountSessionContext(token);
    }
    return Optional.empty();
  }

  /**
   * TD-IAM-003: resolves a non-interactive service-account principal. Unlike {@code
   * local-session:}, the role is never trusted from the token itself — it is always looked up
   * from the persisted {@link com.nexora.hop.platformfoundation.identityaccess.domain.ServiceAccountCredential}
   * so a forged token cannot grant an arbitrary role.
   */
  private Optional<AuthenticatedUserContext> serviceAccountSessionContext(String token) {
    String[] parts = token.split(":", 3);
    if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank() || identityRepository == null) {
      return Optional.empty();
    }
    String tenantId = parts[1];
    String serviceAccountId = parts[2];
    return identityRepository
        .findServiceAccountCredentialById(serviceAccountId)
        .filter(credential -> credential.tenantId().equals(tenantId) && credential.isActive())
        .map(
            credential ->
                new AuthenticatedUserContext(
                    serviceAccountId,
                    tenantId,
                    properties.localFixtureBranchId(),
                    List.of(credential.roleCode()),
                    false));
  }

  private Optional<AuthenticatedUserContext> assistanceSessionContext(
      String token, HttpServletRequest request) {
    String[] parts = token.split(":", 4);
    if (parts.length != 4 || parts[1].isBlank() || parts[2].isBlank() || parts[3].isBlank()) {
      return Optional.empty();
    }
    return Optional.of(
        new AuthenticatedUserContext(
            parts[2],
            parts[1],
            headerOrDefault(request, BRANCH_ID, properties.localFixtureBranchId()),
            List.of("SUPPORT"),
            true));
  }

  private Optional<AuthenticatedUserContext> localSessionContext(
      String token, HttpServletRequest request) {
    String[] parts = token.split(":", 3);
    if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
      return Optional.empty();
    }
    List<String> roles = List.of();
    if (identityRepository != null) {
      roles = identityRepository.findRoleAssignmentsByUserId(parts[2]).stream()
          .map(RoleAssignment::roleCode)
          .toList();
    }
    if (roles.isEmpty()) {
      roles = roleCodes(request);
    }
    return Optional.of(
        new AuthenticatedUserContext(
            headerOrDefault(request, USER_ID, parts[2]),
            headerOrDefault(request, TENANT_ID, parts[1]),
            headerOrDefault(request, BRANCH_ID, properties.localFixtureBranchId()),
            roles,
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
