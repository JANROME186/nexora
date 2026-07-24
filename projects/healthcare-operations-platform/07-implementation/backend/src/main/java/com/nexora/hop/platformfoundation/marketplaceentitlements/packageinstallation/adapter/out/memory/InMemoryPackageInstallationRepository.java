package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallation;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallationRepository;

@Repository
@Profile("!local")
class InMemoryPackageInstallationRepository implements PackageInstallationRepository {

    private final Map<String, PackageInstallation> installations = new ConcurrentHashMap<>();

    @Override
    public PackageInstallation save(PackageInstallation installation) {
        installations.put(installation.installationId(), installation);
        return installation;
    }

    @Override
    public Optional<PackageInstallation> findById(String installationId) {
        return Optional.ofNullable(installations.get(installationId));
    }

    @Override
    public List<PackageInstallation> findByTenantId(String tenantId) {
        return installations.values().stream().filter(candidate -> candidate.tenantId().equals(tenantId)).toList();
    }

    @Override
    public List<PackageInstallation> findByTenantIdAndPackageId(String tenantId, String packageId) {
        return installations.values().stream()
                .filter(candidate -> candidate.tenantId().equals(tenantId) && candidate.packageId().equals(packageId))
                .toList();
    }
}
