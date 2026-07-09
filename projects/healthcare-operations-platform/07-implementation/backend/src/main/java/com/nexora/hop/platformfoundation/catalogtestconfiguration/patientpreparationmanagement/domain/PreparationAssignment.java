package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain;

/** Assignment of a preparation to a test or panel (ENT-PRP-002). */
public record PreparationAssignment(String assignmentId, String preparationId, String targetType, String targetRefId) {

    public static final String TARGET_TEST = "test";
    public static final String TARGET_PANEL = "panel";
}
