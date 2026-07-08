package com.nexora.hop.platformfoundation.organizationmanagement;

/**
 * Read-only cross-module lookup exposed by the organization-management module so that
 * other modules can validate tenant existence without depending on internal domain or
 * application types.
 */
public interface TenantDirectory {

    boolean tenantExists(String tenantId);
}
