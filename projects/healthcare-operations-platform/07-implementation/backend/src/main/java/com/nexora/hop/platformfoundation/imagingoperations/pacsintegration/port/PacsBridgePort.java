package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port;

import java.util.List;

public interface PacsBridgePort {
    boolean pingEndpoint(String baseUrl, String protocol);
    String queryStudyInstances(String pacsNodeId, String accessionNumber);
    List<PacsQidoSearchResult> qidoSearchStudies(String pacsNodeId, String patientId, String modality);
    PacsWadoRetrieveResponse getWadoRetrieveUrl(String pacsNodeId, String studyInstanceUid, String seriesInstanceUid, String objectUid);
    PacsStowStoreResult storeWebInstances(String pacsNodeId, String studyInstanceUid, String contentType, byte[] dicomPayload);
}
