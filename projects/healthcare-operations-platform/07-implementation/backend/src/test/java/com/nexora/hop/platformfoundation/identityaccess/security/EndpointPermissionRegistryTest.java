package com.nexora.hop.platformfoundation.identityaccess.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode;
import org.junit.jupiter.api.Test;

class EndpointPermissionRegistryTest {

  private final EndpointPermissionRegistry registry = new EndpointPermissionRegistry();

  @Test
  void resolvesCapabilityPermissionAndHttpAction() {
    var rule = registry.resolve("POST", "/api/people/patients/patient-001/merge");

    assertThat(rule).isPresent();
    assertThat(rule.get().permission()).isEqualTo(PermissionCode.SCREEN_PATIENTS);
    assertThat(rule.get().action()).isEqualTo(AccessAction.CREATE);
    assertThat(rule.get().capability()).isEqualTo("BCM-PER-002");
  }

  @Test
  void leavesHealthEndpointPublic() {
    assertThat(registry.resolve("GET", "/api/platform/health")).isEmpty();
  }

  @Test
  void resolvesBillingRequestEndpointToBillingPermission() {
    var rule = registry.resolve("GET", "/api/revenue/billing-requests/invoice-request-001");

    assertThat(rule).isPresent();
    assertThat(rule.get().permission()).isEqualTo(PermissionCode.SCREEN_BILLING_REQUESTS);
    assertThat(rule.get().action()).isEqualTo(AccessAction.READ);
    assertThat(rule.get().capability()).isEqualTo("BCM-REV-003");
  }

  @Test
  void resolvesAiOverlayEndpointToAssistantPermission() {
    var rule = registry.resolve("POST", "/api/ai/assistant/sessions");

    assertThat(rule).isPresent();
    assertThat(rule.get().permission()).isEqualTo(PermissionCode.SCREEN_AI_ASSISTANT);
    assertThat(rule.get().action()).isEqualTo(AccessAction.CREATE);
    assertThat(rule.get().capability()).isEqualTo("BCM-AI-001");
  }
}
