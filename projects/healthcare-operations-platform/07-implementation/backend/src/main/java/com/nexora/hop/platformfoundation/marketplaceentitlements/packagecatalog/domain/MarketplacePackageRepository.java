package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain;

import java.util.List;
import java.util.Optional;

public interface MarketplacePackageRepository {

    MarketplacePackage save(MarketplacePackage marketplacePackage);

    Optional<MarketplacePackage> findById(String packageId);

    Optional<MarketplacePackage> findByCode(String code);

    List<MarketplacePackage> findByStatus(String status);

    List<MarketplacePackage> findAll();
}
