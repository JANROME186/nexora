---
id: HOP-EVT-BCM-PLT-011
format: markdown_structured_payload
type: events
name: Product Marketplace and Entitlements Events
version: 1.0.0
status: modeled
backlog_item: COM-MOD-017-DEF
---

# Product Marketplace And Entitlements Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-011
  type: events
  name: Product Marketplace and Entitlements Events
  version: 1.0.0
  status: modeled
  backlog_item: COM-MOD-017-DEF
domain_events:
- MarketplacePackageSubmittedEvent
- MarketplacePackageCertifiedEvent
- MarketplacePackagePublishedEvent
- CommercialOfferPublishedEvent
- TenantEntitlementGrantedEvent
- TenantEntitlementRevokedEvent
- PackageInstalledEvent
- PackageActivatedEvent
- PackageUpgradeStartedEvent
- PackageRollbackCompletedEvent
- PackageSuspendedEvent
- PackageUninstalledEvent
- PackageRetiredEvent
- PackageConsumptionRecordedEvent
integration_events:
  outbound:
  - BillingUsageEvent
  - BillingSubscriptionStateRequestedEvent
  - MarketplaceAuditEvidenceRecordedEvent
  inbound:
  - BillingSubscriptionStateChangedEvent
  - PackageProviderPublicationRequestedEvent
```
