package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticServiceRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.ServiceComponentLink;

@Repository
@Profile("!local")
class InMemoryDiagnosticServiceRepository implements DiagnosticServiceRepository {

    private final Map<String, DiagnosticService> services = new ConcurrentHashMap<>();
    private final Map<String, List<ServiceComponentLink>> componentLinks = new ConcurrentHashMap<>();

    @Override
    public DiagnosticService save(DiagnosticService service) {
        services.put(service.serviceId(), service);
        return service;
    }

    @Override
    public Optional<DiagnosticService> findById(String serviceId) {
        return Optional.ofNullable(services.get(serviceId));
    }

    @Override
    public List<DiagnosticService> findByLaboratoryId(String laboratoryId) {
        return services.values().stream()
                .filter(service -> service.laboratoryId().equals(laboratoryId))
                .toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeServiceId) {
        return services.values().stream()
                .anyMatch(service -> service.laboratoryId().equals(laboratoryId)
                        && service.code().equals(code)
                        && !service.serviceId().equals(excludeServiceId));
    }

    @Override
    public void replaceComponentLinks(String serviceId, List<ServiceComponentLink> links) {
        componentLinks.put(serviceId, new ArrayList<>(links));
    }

    @Override
    public List<ServiceComponentLink> findComponentLinks(String serviceId) {
        return List.copyOf(componentLinks.getOrDefault(serviceId, List.of()));
    }
}
