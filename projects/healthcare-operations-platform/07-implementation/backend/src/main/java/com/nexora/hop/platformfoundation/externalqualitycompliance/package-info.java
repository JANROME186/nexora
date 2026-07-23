/**
 * External Quality and Compliance bounded context, compiled from COM-MOD-013 capability packages.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "External Quality and Compliance",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance", "documentmanagement"})
package com.nexora.hop.platformfoundation.externalqualitycompliance;
