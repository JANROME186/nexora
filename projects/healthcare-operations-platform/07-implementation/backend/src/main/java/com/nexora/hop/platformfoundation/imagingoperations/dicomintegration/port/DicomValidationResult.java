package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port;

import java.util.List;

public record DicomValidationResult(
        boolean valid,
        String aeTitle,
        String patientId,
        String studyInstanceUid,
        String modality,
        String headerChecksum,
        int errorCount,
        List<String> validationNotes
) {}
