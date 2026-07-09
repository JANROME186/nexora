package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application;

import java.math.BigDecimal;

public record CreateSampleRequirementCommand(
        String tenantId,
        String laboratoryId,
        String sampleTypeRefId,
        BigDecimal minVolumeMl,
        String containerRefId,
        String handlingInstructionsEn,
        String handlingInstructionsEs,
        String storageTemperature) {
}
