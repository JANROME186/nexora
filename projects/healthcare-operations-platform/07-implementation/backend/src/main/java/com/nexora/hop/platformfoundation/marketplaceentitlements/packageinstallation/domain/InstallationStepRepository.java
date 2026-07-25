package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain;

import java.util.List;

public interface InstallationStepRepository {

    InstallationStep save(InstallationStep step);

    /** Ordered oldest-first, so the last matching element is the most recent step. */
    List<InstallationStep> findByInstallationIdOrderByOccurredAt(String installationId);
}
