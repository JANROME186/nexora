package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;

@Repository
@Profile("!local")
class InMemoryTenantEntitlementRepository implements TenantEntitlementRepository {

    private final Map<String, TenantEntitlement> entitlements = new ConcurrentHashMap<>();

    @Override
    public TenantEntitlement save(TenantEntitlement entitlement) {
        entitlements.put(entitlement.entitlementId(), entitlement);
        return entitlement;
    }

    @Override
    public Optional<TenantEntitlement> findById(String entitlementId) {
        return Optional.ofNullable(entitlements.get(entitlementId));
    }

    @Override
    public List<TenantEntitlement> findByTenantId(String tenantId) {
        return entitlements.values().stream().filter(candidate -> candidate.tenantId().equals(tenantId)).toList();
    }

    @Override
    public List<TenantEntitlement> findByTenantIdAndPackageId(String tenantId, String packageId) {
        return entitlements.values().stream()
                .filter(candidate -> candidate.tenantId().equals(tenantId) && candidate.packageId().equals(packageId))
                .toList();
    }
}
