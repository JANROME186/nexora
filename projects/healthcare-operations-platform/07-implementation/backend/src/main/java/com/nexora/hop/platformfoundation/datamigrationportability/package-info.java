/**
 * Data Migration Portability bounded context, compiled from MVP-MOD-008 Integration and Migration
 * Readiness capability packages. Hosts the MigrationJob aggregate (BCM-PLT-010, AGG-016 per
 * aggregate-catalog.md), implementing the pre-existing HOP Open Data Ingestion Standard and
 * Contract without redefinition. Never writes directly to a business aggregate's storage
 * (INV-MIG-003); {@code ImportExecution.domainCommandsInvoked} may only reference existing domain
 * commands, and real cross-module command invocation is explicit MVP-MOD-008-BE-002 scope. Reuses
 * {@code documentmanagement.domain.DocumentStoragePort} for import-package storage
 * (PORT-MIG-001) rather than declaring a new storage boundary.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Data Migration Portability",
        allowedDependencies = {
            "sharedkernel", "organizationmanagement", "auditcompliance",
            "documentmanagement::document-service", "documentmanagement::document-domain"
        })
package com.nexora.hop.platformfoundation.datamigrationportability;
