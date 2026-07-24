package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackageRepository;

@Repository
@Profile("!local")
class InMemoryMarketplacePackageRepository implements MarketplacePackageRepository {

    private final Map<String, MarketplacePackage> packages = new ConcurrentHashMap<>();

    @Override
    public MarketplacePackage save(MarketplacePackage marketplacePackage) {
        packages.put(marketplacePackage.packageId(), marketplacePackage);
        return marketplacePackage;
    }

    @Override
    public Optional<MarketplacePackage> findById(String packageId) {
        return Optional.ofNullable(packages.get(packageId));
    }

    @Override
    public Optional<MarketplacePackage> findByCode(String code) {
        return packages.values().stream().filter(candidate -> candidate.code().equals(code)).findFirst();
    }

    @Override
    public List<MarketplacePackage> findByStatus(String status) {
        return packages.values().stream().filter(candidate -> candidate.status().equals(status)).toList();
    }

    @Override
    public List<MarketplacePackage> findAll() {
        return List.copyOf(packages.values());
    }
}
