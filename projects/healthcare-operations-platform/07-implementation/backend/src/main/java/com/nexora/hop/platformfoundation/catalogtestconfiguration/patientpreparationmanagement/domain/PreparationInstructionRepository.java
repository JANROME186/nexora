package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface PreparationInstructionRepository {

    PreparationInstruction save(PreparationInstruction preparation);

    Optional<PreparationInstruction> findById(String preparationId);

    List<PreparationInstruction> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludePreparationId);

    PreparationAssignment saveAssignment(PreparationAssignment assignment);

    List<PreparationAssignment> findAssignments(String preparationId);

    List<PreparationAssignment> findAssignmentsByTarget(String targetType, String targetRefId);
}
