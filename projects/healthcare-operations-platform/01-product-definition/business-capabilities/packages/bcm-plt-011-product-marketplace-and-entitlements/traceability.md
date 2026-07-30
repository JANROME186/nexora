---
id: HOP-TRC-BCM-PLT-011
format: markdown_structured_payload
type: traceability
name: Product Marketplace and Entitlements Traceability Matrix
version: 1.0.0
status: modeled
---

# Product Marketplace And Entitlements Traceability Matrix

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRC-BCM-PLT-011
  type: traceability
  name: Product Marketplace and Entitlements Traceability Matrix
  version: 1.0.0
  status: modeled
capability_id: BCM-PLT-011
roadmap_group: COM-MOD-017
mappings:
- requirement: Product Marketplace and Extension Packaging
  rules:
  - RN-MKT-001
  - RN-MKT-002
  - RN-MKT-003
  - RN-MKT-004
  - RN-MKT-005
  - RN-MKT-006
  processes:
  - PROC-MKT-001
  - PROC-MKT-002
  - PROC-MKT-003
  - PROC-MKT-004
  - PROC-MKT-005
  - PROC-MKT-006
  events:
  - MarketplacePackagePublishedEvent
  - TenantEntitlementGrantedEvent
  - PackageInstalledEvent
  - PackageActivatedEvent
  - PackageRetiredEvent
  api_endpoints:
  - /api/marketplace/packages
  - /api/marketplace/offers
  - /api/marketplace/entitlements/{tenantId}
  - /api/marketplace/installations/{tenantId}
  permissions:
  - marketplace.package:publish
  - marketplace.offer:accept
  - marketplace.entitlement:grant
  - marketplace.installation:activate
  tests:
  - TEST-MKT-001
  - TEST-MKT-002
  - TEST-MKT-003
  - TEST-MKT-004
  - TEST-MKT-005
  - TEST-MKT-006
  - TEST-MKT-007
definition:
  backlog_item: COM-MOD-017-DEF
  status: modeled
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-DEF-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-DEF/security-quality-evidence.md
compilation:
  backlog_item: COM-MOD-017-BE-001
  status: closed
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.md
  notes: All 21 openapi-source.md operations compiled as generatable outputs across
    a new marketplaceentitlements Spring Modulith module (packagecatalog, commercialoffers,
    tenantentitlements, packageinstallation, compatibilityevaluation, billingadapter).
    Each generation-plan.md custom_implementation_point (entitlement policy evaluator,
    compatibility evaluation strategy, billing provider adapter boundary, installation
    rollback orchestration) implemented at a basic, correct level sufficient for every
    endpoint to function; deeper sophistication registered as TD-BE-018 targeting
    a future COM-MOD-017-BE-002. Runtime feature-availability integration with IAM
    and menu generation not yet started (also TD-BE-018).
custom_rules:
  backlog_item: COM-MOD-017-BE-002
  status: closed
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-BE-002/security-quality-evidence.md
  notes: Closed 4 of TD-BE-018's 5 custom_implementation_points -- EntitlementPolicyEvaluator
    now runs the full entitlement-policy.md evaluation_order (via a policy-decision-point
    design keeping the Spring Modulith graph acyclic), CompatibilityEvaluator evaluates
    all 9 compatibility.md dimensions, the billing adapter gained retry/idempotency
    (INV-MKT-003 preserved), and installation rollback derives its target version
    from a persisted multi-step InstallationStep audit trail. TD-BE-018 updated to
    materially_reduced. The 5th point (runtime feature-availability into IAM/employee-portal
    menu) is repointed to new TD-BE-019, targeted at COM-MOD-017-FE-001 (needs real
    employee-portal marketplace screens that do not exist yet).
ui:
  backlog_item: COM-MOD-017-FE-001
  status: closed
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-FE-001-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-FE-001/security-quality-evidence.md
  notes: Compiled all 4 employee_portal.screens from ui-model.md as real screens --
    MarketplacePackagesScreen, MarketplaceOffersScreen, MarketplaceEntitlementsScreen,
    MarketplaceInstallationsScreen -- with a typed marketplaceApi.ts facade over the
    4 marketplace controllers and IAM/menu wiring (permissions.ts/AppShell.tsx/App.tsx,
    MARKETPLACE_OPERATOR/TENANT_ADMIN roles mirroring RolePermissionCatalog.java exactly).
    Closed TD-BE-019 for real -- MarketplaceInstallationsScreen's install control is
    genuinely gated on real tenant entitlement runtime state loaded from TenantEntitlementController
    (mirroring the backend's TenantEntitlement.isEffectivelyActive), not a fabricated
    cross-capability relationship.
validation:
  backlog_item: COM-MOD-017-QA-001
  status: closed
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-QA-001-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-QA-001/security-quality-evidence.md
  notes: Ran 4 traceability sweeps (openapi-source.md vs. the 6 marketplace controllers,
    IAM permissions across PermissionCode.java/RolePermissionCatalog.java/EndpointPermissionRegistry.java/permissions.ts,
    ui-model.md vs. the 4 employee-portal screens, es-MX/en-US i18n key parity).
    Found and corrected 3 real doc-vs-implementation drifts -- openapi-source.md documented
    10 operations under a /tenants/{tenantId}/... path never actually used by the
    shipped, tested routes plus 1 undocumented getPackage endpoint; permissions.md/ui-model.md
    documented an unimplemented 15-code fine-grained permission model while the shipped
    system correctly uses the platform's coarse 4-code SCREEN_MARKETPLACE_* model
    (TD-IAM-002 pattern). i18n key parity was clean. Debt-first action -- closed TD-BE-018
    (all 5 of 5 named custom_implementation_points now closed via the TD-BE-019 chain
    closed by COM-MOD-017-FE-001).
closeout:
  backlog_item: COM-MOD-017-CLOSEOUT
  status: closed
  qa_evidence: ../../../../08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-CLOSEOUT-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-017-CLOSEOUT/security-quality-evidence.md
  notes: Formally closed COM-MOD-017 Product Marketplace and Extension Packaging.
    Marked BCM-PLT-011 module_closed in capability-package.md and capability-package-index.md
    (moved from active_capability_package_groups to completed_capability_package_groups).
    Confirmed zero open technical debt attributable to BCM-PLT-011's own compiled
    scope -- TD-BE-018, TD-BE-019 and TD-BE-020 are all closed; TD-FE-012 remains
    open non-blocking (devDependency-only, no non-breaking fix available). Documentation
    and registry-only closeout; no application source changed, so backend (84.65%)
    and employee-portal (90.68%) coverage are re-affirmed unchanged from COM-MOD-017-QA-001.
    Found and registered new debt TD-WEB-001 -- ui-model.md's PUBLIC_MARKETPLACE_LISTING
    public_website surface and generation-plan.md's matching generated_outputs entry
    were modeled but never compiled; COM-MOD-017-WEB-001 remains deferred and unscheduled,
    tracked as non-blocking debt rather than silently dropped, since it is an outward
    discovery surface only and does not gate any purchase, entitlement or installation
    workflow (all of which are fully compiled, tested and validated).
backlog_items:
  definition: COM-MOD-017-DEF
  definition_status: closed
  compilation: COM-MOD-017-BE-001
  compilation_status: closed
  custom_rules: COM-MOD-017-BE-002
  custom_rules_status: closed
  ui: COM-MOD-017-FE-001
  ui_status: closed
  validation: COM-MOD-017-QA-001
  validation_status: closed
  closeout: COM-MOD-017-CLOSEOUT
  closeout_status: closed
  web_hardening: HOP-HARD-WEB-001
  web_hardening_status: closed
```
