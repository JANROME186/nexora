package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port;

import java.time.Instant;

public record PacsWadoRetrieveResponse(
        String studyInstanceUid,
        String seriesInstanceUid,
        String objectUid,
        String retrieveUrl,
        String format,
        Instant expirationTimestamp
) {}
