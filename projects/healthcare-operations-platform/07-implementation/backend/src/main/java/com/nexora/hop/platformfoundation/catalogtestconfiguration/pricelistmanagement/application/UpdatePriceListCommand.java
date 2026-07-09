package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application;

import java.time.LocalDate;

public record UpdatePriceListCommand(
        String nameEn, String nameEs, String agreementRefId, LocalDate effectiveFrom, LocalDate effectiveTo) {
}
