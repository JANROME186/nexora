package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/** An enumerated coded result value for qualitative or coded analytes (ENT-ANL-003). */
public record AnalyteCodedValue(String codedValueId, String analyteId, String code, LocalizedText display) {
}
