package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application;

import java.math.BigDecimal;

public record UpdateSampleRequirementCommand(
        String sampleTypeRefId,
        BigDecimal minVolumeMl,
        String containerRefId,
        String handlingInstructionsEn,
        String handlingInstructionsEs,
        String storageTemperature) {
}
