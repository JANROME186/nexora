package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.adapter.out.memory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStep;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStepRepository;

@Repository
@Profile("!local")
class InMemoryInstallationStepRepository implements InstallationStepRepository {

    private final Map<String, InstallationStep> steps = new ConcurrentHashMap<>();

    @Override
    public InstallationStep save(InstallationStep step) {
        steps.put(step.stepId(), step);
        return step;
    }

    @Override
    public List<InstallationStep> findByInstallationIdOrderByOccurredAt(String installationId) {
        return steps.values().stream()
                .filter(candidate -> candidate.installationId().equals(installationId))
                .sorted(Comparator.comparing(InstallationStep::occurredAt))
                .toList();
    }
}
