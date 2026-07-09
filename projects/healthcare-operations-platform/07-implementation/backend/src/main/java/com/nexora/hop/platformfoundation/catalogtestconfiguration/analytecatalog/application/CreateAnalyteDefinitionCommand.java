package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application;

import java.math.BigDecimal;
import java.util.List;

public record CreateAnalyteDefinitionCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String nameEn,
        String nameEs,
        String loincCode,
        String resultDataType,
        String measurementUnit,
        Integer decimalPrecision,
        BigDecimal minValue,
        BigDecimal maxValue,
        List<CodedValueInput> codedValues) {
}
