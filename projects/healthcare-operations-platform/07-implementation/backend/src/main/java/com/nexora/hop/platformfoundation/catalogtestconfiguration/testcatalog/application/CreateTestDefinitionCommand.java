package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application;

import java.util.List;

public record CreateTestDefinitionCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String nameEn,
        String nameEs,
        String methodology,
        String measurementUnit,
        String resultType,
        Integer turnaroundTimeHours,
        List<String> analyteRefIds,
        List<String> sampleRequirementRefIds) {
}
