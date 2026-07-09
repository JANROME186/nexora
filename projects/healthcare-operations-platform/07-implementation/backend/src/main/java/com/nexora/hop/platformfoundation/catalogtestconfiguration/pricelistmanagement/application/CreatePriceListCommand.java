package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application;

import java.time.LocalDate;

public record CreatePriceListCommand(
        String tenantId,
        String laboratoryId,
        String code,
        String nameEn,
        String nameEs,
        String currency,
        String agreementRefId,
        LocalDate effectiveFrom,
        LocalDate effectiveTo) {
}
