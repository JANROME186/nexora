package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain;

import java.util.List;
import java.util.Optional;

public interface DiagnosticServiceRepository {

    DiagnosticService save(DiagnosticService service);

    Optional<DiagnosticService> findById(String serviceId);

    List<DiagnosticService> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludeServiceId);

    void replaceComponentLinks(String serviceId, List<ServiceComponentLink> links);

    List<ServiceComponentLink> findComponentLinks(String serviceId);
}
