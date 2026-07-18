package com.nexora.hop.platformfoundation.identityaccess.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hop.security")
public record HopSecurityProperties(
    boolean enforceRequestAuthorization,
    boolean localFixtureEnabled,
    String localFixtureToken,
    String localFixtureTenantId,
    String localFixtureBranchId,
    String localFixtureUserId,
    String localFixtureRoleCode) {

  public HopSecurityProperties {
    if (localFixtureToken == null || localFixtureToken.isBlank()) {
      localFixtureToken = "local-dev-token";
    }
    if (localFixtureTenantId == null || localFixtureTenantId.isBlank()) {
      localFixtureTenantId = "tenant-local";
    }
    if (localFixtureBranchId == null || localFixtureBranchId.isBlank()) {
      localFixtureBranchId = "branch-local";
    }
    if (localFixtureUserId == null || localFixtureUserId.isBlank()) {
      localFixtureUserId = "local-dev-fixture-user";
    }
    if (localFixtureRoleCode == null || localFixtureRoleCode.isBlank()) {
      localFixtureRoleCode = "ADMIN";
    }
  }
}
