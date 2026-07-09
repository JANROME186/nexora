/**
 * People and Clinical Master Data application module (MVP-MOD-003). Hosts the shared person
 * primitives (BCM-PER-001), the Patient aggregate (BCM-PER-002, AGG-001), the Doctor aggregate
 * (BCM-PER-003, AGG-005) and the Patient Registration orchestration (BCM-ATT-002). Bounded
 * contexts patient-management and medical-staff are kept as sibling packages inside this Spring
 * Modulith module. Cross-module dependencies are limited to organization-management (tenant
 * lookup) and audit-compliance (append-only audit sink).
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "People and Clinical Master Data",
        allowedDependencies = { "organizationmanagement", "auditcompliance" })
package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;
