package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

/**
 * Outbound port through which {@code ImportExecution} delegates one declared entity category's
 * import to the owning capability's domain command (RN-003, CUS-MIG-010-04), mirroring
 * {@code IntegrationAdapterPort}/{@code FiscalAdapterPort}/{@code NotificationProviderPort}/
 * {@code DocumentStoragePort}. Implementations must never write directly to a business
 * aggregate's storage (INV-MIG-003) — this port is the only interaction point
 * {@link com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.application.MigrationManagementService}
 * uses to advance an import, so satisfying that constraint here satisfies it for the whole
 * commit/retry flow by construction.
 *
 * <p>A production implementation would delegate to each owning module's own existing application
 * command (e.g. patient registration, catalog publication) once that module exposes a
 * migration-import entry point; see technical debt TD-BE-014 for that real cross-module wiring,
 * which is deliberately not invented prematurely here (no HOP module currently exposes such an
 * entry point).</p>
 */
public interface MigrationDomainCommandPort {

    /**
     * Idempotently invokes the import command for one manifest-declared entity category.
     * Implementations must return the same identifier for the same
     * {@code (migrationJobId, entityCategory)} pair on every call, so a retried attempt can detect
     * it already ran without re-invoking it (RN-004, INV-MIG-004).
     *
     * @return a stable identifier for the invoked command, safe to persist in
     *         {@link ImportExecution#domainCommandsInvoked()}
     * @throws MigrationAdapterException if the category could not be imported this attempt
     */
    String invokeImportCommand(String migrationJobId, String entityCategory, int recordCount);
}
