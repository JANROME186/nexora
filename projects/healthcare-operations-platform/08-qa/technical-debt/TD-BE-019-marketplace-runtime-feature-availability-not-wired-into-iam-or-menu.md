---
id: TD-BE-019
format: markdown_structured_payload
type: technical-debt-item
name: Marketplace runtime feature-availability is not wired into IAM permission evaluation
  or employee-portal menu generation
version: 1.0.0
status: closed
---

# Marketplace Runtime Feature-Availability Is Not Wired Into IAM Permission Evaluation Or Employee-Portal Menu Generation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-019
  type: technical-debt-item
  name: Marketplace runtime feature-availability is not wired into IAM permission
    evaluation or employee-portal menu generation
  version: 1.0.0
  status: closed
  created_date: 2026-07-24
source:
  discovered_during_backlog_item: COM-MOD-017-BE-002
  module: COM-MOD-017 Product Marketplace and Extension Packaging
  evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md
classification:
  category: backend_and_frontend_custom_rule_deferred
  affected_area: marketplace_entitlements_runtime_feature_availability
  affected_components:
  - identityaccess.security.HopAuthorizationInterceptor
  - employee-portal/src/state/permissions.ts
  - employee-portal/src/App.tsx
  - employee-portal/src/components/layout/AppShell.tsx
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: The 4 marketplace backend REST operation groups (packages,
    offers, entitlements, installations) already enforce IAM permission checks (SCREEN_MARKETPLACE_*
    PermissionCodes registered in EndpointPermissionRegistry since COM-MOD-017-BE-001)
    and correct entitlement-state business rules (EntitlementPolicyEvaluator, since
    COM-MOD-017-BE-002). What is missing is a *second*, additional layer -- entitlement/installation
    runtime state gating an IAM decision or employee-portal navigation for some other
    capability -- which no part of the system depends on today.
current_state:
  issue: 'TD-BE-018''s fifth custom_implementation_point (runtime feature-availability
    integration with IAM and menu generation) was investigated during COM-MOD-017-BE-002
    and found to require more than a backend change: employee-portal''s App.tsx binds
    every ScreenKey to a React screen component 1:1 via SCREEN_COMPONENTS (a `satisfies
    Record<ScreenKey, () => JSX.Element>` map), so adding the 4 marketplace ScreenKeys
    to permissions.ts/AppShell.tsx without also building 4 real marketplace administration
    screens breaks the frontend build. Building those screens is explicitly the separate
    COM-MOD-017-FE-001 "employee_portal: Marketplace administration screens..." output
    named by generation-plan.md, not a backend custom_implementation_point. Separately,
    a genuine backend-only integration (an identityaccess-side runtime check of marketplace
    entitlement/installation state) was evaluated and set aside: HopAuthorizationInterceptor
    has no existing, real, non-fabricated relationship between any non-marketplace
    IAM permission/screen and a specific marketplace package to gate on -- inventing
    one would misrepresent product state that does not exist in this codebase.'
target_state:
  preferred_remediation: When COM-MOD-017-FE-001 (or an equivalent employee-portal
    marketplace screens item) is scheduled, add the 4 marketplace ScreenKeys/PermissionCodes/SCREEN_TO_PERMISSION
    entries and MARKETPLACE_OPERATOR/TENANT_ADMIN RoleCode entries to permissions.ts,
    wire them into AppShell.tsx/App.tsx alongside the new screen components, and
    reassess whether a real (not fabricated) cross-capability entitlement-gates-IAM
    relationship exists once actual purchasable/optional capabilities are modeled
    against BCM-PLT-011 packages.
  quality_goal: Close this item only when a real screen or a real cross-capability
    relationship exists to wire -- do not force a fabricated one to close this item
    early.
remediation:
  strategy: gradual_when_COM_MOD_017_FE_001_or_a_real_optional_capability_relationship_is_next_scheduled
  owner: frontend_and_backend_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: COM-MOD-017-FE-001
  dependencies_or_prerequisites:
  - COM-MOD-017-BE-002 entitlement/installation runtime state (closed).
  acceptance_criteria:
  - Marketplace ScreenKeys/PermissionCodes are wired into employee-portal navigation
    once real screens exist.
  - At least one IAM permission or employee-portal menu decision is genuinely gated
    by marketplace entitlement/installation runtime state.
closure:
  status: closed
  closed_by_backlog_item: COM-MOD-017-FE-001
  closed_date: 2026-07-25
  mechanism: 'Both acceptance criteria closed for real. (1) `MarketplacePackagesScreen`,
    `MarketplaceOffersScreen`, `MarketplaceEntitlementsScreen` and `MarketplaceInstallationsScreen`
    were built and wired into `employee-portal/src/state/permissions.ts` (4 new
    `ScreenKey`/`PermissionCode`/`SCREEN_TO_PERMISSION` entries plus `MARKETPLACE_OPERATOR`/`TENANT_ADMIN`
    `RoleCode` entries mirroring the backend `RolePermissionCatalog.java` exactly),
    `AppShell.tsx` (`SCREEN_TAB_LABEL_KEYS`) and `App.tsx` (`SCREEN_COMPONENTS`),
    using the same permission-filtered dynamic navigation mechanism every other
    employee-portal screen already uses -- unauthorized roles never see the tabs.
    (2) `MarketplaceInstallationsScreen.tsx`''s "Install package" control is genuinely
    gated on real marketplace entitlement runtime state: before enabling install
    for a typed `packageId`, the screen loads the tenant''s real entitlements via
    `marketplaceApi.listTenantEntitlements` (a real backend call to `TenantEntitlementController`)
    and the `isPackageEntitled` helper only allows install when an entitlement for
    that exact `packageId` is effectively active (status `active` and, if set,
    `expiresAt` in the future -- mirroring the backend''s own `TenantEntitlement.isEffectivelyActive`).
    Otherwise the control is disabled and a localized explanatory status is shown.
    This is a real UI decision gated on real entitlement/installation runtime state
    from the real backend endpoint, not a fabricated cross-capability relationship
    to an unrelated screen -- exactly the boundary this item''s `quality_goal` required.'
  verification: 'MarketplaceInstallationsScreen.test.tsx and a dedicated isPackageEntitled
    unit-test block assert both directions: install allowed when an active,
    non-expired entitlement exists for the typed packageId, and disabled with the
    localized notEntitled status shown otherwise (revoked, expired, or no matching
    entitlement).'
```
