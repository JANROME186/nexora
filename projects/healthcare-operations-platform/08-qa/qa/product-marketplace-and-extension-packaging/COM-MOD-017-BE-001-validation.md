# COM-MOD-017-BE-001 QA Validation Evidence

- **Backlog Item**: COM-MOD-017-BE-001 — Compile marketplace catalog, offer, entitlement and installation backend outputs
- **Module**: COM-MOD-017 Product Marketplace and Extension Packaging
- **Date**: 2026-07-24
- **Status**: Validated

## Summary of Accomplishments

1. **New `marketplaceentitlements` Spring Modulith module** hosting six sibling capability sub-packages, mirroring how `integrationinteroperability` hosts BCM-PLT-004/BCM-PLT-005.

2. **Package Catalog (AGG-030 MarketplacePackage/PackageVersion)**:
   - `POST/GET /api/marketplace/packages`, `POST /api/marketplace/packages/{id}/publish`, `GET/POST /api/marketplace/packages/{id}/versions/{version}[/certify|/retire]`.
   - RN-MKT-001: submission requires at least one capability mapping. INV-MKT-001/RN-MKT-004: publication is blocked until a version is certified (all four of compatibility/security-review/support-model/telemetry-model approved).

3. **Commercial Offers (AGG-031 CommercialOffer)**:
   - `POST/GET /api/marketplace/offers`, `POST /api/marketplace/offers/{id}/accept`.
   - OFFER-001: an offer can only be published against an already-published package version. OFFER-002: acceptance runs a compatibility evaluation and rejects on failure. Acceptance directly grants a `TenantEntitlement`.

4. **Tenant Entitlements (AGG-032 TenantEntitlement)**:
   - `GET/POST /api/marketplace/entitlements/{tenantId}`, `POST /api/marketplace/entitlements/{tenantId}/{id}/revoke`.
   - A new centralized `EntitlementPolicyEvaluator` (RN-MKT-005) is the single gate every other capability calls to check entitlement state — never hard-coded in a controller.

5. **Package Installation (AGG-033 PackageInstallation)**:
   - `GET/POST /api/marketplace/installations/{tenantId}`, `.../{id}/activate|suspend|uninstall|upgrade`, `.../{id}/upgrade/rollback`.
   - RN-MKT-002: activation is gated by an active entitlement. INV-MKT-004: activation and upgrade preserve a rollback checkpoint before applying a version change; rollback fails cleanly with `ROLLBACK_NOT_AVAILABLE` when no checkpoint exists. Uninstall soft-disables and preserves the record (package-manifest.yaml `uninstall_policy`).

6. **Compatibility Evaluation**: `POST /api/marketplace/compatibility/evaluate` — stateless `platform_version` major/minor comparator producing `compatible`/`compatible_with_warning`/`incompatible`/`unknown` decisions matching compatibility.yaml's four effects.

7. **Billing Adapter Boundary (INV-MKT-003, OFFER-004)**: `POST /api/marketplace/billing/events` — a provider-agnostic `BillingAdapterPort` with a `LocalDeterministicBillingAdapter` implementation, mirroring `IntegrationAdapterPort`/`FiscalAdapterPort`. Billing events are persisted as an observability-only record and never become the source of truth for entitlement, license or clinical state.

8. **IAM Permissions & i18n**: Four new `PermissionCode` values (`SCREEN_MARKETPLACE_PACKAGES/OFFERS/ENTITLEMENTS/INSTALLATIONS`), registered in `EndpointPermissionRegistry` and a new `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` role pair in `RolePermissionCatalog`. 16 `marketplace.error.*` i18n keys added to the default, es-MX and en-US catalogs.

9. **Persistence & Verification**: New `db/product-marketplace-and-entitlements/schema.sql` (6 tables), registered in `application-local.yml`. 60 new tests added (service-level unit tests per capability, a full-lifecycle API test, a real-Postgres local-database test) — all pass. Backend coverage raised from the 84.25% floor to a reproducible **84.53%** (442 tests, 0 failures/errors/skipped, Docker Compose PostgreSQL 16 up).

10. **Technical debt**: registered **TD-BE-018** for the deeper entitlement-policy/compatibility/billing-adapter/rollback sophistication generation-plan.yaml names as `custom_implementation_points`, targeting a future COM-MOD-017-BE-002 — matching the BE-001/BE-002 maturation pattern already used for MVP-MOD-005's fiscal adapter and MVP-MOD-008's integration adapter. Fixed 2 low-severity SpotBugs `IMPROPER_UNICODE` findings introduced by this item's own code before closure.
