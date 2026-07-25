package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port;

import java.util.List;

public interface DicomGatewayPort {
    boolean testConnection(String aeTitle, String host, int port);
    String echoCEcho(String aeTitle);
    List<DicomWorklistEntry> queryWorklist(String aeTitle, String patientId, String modality);
    DicomTransferResult requestStudyTransfer(String aeTitle, String studyInstanceUid, String destinationAeTitle);
    DicomValidationResult validateDatasetHeader(String aeTitle, String patientId, String studyInstanceUid, String modality);
}
