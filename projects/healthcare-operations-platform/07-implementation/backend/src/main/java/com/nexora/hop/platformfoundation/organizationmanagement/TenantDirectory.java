package com.nexora.hop.platformfoundation.organizationmanagement;

import java.util.Optional;

/**
 * Read-only cross-module lookup exposed by the organization-management module so that
 * other modules can validate tenant existence and status without depending on internal domain or
 * application types.
 */
public interface TenantDirectory {

    boolean tenantExists(String tenantId);

    /** The tenant's current {@code TenantLifecycle} status (e.g. {@code ACTIVE}), if the tenant exists. */
    Optional<String> tenantStatus(String tenantId);
}
