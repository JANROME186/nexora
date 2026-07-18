/**
 * Integration Interoperability bounded context, compiled from MVP-MOD-008 Integration and
 * Migration Readiness capability packages. Hosts the IntegrationEndpoint aggregate
 * (BCM-PLT-004, inbound/outbound message adapter boundary) and the ApiSurfaceRegistration
 * aggregate (BCM-PLT-005, API classification and partner-key governance) as sibling
 * sub-packages, mirroring how frontdeskcaredelivery hosts two bounded-context sub-packages
 * inside a single Spring Modulith module. Neither capability mutates any other module's
 * aggregates; BCM-PLT-004's IntegrationAdapterPort is a provider-agnostic replaceable
 * boundary mirroring FiscalAdapterPort/NotificationProviderPort/DocumentStoragePort.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Integration Interoperability",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance"})
package com.nexora.hop.platformfoundation.integrationinteroperability;
