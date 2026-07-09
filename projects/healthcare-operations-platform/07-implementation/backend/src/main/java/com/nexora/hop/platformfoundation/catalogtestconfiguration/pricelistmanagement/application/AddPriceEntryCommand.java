package com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application;

import java.math.BigDecimal;

public record AddPriceEntryCommand(String itemType, String itemRefId, BigDecimal amount) {
}
