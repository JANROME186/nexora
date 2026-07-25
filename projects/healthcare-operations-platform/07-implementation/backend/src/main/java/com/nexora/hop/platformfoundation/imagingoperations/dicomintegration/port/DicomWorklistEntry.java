package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port;

import java.time.Instant;

public record DicomWorklistEntry(
        String worklistId,
        String patientId,
        String patientName,
        String accessionNumber,
        String modality,
        String scheduledProcedureStepId,
        Instant scheduledDateTime,
        String aeTitle
) {}
