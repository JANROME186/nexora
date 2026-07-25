package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsBridgePort;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsQidoSearchResult;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsStowStoreResult;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsWadoRetrieveResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PacsBridgeAdapter implements PacsBridgePort {

    @Override
    public boolean pingEndpoint(String baseUrl, String protocol) {
        return baseUrl != null && baseUrl.startsWith("http");
    }

    @Override
    public String queryStudyInstances(String pacsNodeId, String accessionNumber) {
        return "PACS_QUERY_OK: NODE=" + pacsNodeId + ", ACCESSION=" + accessionNumber;
    }

    @Override
    public List<PacsQidoSearchResult> qidoSearchStudies(String pacsNodeId, String patientId, String modality) {
        String safePatientId = (patientId != null && !patientId.isBlank()) ? patientId : "PAT-UNKNOWN";
        String safeModality = (modality != null && !modality.isBlank()) ? modality : "MR";
        String studyUid = "1.3.12.2.1107.5.2.32.35177." + Math.abs(safePatientId.hashCode());

        PacsQidoSearchResult result = new PacsQidoSearchResult(
                studyUid,
                safePatientId,
                "Patient " + safePatientId,
                safeModality,
                "2026-07-25",
                24,
                pacsNodeId
        );
        return List.of(result);
    }

    @Override
    public PacsWadoRetrieveResponse getWadoRetrieveUrl(String pacsNodeId, String studyInstanceUid, String seriesInstanceUid, String objectUid) {
        String safeStudyUid = (studyInstanceUid != null && !studyInstanceUid.isBlank()) ? studyInstanceUid : "1.3.12.2.1107.5.2.32.35177.1";
        String safeSeriesUid = (seriesInstanceUid != null && !seriesInstanceUid.isBlank()) ? seriesInstanceUid : "1.3.12.2.1107.5.2.32.35177.1.1";
        String safeObjectUid = (objectUid != null && !objectUid.isBlank()) ? objectUid : "1.3.12.2.1107.5.2.32.35177.1.1.1";

        String url = "https://pacs.nexora.local/wado?requestType=WADO&studyUID=" + safeStudyUid + "&seriesUID=" + safeSeriesUid + "&objectUID=" + safeObjectUid;
        Instant expires = Instant.now().plus(2, ChronoUnit.HOURS);

        return new PacsWadoRetrieveResponse(
                safeStudyUid,
                safeSeriesUid,
                safeObjectUid,
                url,
                "application/dicom",
                expires
        );
    }

    @Override
    public PacsStowStoreResult storeWebInstances(String pacsNodeId, String studyInstanceUid, String contentType, byte[] dicomPayload) {
        String safeStudyUid = (studyInstanceUid != null && !studyInstanceUid.isBlank()) ? studyInstanceUid : "1.3.12.2.1107.5.2.32.35177.2";
        String storeId = "STOW-" + UUID.randomUUID().toString().substring(0, 8);
        int instanceCount = (dicomPayload != null && dicomPayload.length > 0) ? 1 : 0;

        return new PacsStowStoreResult(
                storeId,
                safeStudyUid,
                pacsNodeId,
                instanceCount,
                "STORED",
                200,
                Instant.now()
        );
    }
}
