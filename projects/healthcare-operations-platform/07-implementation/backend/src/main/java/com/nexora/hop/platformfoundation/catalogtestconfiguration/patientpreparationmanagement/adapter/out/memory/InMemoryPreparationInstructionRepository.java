package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationAssignment;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstructionRepository;

@Repository
@Profile("!local")
class InMemoryPreparationInstructionRepository implements PreparationInstructionRepository {

    private final Map<String, PreparationInstruction> preparations = new ConcurrentHashMap<>();
    private final Map<String, List<PreparationAssignment>> assignments = new ConcurrentHashMap<>();

    @Override
    public PreparationInstruction save(PreparationInstruction preparation) {
        preparations.put(preparation.preparationId(), preparation);
        return preparation;
    }

    @Override
    public Optional<PreparationInstruction> findById(String preparationId) {
        return Optional.ofNullable(preparations.get(preparationId));
    }

    @Override
    public List<PreparationInstruction> findByLaboratoryId(String laboratoryId) {
        return preparations.values().stream()
                .filter(preparation -> preparation.laboratoryId().equals(laboratoryId))
                .toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludePreparationId) {
        return preparations.values().stream()
                .anyMatch(preparation -> preparation.laboratoryId().equals(laboratoryId)
                        && preparation.code().equals(code)
                        && !preparation.preparationId().equals(excludePreparationId));
    }

    @Override
    public PreparationAssignment saveAssignment(PreparationAssignment assignment) {
        assignments.computeIfAbsent(assignment.preparationId(), key -> new ArrayList<>()).add(assignment);
        return assignment;
    }

    @Override
    public List<PreparationAssignment> findAssignments(String preparationId) {
        return List.copyOf(assignments.getOrDefault(preparationId, List.of()));
    }

    @Override
    public List<PreparationAssignment> findAssignmentsByTarget(String targetType, String targetRefId) {
        return assignments.values().stream()
                .flatMap(List::stream)
                .filter(assignment -> assignment.targetType().equals(targetType)
                        && assignment.targetRefId().equals(targetRefId))
                .toList();
    }
}
