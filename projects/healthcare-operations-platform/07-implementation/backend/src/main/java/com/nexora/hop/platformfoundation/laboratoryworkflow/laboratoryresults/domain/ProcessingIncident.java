package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;

/**
 * Processing incident recorded against a result (BCM-LAB-006, VO-LPR-004).
 */
public record ProcessingIncident(
        IncidentType incidentType,
        String notes,
        String recordedBy,
        Instant recordedAt) {
}
