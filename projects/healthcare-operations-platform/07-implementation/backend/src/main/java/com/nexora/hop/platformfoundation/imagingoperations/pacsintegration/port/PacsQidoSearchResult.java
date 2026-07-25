package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port;

public record PacsQidoSearchResult(
        String studyInstanceUid,
        String patientId,
        String patientName,
        String modality,
        String studyDate,
        int numberOfStudyRelatedInstances,
        String pacsNodeId
) {}
