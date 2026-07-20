package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpectedRange(BigDecimal min, BigDecimal max, LocalDateTime capturedAt) {}
