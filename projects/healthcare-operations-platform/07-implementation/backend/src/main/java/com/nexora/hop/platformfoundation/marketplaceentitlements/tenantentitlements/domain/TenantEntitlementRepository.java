package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain;

import java.util.List;
import java.util.Optional;

public interface TenantEntitlementRepository {

    TenantEntitlement save(TenantEntitlement entitlement);

    Optional<TenantEntitlement> findById(String entitlementId);

    List<TenantEntitlement> findByTenantId(String tenantId);

    List<TenantEntitlement> findByTenantIdAndPackageId(String tenantId, String packageId);
}
