package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port;

public interface PacsBridgePort {
    boolean pingEndpoint(String baseUrl, String protocol);
    String queryStudyInstances(String pacsNodeId, String accessionNumber);
}
