package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

/**
 * Command to release a laboratory result.
 */
public record ReleaseResultCommand(
        String resultId,
        String tenantId,
        String actorId
) {}
