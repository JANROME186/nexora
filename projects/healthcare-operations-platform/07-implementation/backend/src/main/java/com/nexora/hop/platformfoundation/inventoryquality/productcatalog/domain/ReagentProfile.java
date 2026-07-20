package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain;

import java.math.BigDecimal;

/**
 * VO-CAT-002 ReagentProfile. Delegated field placeholder on the shared aggregate; owned and
 * mutated exclusively by BCM-INV-002 Reagent Management.
 */
public record ReagentProfile(
    String linkedTestDefinitionId, String reagentCategory, BigDecimal consumptionUnitRatio) {

  public static final String CATEGORY_CALIBRATOR = "calibrator";
  public static final String CATEGORY_CONTROL = "control";
  public static final String CATEGORY_WORKING_REAGENT = "working_reagent";
  public static final String CATEGORY_BUFFER = "buffer";
  public static final String CATEGORY_DILUENT = "diluent";
  public static final String CATEGORY_OTHER = "other";
}
