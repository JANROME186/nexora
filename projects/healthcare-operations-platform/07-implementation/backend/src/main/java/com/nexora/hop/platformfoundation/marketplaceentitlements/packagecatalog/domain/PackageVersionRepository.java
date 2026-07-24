package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain;

import java.util.List;
import java.util.Optional;

public interface PackageVersionRepository {

    PackageVersion save(PackageVersion packageVersion);

    Optional<PackageVersion> findByPackageIdAndVersion(String packageId, String version);

    List<PackageVersion> findByPackageId(String packageId);
}
