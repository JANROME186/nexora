package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain;

import java.util.List;
import java.util.Optional;

public interface PackageInstallationRepository {

    PackageInstallation save(PackageInstallation installation);

    Optional<PackageInstallation> findById(String installationId);

    List<PackageInstallation> findByTenantId(String tenantId);

    List<PackageInstallation> findByTenantIdAndPackageId(String tenantId, String packageId);
}
