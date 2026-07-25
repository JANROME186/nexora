# BCM-PLT-011 — Product Marketplace and Entitlements Requirements

**Spanish:** Marketplace de Producto y Derechos de Uso
**Domain:** DOM-10 — Platform
**Priority:** High
**Roadmap:** MVP2

## Actors

- System Administrator
- Marketplace Operator
- Customer Buyer
- Tenant Administrator
- Support Analyst

## Portals

- employee_portal
- public_website

## Related Aggregates

- MarketplacePackage
- CommercialOffer
- LicensePlan
- TenantEntitlement
- PackageInstallation

## Functional Requirements

### FR-PLT-011-001

The platform shall maintain a marketplace catalog of versioned product packages with capability, compatibility, pricing, support and security metadata.

**Acceptance Criteria**

- Given a package is published, when a user views the catalog, then package metadata, version, compatibility, support owner and package type are visible to authorized users.
- Given a package is not certified, when publication is requested, then the platform blocks publication and records the reason.
- Given a package version is superseded, when the catalog is queried, then compatibility and upgrade guidance remain available.

### FR-PLT-011-002

The platform shall model commercial offers, trials, bundles, quotas and billing events through provider-agnostic marketplace boundaries.

**Acceptance Criteria**

- Given an offer is published, when a customer selects it, then purchase or grant flow is initiated without coupling to a specific billing provider.
- Given a trial or bundle has limits, when entitlement is resolved, then the limits are enforced consistently.
- Given a billing provider is unavailable, when a purchase event is pending, then the platform keeps product domain state isolated from provider state.

### FR-PLT-011-003

The platform shall grant and resolve tenant entitlements separately from IAM permissions and feature flags.

**Acceptance Criteria**

- Given a tenant has a valid entitlement, when an authorized administrator installs a package, then the entitlement allows installation.
- Given a tenant lacks entitlement, when package execution is requested, then the platform denies execution before domain mutation.
- Given a user lacks role permission, when entitlement exists, then the platform still denies access through IAM authorization.

### FR-PLT-011-004

The platform shall install, activate, suspend, upgrade, roll back, uninstall and retire marketplace packages per tenant with compatibility validation.

**Acceptance Criteria**

- Given a package has unmet dependencies, when installation is requested, then the platform blocks installation with actionable validation errors.
- Given a package is activated, when runtime consumption begins, then feature flags, entitlements, permissions and audit checks are applied.
- Given an upgrade fails, when rollback is allowed, then the previous compatible package version is restored or a migration-bound exception is recorded.

### FR-PLT-011-005

The platform shall provide audit, observability and support evidence for marketplace package lifecycle, entitlement and consumption events.

**Acceptance Criteria**

- Given a package lifecycle operation occurs, when the operation completes or fails, then an audit event is recorded.
- Given package usage is metered, when consumption occurs, then telemetry is emitted without exposing protected health or financial data unnecessarily.
- Given support reviews a tenant package issue, when evidence is requested, then installation, entitlement, compatibility and lifecycle history are available.

## User Stories

### US-PLT-011-001 — Discover marketplace packages

As a tenant administrator, I want to discover available packages so that I can evaluate optional product functionality for my organization.

### US-PLT-011-002 — Purchase or grant a package offer

As a customer buyer, I want to purchase or receive a commercial offer so that my tenant can be entitled to use a package.

### US-PLT-011-003 — Resolve entitlement before usage

As a platform administrator, I want package execution to require entitlement and authorization so that purchased functionality remains controlled.

### US-PLT-011-004 — Install and manage tenant packages

As a tenant administrator, I want to install, activate, upgrade and remove packages so that my tenant can evolve without custom product forks.

### US-PLT-011-005 — Support marketplace package issues

As a support analyst, I want lifecycle and usage evidence for packages so that customer issues can be diagnosed and resolved.

## Non-Functional Requirements

- Marketplace package models must remain agent-agnostic, cloud-agnostic and provider-agnostic.
- Entitlement checks must be centralized and observable.
- Package lifecycle operations must record audit metadata.
- Package installation and upgrade flows must validate compatibility before activation.
- Billing-provider, payment-provider and external marketplace details must remain adapter-based.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: REQ-BCM-PLT-011
  type: capability-requirements
  name: Product Marketplace and Entitlements Requirements
  version: 1.0.0
  status: approved
  owner: Product Requirements Team
  source_of_truth: 04-requirements/capabilities/bcm-plt-011-product-marketplace-and-entitlements/requirements.md
  depends_on:
  - BCM-PLT-011
  - BCM-001
  - NXF-MKT-STD-001
capability:
  domain_id: DOM-10
  domain_name: Platform
  id: BCM-PLT-011
  name_en: Product Marketplace and Entitlements
  name_es: Marketplace de Producto y Derechos de Uso
  priority: High
  roadmap: MVP2
actors:
- System Administrator
- Marketplace Operator
- Customer Buyer
- Tenant Administrator
- Support Analyst
portals:
- employee_portal
- public_website
mobile: entitlement_aware
related_aggregates:
- MarketplacePackage
- CommercialOffer
- LicensePlan
- TenantEntitlement
- PackageInstallation
primary_events:
- MarketplacePackagePublished
- CommercialOfferPublished
- PackagePurchased
- TenantEntitlementGranted
- PackageInstalled
- PackageActivated
- PackageSuspended
- PackageUpgraded
- PackageRolledBack
- PackageUninstalled
- PackageRetired
requirements:
- id: FR-PLT-011-001
  type: functional
  capability: BCM-PLT-011
  domain: DOM-10
  statement: The platform shall maintain a marketplace catalog of versioned product
    packages with capability, compatibility, pricing, support and security metadata.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Given a package is published, when a user views the catalog, then package metadata,
    version, compatibility, support owner and package type are visible to authorized
    users.
  - Given a package is not certified, when publication is requested, then the platform
    blocks publication and records the reason.
  - Given a package version is superseded, when the catalog is queried, then compatibility
    and upgrade guidance remain available.
- id: FR-PLT-011-002
  type: functional
  capability: BCM-PLT-011
  domain: DOM-10
  statement: The platform shall model commercial offers, trials, bundles, quotas and
    billing events through provider-agnostic marketplace boundaries.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Given an offer is published, when a customer selects it, then purchase or grant
    flow is initiated without coupling to a specific billing provider.
  - Given a trial or bundle has limits, when entitlement is resolved, then the limits
    are enforced consistently.
  - Given a billing provider is unavailable, when a purchase event is pending, then
    the platform keeps product domain state isolated from provider state.
- id: FR-PLT-011-003
  type: functional
  capability: BCM-PLT-011
  domain: DOM-10
  statement: The platform shall grant and resolve tenant entitlements separately from
    IAM permissions and feature flags.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Given a tenant has a valid entitlement, when an authorized administrator installs
    a package, then the entitlement allows installation.
  - Given a tenant lacks entitlement, when package execution is requested, then the
    platform denies execution before domain mutation.
  - Given a user lacks role permission, when entitlement exists, then the platform
    still denies access through IAM authorization.
- id: FR-PLT-011-004
  type: functional
  capability: BCM-PLT-011
  domain: DOM-10
  statement: The platform shall install, activate, suspend, upgrade, roll back, uninstall
    and retire marketplace packages per tenant with compatibility validation.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Given a package has unmet dependencies, when installation is requested, then the
    platform blocks installation with actionable validation errors.
  - Given a package is activated, when runtime consumption begins, then feature flags,
    entitlements, permissions and audit checks are applied.
  - Given an upgrade fails, when rollback is allowed, then the previous compatible
    package version is restored or a migration-bound exception is recorded.
- id: FR-PLT-011-005
  type: functional
  capability: BCM-PLT-011
  domain: DOM-10
  statement: The platform shall provide audit, observability and support evidence
    for marketplace package lifecycle, entitlement and consumption events.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Given a package lifecycle operation occurs, when the operation completes or fails,
    then an audit event is recorded.
  - Given package usage is metered, when consumption occurs, then telemetry is emitted
    without exposing protected health or financial data unnecessarily.
  - Given support reviews a tenant package issue, when evidence is requested, then
    installation, entitlement, compatibility and lifecycle history are available.
user_stories:
- id: US-PLT-011-001
  capability: BCM-PLT-011
  title: Discover marketplace packages
  story: As a tenant administrator, I want to discover available packages so that
    I can evaluate optional product functionality for my organization.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Scenario: only published and compatible packages are shown.
  - Scenario: package type, version, support owner and compatibility are visible.
  - Scenario: unavailable packages show actionable reasons.
- id: US-PLT-011-002
  capability: BCM-PLT-011
  title: Purchase or grant a package offer
  story: As a customer buyer, I want to purchase or receive a commercial offer so
    that my tenant can be entitled to use a package.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Scenario: purchase, trial and private grant flows are distinguishable.
  - Scenario: billing provider details remain behind an adapter boundary.
  - Scenario: commercial offer state is auditable.
- id: US-PLT-011-003
  capability: BCM-PLT-011
  title: Resolve entitlement before usage
  story: As a platform administrator, I want package execution to require entitlement
    and authorization so that purchased functionality remains controlled.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Scenario: entitlement exists and IAM permission exists, so execution is allowed.
  - Scenario: entitlement exists but IAM permission is missing, so execution is denied.
  - Scenario: IAM permission exists but entitlement is missing, so execution is denied.
- id: US-PLT-011-004
  capability: BCM-PLT-011
  title: Install and manage tenant packages
  story: As a tenant administrator, I want to install, activate, upgrade and remove
    packages so that my tenant can evolve without custom product forks.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Scenario: compatibility is validated before installation.
  - Scenario: activation can be suspended without deleting package history.
  - Scenario: rollback or migration-bound uninstall behavior is documented.
- id: US-PLT-011-005
  capability: BCM-PLT-011
  title: Support marketplace package issues
  story: As a support analyst, I want lifecycle and usage evidence for packages so
    that customer issues can be diagnosed and resolved.
  priority: High
  roadmap: MVP2
  acceptance_criteria:
  - Scenario: package lifecycle history is available.
  - Scenario: entitlement decisions are explainable.
  - Scenario: telemetry supports troubleshooting without unnecessary protected data
      exposure.
non_functional_requirements:
- Marketplace package models must remain agent-agnostic, cloud-agnostic and provider-agnostic.
- Entitlement checks must be centralized and observable.
- Package lifecycle operations must record audit metadata.
- Package installation and upgrade flows must validate compatibility before activation.
- Billing-provider, payment-provider and external marketplace details must remain
  adapter-based.
definition_of_ready:
- Capability exists in BCM-001.
- Capability is mapped in BCM-002.
- Product Marketplace Standard is loaded.
- HOP Product Marketplace Contract is approved.
- Actors, portals, aggregates, events and lifecycle operations are identified.
definition_of_done:
- Marketplace package model exists.
- Commercial offer and entitlement models exist.
- Installation, activation, upgrade, rollback and retirement flows are implemented
  or explicitly deferred.
- IAM, entitlement, audit and observability requirements are validated.
- Traceability to BCM-001, BCM-002, COM-MOD-017 and HOP-MKT-001 is complete.
```
