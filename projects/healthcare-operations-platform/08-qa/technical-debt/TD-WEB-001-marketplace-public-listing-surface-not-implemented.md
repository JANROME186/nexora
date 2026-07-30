---
id: TD-WEB-001
format: markdown_structured_payload
type: technical-debt-item
name: Public marketplace listing surface (PUBLIC_MARKETPLACE_LISTING) modeled by
  BCM-PLT-011 but never compiled
version: 1.1.0
status: closed
---

# Public Marketplace Listing Surface (PUBLIC_MARKETPLACE_LISTING) Modeled By BCM-PLT-011 But Never Compiled

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-WEB-001
  type: technical-debt-item
  name: Public marketplace listing surface (PUBLIC_MARKETPLACE_LISTING) modeled
    by BCM-PLT-011 but never compiled
  version: 1.1.0
  status: closed
  created_date: 2026-07-25
  closed_date: 2026-07-30
  closed_by: HOP-HARD-WEB-001
source:
  discovered_during_backlog_item: COM-MOD-017-CLOSEOUT
  module: COM-MOD-017 Product Marketplace and Extension Packaging
  evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-CLOSEOUT-validation.md
classification:
  category: deferred_product_surface_not_yet_scheduled
  affected_area: bcm_plt_011_public_website_marketplace_listing
  affected_components:
  - 01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/ui-model.md
  - 01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/capability-package.md
  - 01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/generation-plan.md
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: 'BCM-PLT-011''s own backend, IAM, employee-portal administration
    and entitlement-gated installation lifecycle surfaces are fully compiled, tested
    and validated (COM-MOD-017-BE-001/BE-002/FE-001/QA-001). Only the anonymous
    public_website discovery surface (PUBLIC_MARKETPLACE_LISTING, ui-model.md public_website.surfaces)
    remains uncompiled. This surface is outward marketing/discovery only -- it does
    not gate purchase, entitlement, installation or any commercial transaction,
    all of which already work end to end through the employee-portal administration
    screens. COM-MOD-017-QA-001 already identified COM-MOD-017-WEB-001 as "a separate,
    not-yet-scheduled backlog item" without registering formal debt for it; this
    entry closes that gap in the technical-debt registry.

    '
current_state:
  issue: 'capability-package.md declares `product_surfaces.public_website: marketplace_listing_required`
    and ui-model.md models a `PUBLIC_MARKETPLACE_LISTING` surface under `public_website.surfaces`,
    and generation-plan.md''s `generated_outputs.public_website` names "Public marketplace
    listing and package discovery surface" as a generated output. `COM-MOD-017-WEB-001`
    exists as a named backlog item in HOP_COMMERCIAL_PRODUCT_BACKLOG.md ("Compile
    public marketplace listing and package discovery surfaces") but carries no `status`
    field and has never been scheduled, implemented or validated. COM-MOD-017-CLOSEOUT
    closes the COM-MOD-017 module (BCM-PLT-011 marked `module_closed`) with this
    one modeled-but-uncompiled surface explicitly carried forward as tracked debt
    rather than silently dropped.

    '
target_state:
  fix: 'Schedule `COM-MOD-017-WEB-001` (or an equivalent public-website backlog
    item) to add a public marketplace package/offer discovery page to `07-implementation/public-website/`
    (the existing COM-MOD-011-WEB-001 pattern: anonymous GET-only endpoints, SEO
    metadata, i18n, accessibility), backed by a new anonymous, read-only backend
    endpoint under `/api/public/marketplace/**` mirroring the anonymous catalog-discovery
    pattern already used by BCM-SVC-001/002/003/005 (`CatalogPublicReadPort`).

    '
remediation:
  strategy: gradual_dedicated_backlog_item_com_mod_017_web_001
  owner: product_architecture_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: COM-MOD-017-WEB-001
  acceptance_criteria:
  - A public, anonymous, read-only marketplace package/offer discovery surface
    exists in 07-implementation/public-website/ consuming a new anonymous
    /api/public/marketplace/** backend endpoint.
  - No tenantId, entitlement, billing or installation-lifecycle data is exposed
    on the anonymous surface.
  - ui-model.md's PUBLIC_MARKETPLACE_LISTING surface and generation-plan.md's
    public_website generated_outputs entry are realized and traced in traceability.md.
```
