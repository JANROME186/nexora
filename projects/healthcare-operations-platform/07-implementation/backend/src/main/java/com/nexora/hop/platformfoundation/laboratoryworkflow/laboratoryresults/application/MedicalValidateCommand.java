package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

/**
 * Command to medically validate a laboratory result.
 */
public record MedicalValidateCommand(
        String resultId,
        String tenantId,
        String actorId,
        String licenseIdentifier
) {}
