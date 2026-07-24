package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;

@Repository
@Profile("!local")
class InMemoryPackageVersionRepository implements PackageVersionRepository {

    private final Map<String, PackageVersion> versions = new ConcurrentHashMap<>();

    @Override
    public PackageVersion save(PackageVersion packageVersion) {
        versions.put(packageVersion.versionId(), packageVersion);
        return packageVersion;
    }

    @Override
    public Optional<PackageVersion> findByPackageIdAndVersion(String packageId, String version) {
        return versions.values().stream()
                .filter(candidate -> candidate.packageId().equals(packageId) && candidate.version().equals(version))
                .findFirst();
    }

    @Override
    public List<PackageVersion> findByPackageId(String packageId) {
        return versions.values().stream().filter(candidate -> candidate.packageId().equals(packageId)).toList();
    }
}
