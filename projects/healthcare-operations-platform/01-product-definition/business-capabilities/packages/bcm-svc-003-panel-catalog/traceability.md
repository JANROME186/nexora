---
id: HOP-TRACE-BCM-SVC-003
format: markdown_structured_payload
type: traceability
name: Panel Catalog Traceability
version: 0.2.0
status: modeled
---

# Panel Catalog Traceability

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRACE-BCM-SVC-003
  type: traceability
  name: Panel Catalog Traceability
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-003
traces:
  capability_map:
    bcm_001: BCM-SVC-003
    domain: DOM-03 Diagnostic Services
  dependency_map:
    bcm_002_profile: catalog
    required_capabilities:
    - BCM-ORG-001
    - BCM-ORG-002
    - BCM-PLT-001
    - BCM-PLT-007
    - BCM-SVC-002
    downstream_capabilities:
    - BCM-LAB-001
    - BCM-ATT-006
  domain_foundation:
    bounded_context: catalog-test-configuration
    aggregate: AGG-006 TestDefinition
    context_relationships:
    - REL-CTX-003
    shared_kernel_refs:
    - VO-ID-001
    - VO-ID-002
    - VO-007
  rules_to_tests:
  - rule: RN-001
    tests:
    - TST-SVC-003-01
  - rule: RN-002
    tests:
    - TST-SVC-003-02
  - rule: RN-003
    tests:
    - TST-SVC-003-03
  - rule: RN-004
    tests:
    - TST-SVC-003-04
  - rule: RN-005
    tests:
    - TST-SVC-003-05
  processes_to_commands:
  - process: PRC-SVC-003-01
    commands:
    - CreatePanelDefinition
  - process: PRC-SVC-003-02
    commands:
    - PublishPanelDefinition
  - process: PRC-SVC-003-03
    commands:
    - DeprecatePanelDefinition
  api_to_permissions:
  - operation: createPanel
    scope: catalog.panel.write
  - operation: publishPanel
    scope: catalog.panel.publish
  events_to_audit:
  - event: PanelDefinitionPublished
    audit_sink: BCM-PLT-007
  ui_to_api:
  - screen: SCR-SVC-003-02
    operations:
    - createPanel
    - updatePanel
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
      package, aggregate or schema was created; reuses TestDefinition/AGG-006, PublishedPanelSnapshot
      and getPublishedPanelSnapshot.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-BE-001
    surface_added: public_website (runtime discovery)
    note: Compiled GET /api/public/catalog/panels/published and /api/public/catalog/panels/{panelId}/published-snapshot
      via the new catalogtestconfiguration::catalog-public-read-port named interface.
      PanelCatalogService.listPublished added; endpoints only return status=published
      records.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-WEB-001
    surface_added: public_website (frontend discovery)
    note: Compiled PanelsPage (list) and PanelDetailPage (snapshot) at 07-implementation/public-website/src/pages/,
      consuming GET /api/public/catalog/panels/published and /api/public/catalog/panels/{panelId}/published-snapshot.
      Panels are also combined with tests into a single picker (useCatalogItemOptions)
      for the appointment/quotation forms.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-FE-001
    surface_added: employee_portal (staff content review)
    note: Compiled PublicContentReviewScreen at 07-implementation/employee-portal/src/components/screens/,
      consuming GET /api/public/catalog/panels/published (src/api/publicContentApi.ts)
      — the same public endpoint, not the internal catalog-admin API — so no internal
      field can leak into the staff review view. Read-only; publish lifecycle stays
      owned by DiagnosticCatalogScreen.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-QA-001
    surface_added: integrated QA, SEO and privacy validation
    note: Validated public panel catalog surface end to end with zero vulnerabilities
      across Maven quality, OWASP Dependency-Check, npm audit, and Trivy fs scans.
      Verified no tenantId, audit or internal identifier leakage. SEO metadata, sitemap,
      robots, i18n key parity and axe-core accessibility checks passed.
  - roadmap_group: COM-MOD-011
    backlog_item: COM-MOD-011-CLOSEOUT
    surface_added: module closeout and registry update
    note: Formally closed COM-MOD-011 Public Website and Digital Growth module. Confirmed
      reused public panel catalog surfaces, zero vulnerability findings, zero coverage
      regressions, and all quality gates passing clean.
```
