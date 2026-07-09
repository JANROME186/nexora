package com.nexora.hop.platformfoundation.catalogtestconfiguration.shared;

import java.math.BigDecimal;

/**
 * Money value object shared by price list entries, as modeled in
 * bcm-svc-009-price-list-management/business-model.yaml.
 */
public record Money(String currency, BigDecimal amount) {
}
