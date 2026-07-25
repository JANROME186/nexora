---
id: HOP-BIZ-BCM-PLT-011
format: markdown_structured_payload
type: business-model
name: Product Marketplace and Entitlements Business Model
version: 1.0.0
status: modeled
backlog_item: COM-MOD-017-DEF
---

# Product Marketplace And Entitlements Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BIZ-BCM-PLT-011
  type: business-model
  name: Product Marketplace and Entitlements Business Model
  version: 1.0.0
  status: modeled
  backlog_item: COM-MOD-017-DEF
bounded_context: marketplace-entitlements
aggregates:
- id: AGG-030
  name: MarketplacePackage
  root_entity: MarketplacePackage
  purpose: Published package metadata, version catalog and lifecycle state.
  entities:
  - MarketplacePackage
  - PackageVersion
  - ExtensionPointBinding
  - PackageDependency
- id: AGG-031
  name: CommercialOffer
  root_entity: CommercialOffer
  purpose: Customer-facing offer, bundle, trial, pricing reference and purchasing
    terms.
  entities:
  - CommercialOffer
  - OfferTier
  - TrialPolicy
  - BillingEventRule
- id: AGG-032
  name: TenantEntitlement
  root_entity: TenantEntitlement
  purpose: Tenant package access grants, limits, validity and revocation state.
  entities:
  - TenantEntitlement
  - EntitlementGrant
  - EntitlementLimit
  - EntitlementConsumptionCounter
- id: AGG-033
  name: PackageInstallation
  root_entity: PackageInstallation
  purpose: Tenant installation lifecycle, activation, rollback and uninstall evidence.
  entities:
  - PackageInstallation
  - InstallationStep
  - ActivationState
  - RollbackCheckpoint
relationships:
- from: MarketplacePackage
  to: CommercialOffer
  cardinality: one_to_many
- from: CommercialOffer
  to: TenantEntitlement
  cardinality: one_to_many
- from: TenantEntitlement
  to: PackageInstallation
  cardinality: one_to_many
- from: PackageInstallation
  to: ExtensionPointBinding
  cardinality: many_to_many
invariants:
- id: INV-MKT-001
  statement: A package version cannot be published unless compatibility, security
    review, support model and telemetry model are approved.
- id: INV-MKT-002
  statement: A tenant cannot activate a package without an active entitlement and
    passing IAM authorization.
- id: INV-MKT-003
  statement: Billing events are integration outputs and cannot become the source of
    truth for clinical, operational or financial domain state.
- id: INV-MKT-004
  statement: A package installation must preserve rollback evidence before activation
    when rollback is supported.
```
