/**
 * Laboratory Workflow bounded context generated from MVP-MOD-006 capability packages.
 * Owns the Sample aggregate (AGG-008, orders-samples context) and the LaboratoryResult
 * aggregate (AGG-009, laboratory-results context). Reads audit events from AuditCompliance.
 * Does not mutate Patient, Doctor, DiagnosticOrder, Sale or Invoice aggregates.
 * Exposes a named {@code sample-read-port} interface for cross-module consumption
 * (TD-BE-010 advance; wired to DiagnosticOrderManagementService in MVP-MOD-006-BE-002).
 */
@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
            "auditcompliance"
        })
package com.nexora.hop.platformfoundation.laboratoryworkflow;
