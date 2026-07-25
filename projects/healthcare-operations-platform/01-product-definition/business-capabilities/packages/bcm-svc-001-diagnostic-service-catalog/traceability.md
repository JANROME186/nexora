---
id: HOP-TRACE-BCM-SVC-001
format: markdown_structured_payload
type: traceability
name: Diagnostic Service Catalog Traceability
version: 0.2.0
status: modeled
---

# Diagnostic Service Catalog Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-SVC-001
  type: traceability
  name: Diagnostic Service Catalog Traceability
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
traces:
  capability_map:
    bcm_001: BCM-SVC-001
    domain: DOM-03 Diagnostic Services
  dependency_map:
    bcm_002_profile: catalog
    required_capabilities:
    - BCM-ORG-001
    - BCM-ORG-002
    - BCM-ORG-003
    - BCM-PLT-001
    - BCM-PLT-007
    downstream_capabilities:
    - BCM-SVC-002
    - BCM-SVC-003
    - BCM-ATT-006
  domain_foundation:
    bounded_context: catalog-test-configuration
    aggregate: AGG-006 TestDefinition
    context_relationships:
    - REL-CTX-003
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-ID-003
    - VO-007
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-SVC-001-01
  - rule: RN-002
    tests:
    - TST-SVC-001-02
    - TST-SVC-001-03
  - rule: RN-003
    tests:
    - TST-SVC-001-04
  - rule: RN-004
    tests:
    - TST-SVC-001-05
  - rule: RN-005
    tests:
    - TST-SVC-001-06
  - rule: RN-006
    tests:
    - TST-SVC-001-07
  processes_to_commands:
  - process: PRC-SVC-001-01
    commands:
    - CreateDiagnosticService
  - process: PRC-SVC-001-02
    commands:
    - PublishDiagnosticService
  - process: PRC-SVC-001-03
    commands:
    - DeprecateDiagnosticService
  api_to_permissions:
  - operation: createDiagnosticService
    scope: catalog.service.write
  - operation: publishDiagnosticService
    scope: catalog.service.publish
  events_to_audit:
  - event: DiagnosticServicePublished
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-SVC-001-02
    operations:
    - createDiagnosticService
    - updateDiagnosticService
  generated_outputs_ref: generation-plan.md
  qa_evidence: ../../../../08-qa/qa/catalog-test-configuration/MVP-MOD-002-DEF-validation.md
  backlog_items:
    definition: MVP-MOD-002-DEF
    compilation: MVP-MOD-002-BE-001
    custom_rules: MVP-MOD-002-BE-002
    ui: MVP-MOD-002-FE-001
  cross_module_reuse:
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-DEF
    surface_added: public_website
    note: Public website discovery surface modeled by extending openapi-source.md,
      capability-package.md, ui-model.md and permissions.md. No new capability
      package, aggregate or schema was created; reuses TestDefinition/AGG-006, PublishedServiceSnapshot
      and getPublishedServiceSnapshot.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-BE-001
    surface_added: public_website (runtime discovery)
    note: Compiled GET /api/public/catalog/diagnostic-services/published and /api/public/catalog/diagnostic-services/{serviceId}/published-snapshot
      via the new catalogtestconfiguration::catalog-public-read-port named interface
      (CatalogPublicReadPort / CatalogPublicReadAdapter). DiagnosticServiceCatalogService.listPublished
      added. Anonymous endpoints filter to status=published, so drafts, deprecated
      and retired records are never returned. Public rate limiting is enforced by
      PublicApiRateLimitInterceptor (BCM-PLT-005 RN-007).
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-WEB-001
    surface_added: public_website (frontend discovery)
    note: Compiled ServicesPage (list) and ServiceDetailPage (snapshot) at 07-implementation/public-website/src/pages/,
      consuming GET /api/public/catalog/diagnostic-services/published and /api/public/catalog/diagnostic-services/{serviceId}/published-snapshot
      via src/api/publicCatalogApi.ts. es-MX/en-US localized name display, loading/empty/error/
      success states, SEO metadata and a real crawlable /services and /services/:id
      URL.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-FE-001
    surface_added: employee_portal (staff content review)
    note: Compiled PublicContentReviewScreen at 07-implementation/employee-portal/src/components/screens/,
      consuming the same GET /api/public/catalog/diagnostic-services/published endpoint
      the public website calls (via the new src/api/publicContentApi.ts), rather than
      the internal catalog-admin API, so staff see exactly what a visitor sees with
      no tenantId/audit field ever entering the view. Read-only; full draft/publish
      lifecycle stays owned by DiagnosticCatalogScreen.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-QA-001
    surface_added: integrated QA, SEO and privacy validation
    note: Validated public diagnostic service catalog surface end to end with zero
      vulnerabilities across Maven quality, OWASP Dependency-Check, npm audit, and
      Trivy fs scans. Verified no tenantId, audit or internal identifier leakage.
      SEO metadata, sitemap, robots, i18n key parity and axe-core accessibility checks
      passed.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-CLOSEOUT
    surface_added: module closeout and registry update
    note: Formally closed COM-MOD-011 Public Website and Digital Growth module. Confirmed
      reused public catalog surfaces, zero vulnerability findings, zero coverage regressions,
      and all quality gates passing clean.
```
