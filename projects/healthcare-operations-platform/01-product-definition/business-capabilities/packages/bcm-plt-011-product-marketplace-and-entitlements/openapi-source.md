---
id: HOP-API-BCM-PLT-011
format: markdown_structured_payload
type: openapi-source
name: Product Marketplace and Entitlements API Source
version: 1.0.0
status: modeled
backlog_item: COM-MOD-017-DEF
---

# Product Marketplace And Entitlements Api Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-PLT-011
  type: openapi-source
  name: Product Marketplace and Entitlements API Source
  version: 1.0.0
  status: modeled
  backlog_item: COM-MOD-017-DEF
base_path: /api/marketplace
resources:
- path: /packages
  operations:
  - listPublishedPackages
  - submitPackage
  - publishPackage
- path: /packages/{packageId}/versions/{version}
  operations:
  - getPackageVersion
  - certifyPackageVersion
  - retirePackageVersion
- path: /offers
  operations:
  - listOffers
  - publishOffer
  - acceptOffer
- path: /tenants/{tenantId}/entitlements
  operations:
  - listTenantEntitlements
  - grantEntitlement
  - revokeEntitlement
- path: /tenants/{tenantId}/installations
  operations:
  - listInstallations
  - installPackage
  - activatePackage
  - suspendPackage
  - uninstallPackage
- path: /tenants/{tenantId}/installations/{installationId}/upgrade
  operations:
  - upgradePackage
  - rollbackPackage
- path: /compatibility/evaluate
  operations:
  - evaluateCompatibility
- path: /billing/events
  operations:
  - publishBillingEvent
error_model:
  code_namespace: marketplace.error
  standard_errors:
  - PACKAGE_NOT_FOUND
  - OFFER_NOT_AVAILABLE
  - ENTITLEMENT_REQUIRED
  - ENTITLEMENT_EXPIRED
  - COMPATIBILITY_FAILED
  - INSTALLATION_CONFLICT
  - ROLLBACK_NOT_AVAILABLE
  - PROVIDER_ADAPTER_UNAVAILABLE
```
