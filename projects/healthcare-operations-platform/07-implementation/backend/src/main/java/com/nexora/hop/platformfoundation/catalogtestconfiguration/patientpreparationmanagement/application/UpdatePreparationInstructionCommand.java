package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application;

public record UpdatePreparationInstructionCommand(
        String code,
        String titleEn,
        String titleEs,
        String instructionTextEn,
        String instructionTextEs,
        String category,
        Integer durationHours) {
}
