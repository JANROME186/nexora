package com.nexora.hop.platformfoundation.identityaccess.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexora.hop.platformfoundation.identityaccess.application.AuthorizationService;
import com.nexora.hop.platformfoundation.sharedkernel.security.CurrentTenantContext;
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
    CurrentTenantContext.clear();
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
    assertThat(CurrentTenantContext.current()).contains("tenant-a");
  }

  @Test
  void clearsCurrentTenantContextAfterCompletion() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/revenue/cashier/sessions");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "CASHIER");
    var response = new MockHttpServletResponse();

    interceptor.preHandle(request, response, new Object());
    assertThat(CurrentTenantContext.current()).isPresent();

    interceptor.afterCompletion(request, response, new Object(), null);

    assertThat(CurrentTenantContext.current()).isEmpty();
  }

  @Test
  void adminMayApproveCapaThroughTheDomainResourceActionScopeGrammar() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("POST", "/api/quality/capa/capa-1/approve");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "ADMIN");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void roleWithoutTheScopedCapaApprovalGrammarPermissionCannotApproveCapa() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("POST", "/api/quality/capa/capa-1/approve");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "CASHIER");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  void referringDoctorWithMatchingDoctorIdQueryParamMayListTheirOwnOrders() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/clinical-operations/diagnostic-orders");
    request.setParameter("doctorId", "Doctor-01");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Doctor-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "REFERRING_DOCTOR");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void referringDoctorCannotListOrdersUnderAnotherDoctorId() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/clinical-operations/diagnostic-orders");
    request.setParameter("doctorId", "Doctor-02");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Doctor-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "REFERRING_DOCTOR");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  void referringDoctorMayReachResultsHistoryPermissionCheck() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/results/history/patient/Patient-01");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Doctor-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "REFERRING_DOCTOR");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void referringDoctorMayReachResultNotificationsPermissionCheck() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request =
        request("GET", "/api/clinical-operations/laboratory-results/res-1/notifications");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Doctor-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "REFERRING_DOCTOR");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void patientWithMatchingPatientIdQueryParamMayListTheirOwnImagingDeliveryPackages() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/v1/imaging/delivery-packages");
    request.setParameter("patientId", "Patient-01");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Patient-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "PATIENT");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void patientCannotListAnotherPatientsImagingDeliveryPackages() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/v1/imaging/delivery-packages");
    request.setParameter("patientId", "Patient-02");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Patient-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "PATIENT");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  void patientMayReachImagingDeliveryPackageByIdPermissionCheck() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/v1/imaging/delivery-packages/pkg-1");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Patient-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "PATIENT");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void patientCannotMutateImagingDeliveryPackages() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("PUT", "/api/v1/imaging/delivery-packages/pkg-1/deliver");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Patient-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "PATIENT");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
  }

  @Test
  void referringDoctorMayReachImagingReportsPermissionCheck() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/v1/imaging/reports");
    request.setParameter("studyId", "std-1");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:Doctor-01");
    request.addHeader(HopAuthenticationResolver.ROLES, "REFERRING_DOCTOR");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void otherRolesRemainDeniedForDoctorPortalEndpoints() throws Exception {
    var properties = properties(false);
    var interceptor = interceptor(properties);
    var request = request("GET", "/api/results/history/patient/Patient-01");
    request.addHeader(
        HopAuthenticationResolver.AUTHORIZATION, "Bearer local-session:tenant-a:user-a");
    request.addHeader(HopAuthenticationResolver.ROLES, "FRONT_DESK");
    var response = new MockHttpServletResponse();

    boolean allowed = interceptor.preHandle(request, response, new Object());

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
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
