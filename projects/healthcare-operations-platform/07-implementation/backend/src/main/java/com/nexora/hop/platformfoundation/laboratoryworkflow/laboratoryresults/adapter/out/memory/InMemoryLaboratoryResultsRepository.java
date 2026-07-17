package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;

/**
 * In-memory repository for LaboratoryResult aggregate (test and default profile).
 */
@Repository
@Profile("!local")
class InMemoryLaboratoryResultsRepository implements LaboratoryResultsRepository {

    private final Map<String, LaboratoryResult> store = new ConcurrentHashMap<>();

    @Override
    public LaboratoryResult save(LaboratoryResult result) {
        store.put(key(result.resultId(), result.tenantId()), result);
        return result;
    }

    @Override
    public Optional<LaboratoryResult> findById(String resultId, String tenantId) {
        return Optional.ofNullable(store.get(key(resultId, tenantId)));
    }

    @Override
    public List<LaboratoryResult> findBySampleId(String sampleId, String tenantId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId()) && sampleId.equals(r.sampleId()))
                .toList();
    }

    @Override
    public List<LaboratoryResult> findByStatus(ResultStatus status, String tenantId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId()) && status == r.status())
                .toList();
    }

    @Override
    public List<LaboratoryResult> findProcessingWorklist(String tenantId, String laboratoryId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId())
                        && laboratoryId.equals(r.laboratoryId())
                        && r.status() == ResultStatus.captured)
                .toList();
    }

    @Override
    public List<LaboratoryResult> findTechnicalValidationWorklist(String tenantId,
            String laboratoryId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId())
                        && laboratoryId.equals(r.laboratoryId())
                        && r.status() == ResultStatus.pending_technical_validation)
                .toList();
    }

    @Override
    public List<LaboratoryResult> findMedicalValidationWorklist(String tenantId,
            String laboratoryId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId())
                        && laboratoryId.equals(r.laboratoryId())
                        && r.status() == ResultStatus.pending_medical_validation)
                .toList();
    }

    @Override
    public List<LaboratoryResult> findReleaseWorklist(String tenantId, String laboratoryId) {
        return store.values().stream()
                .filter(r -> tenantId.equals(r.tenantId())
                        && laboratoryId.equals(r.laboratoryId())
                        && r.status() == ResultStatus.medically_validated)
                .toList();
    }

    private static String key(String resultId, String tenantId) {
        return tenantId + ":" + resultId;
    }
}
