package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application;

public record CreatePreparationInstructionCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String titleEn,
        String titleEs,
        String instructionTextEn,
        String instructionTextEs,
        String category,
        Integer durationHours) {
}
