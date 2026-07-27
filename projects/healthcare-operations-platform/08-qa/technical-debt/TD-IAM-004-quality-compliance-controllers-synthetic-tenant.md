---
id: TD-IAM-004
format: markdown_structured_payload
type: technical-debt-item
name: External Quality/CAPA/Audit/Document-Management controllers assign a synthetic
  random TenantId instead of the authenticated request tenant
version: 2.0.0
status: closed
---

# External Quality/Capa/Audit/Document Management Controllers Assign A Synthetic Random Tenantid Instead Of The Authenticated Request Tenant

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-IAM-004
  type: technical-debt-item
  name: External Quality/CAPA/Audit/Document-Management controllers assign a synthetic
    random TenantId instead of the authenticated request tenant
  version: 2.0.0
  status: closed
  created_date: 2026-07-23
  closed_date: 2026-07-26
  closed_during_backlog_item: HOP-HARD-IAM-001
source:
  discovered_during_backlog_item: COM-MOD-013-QA-001
  module: COM-MOD-013 Advanced Quality and Compliance
  evidence: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-QA-001-validation.md
classification:
  category: iam_tenant_context_gap
  affected_area: quality_compliance_and_document_management_tenant_traceability
  affected_components:
  - externalqualitycompliance.adapter.in.web.ExternalQualityController
  - externalqualitycompliance.adapter.in.web.CapaManagementController
  - externalqualitycompliance.adapter.in.web.AuditManagementController
  - externalqualitycompliance.adapter.in.web.QualityEventIntakeController
  - documentmanagement.adapter.in.web.DocumentManagementController
  risk_level: medium
  urgency: medium
  blocking: false
  reason_non_blocking: 'HOP-HARD-IAM-001 closed this debt. All 5 endpoint prefixes
    (/api/quality/external-controls, /api/quality/capa, /api/quality/audits,
    /api/quality/events, /api/documents) now resolve the real authenticated tenant;
    residual risk is limited to depth of adoption, not an open gap.'
current_state:
  issue: 'CLOSED by HOP-HARD-IAM-001. A new sharedkernel.security.CurrentTenantContext
    ThreadLocal holder (OPEN Spring Modulith module, so no allowedDependencies change
    was needed for externalqualitycompliance or documentmanagement, both of which
    already declared sharedkernel as an allowed dependency) is populated by
    HopAuthorizationInterceptor.preHandle alongside the existing
    AuthenticatedUserContextHolder, and cleared in afterCompletion. All 5 controllers
    (ExternalQualityController, CapaManagementController, AuditManagementController,
    QualityEventIntakeController, DocumentManagementController) now resolve
    TenantId via a private currentTenantId() helper that reads
    CurrentTenantContext.current().map(TenantId::new), falling back to the previous
    new TenantId(UUID.randomUUID()...) behavior only when no authenticated context
    exists (e.g. standalone MockMvc unit tests that bypass the interceptor), exactly
    matching AuthController''s own .orElse(...) fallback pattern.'
  root_cause: Resolved. sharedkernel.security.CurrentTenantContext is the narrow
    named-interface tenant-context port previously identified as missing; it lives
    in the OPEN sharedkernel module so no module-boundary widening was required.
target_state:
  preferred_remediation: Closed. Future controllers that create tenant-scoped records
    should resolve TenantId from CurrentTenantContext the same way, rather than
    reintroducing a random-UUID placeholder.
  quality_goal: Compliance and document-management records created through authenticated
    requests carry the real requesting tenant, not a fabricated placeholder, so
    evidence search/export/retention workflows are genuinely traceable by tenant.
remediation:
  strategy: closed_with_documented_residual_risk
  owner: backend_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: HOP-HARD-IAM-001
  residual_risk:
  - The random-UUID fallback still exists for requests with no authenticated
    context (e.g. unit tests constructing controllers directly). This is intentional
    (matches AuthController's own fallback pattern and keeps existing unit tests
    passing unchanged) but means a future caller that reaches these controllers
    outside the HopAuthorizationInterceptor chain would still get a fabricated
    tenant; production traffic always passes through the interceptor because all
    5 paths are registered in EndpointPermissionRegistry.
  acceptance_criteria:
  - The 5 listed controllers resolve TenantId from the authenticated request context
    when one is present. [met]
  - Existing standalone MockMvc controller tests continue to pass unchanged (fallback
    preserved). [met]
  - PlatformFoundationModulithTest remains green after the module-boundary change. [met, no module-boundary change was needed]
  progress_evidence:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/sharedkernel/security/CurrentTenantContext.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptor.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptorTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/externalqualitycompliance/ExternalQualityComplianceControllerTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/externalqualitycompliance/DocumentManagementControllerTest.java
```
