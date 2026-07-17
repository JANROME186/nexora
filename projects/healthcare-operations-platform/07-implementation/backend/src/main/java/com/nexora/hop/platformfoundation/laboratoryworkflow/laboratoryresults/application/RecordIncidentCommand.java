package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

/**
 * Command to record a processing incident against a captured result (BCM-LAB-006).
 */
public record RecordIncidentCommand(
        String resultId,
        String tenantId,
        String incidentType,
        String notes,
        String recordedBy) {
}
