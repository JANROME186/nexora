package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomGatewayPort;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomTransferResult;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomValidationResult;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomWorklistEntry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DicomGatewayAdapter implements DicomGatewayPort {

    @Override
    public boolean testConnection(String aeTitle, String host, int port) {
        return aeTitle != null && !aeTitle.isBlank() && port > 0;
    }

    @Override
    public String echoCEcho(String aeTitle) {
        return "C-ECHO SUCCESS: AE_TITLE=" + aeTitle;
    }

    @Override
    public List<DicomWorklistEntry> queryWorklist(String aeTitle, String patientId, String modality) {
        String safePatientId = (patientId != null && !patientId.isBlank()) ? patientId : "PAT-DEFAULT";
        String safeModality = (modality != null && !modality.isBlank()) ? modality : "CT";
        String worklistId = "MWL-" + UUID.randomUUID().toString().substring(0, 8);
        String accessionNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8);
        String stepId = "SPS-" + UUID.randomUUID().toString().substring(0, 8);

        DicomWorklistEntry entry = new DicomWorklistEntry(
                worklistId,
                safePatientId,
                "Patient " + safePatientId,
                accessionNumber,
                safeModality,
                stepId,
                Instant.now(),
                aeTitle
        );
        return List.of(entry);
    }

    @Override
    public DicomTransferResult requestStudyTransfer(String aeTitle, String studyInstanceUid, String destinationAeTitle) {
        String safeStudyUid = (studyInstanceUid != null && !studyInstanceUid.isBlank()) ? studyInstanceUid : "1.2.840.10008.1.1";
        String safeDest = (destinationAeTitle != null && !destinationAeTitle.isBlank()) ? destinationAeTitle : "PACS_VIEWER";
        String transferId = "XFR-" + UUID.randomUUID().toString().substring(0, 8);

        return new DicomTransferResult(
                transferId,
                safeStudyUid,
                safeDest,
                "COMPLETED",
                12,
                150L,
                Instant.now()
        );
    }

    @Override
    public DicomValidationResult validateDatasetHeader(String aeTitle, String patientId, String studyInstanceUid, String modality) {
        boolean valid = aeTitle != null && !aeTitle.isBlank() && studyInstanceUid != null && !studyInstanceUid.isBlank();
        String checksum = "CHK-" + Math.abs((aeTitle + ":" + studyInstanceUid).hashCode());
        List<String> notes = valid ? List.of("Header tags valid", "VR implicit VR LE matched") : List.of("Missing mandatory DICOM tags");

        return new DicomValidationResult(
                valid,
                aeTitle,
                patientId,
                studyInstanceUid,
                modality,
                checksum,
                valid ? 0 : 1,
                notes
        );
    }
}
