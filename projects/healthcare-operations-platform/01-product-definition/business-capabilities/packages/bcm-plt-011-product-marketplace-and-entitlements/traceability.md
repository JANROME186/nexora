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
  - /api/marketplace/tenants/{tenantId}/entitlements
  - /api/marketplace/tenants/{tenantId}/installations
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
backlog_items:
  definition: COM-MOD-017-DEF
  definition_status: closed
  compilation: COM-MOD-017-BE-001
  compilation_status: closed
  custom_rules: COM-MOD-017-BE-002
  custom_rules_status: closed
  ui: COM-MOD-017-FE-001
  ui_status: pending
  validation: COM-MOD-017-QA-001
  validation_status: pending
  closeout: COM-MOD-017-CLOSEOUT
```
