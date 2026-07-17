package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

/**
 * Command to technically validate a laboratory result.
 */
public record TechnicalValidateCommand(
        String resultId,
        String tenantId,
        String actorId,
        boolean approved
) {}
