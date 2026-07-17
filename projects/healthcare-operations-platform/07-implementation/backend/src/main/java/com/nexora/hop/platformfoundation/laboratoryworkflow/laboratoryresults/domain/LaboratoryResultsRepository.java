package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for the LaboratoryResult aggregate (AGG-009, laboratory-results bounded context).
 * Implementations: InMemoryLaboratoryResultsRepository, JdbcLaboratoryResultsRepository.
 */
public interface LaboratoryResultsRepository {

    LaboratoryResult save(LaboratoryResult result);

    Optional<LaboratoryResult> findById(String resultId, String tenantId);

    List<LaboratoryResult> findBySampleId(String sampleId, String tenantId);

    List<LaboratoryResult> findByStatus(ResultStatus status, String tenantId);

    List<LaboratoryResult> findProcessingWorklist(String tenantId, String laboratoryId);

    List<LaboratoryResult> findTechnicalValidationWorklist(String tenantId, String laboratoryId);

    List<LaboratoryResult> findMedicalValidationWorklist(String tenantId, String laboratoryId);

    List<LaboratoryResult> findReleaseWorklist(String tenantId, String laboratoryId);
}
