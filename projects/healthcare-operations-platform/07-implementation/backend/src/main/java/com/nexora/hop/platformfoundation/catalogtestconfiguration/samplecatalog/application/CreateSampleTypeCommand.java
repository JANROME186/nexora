package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application;

public record CreateSampleTypeCommand(
        String tenantId, String laboratoryId, String code, String nameEn, String nameEs, String matrix) {
}
