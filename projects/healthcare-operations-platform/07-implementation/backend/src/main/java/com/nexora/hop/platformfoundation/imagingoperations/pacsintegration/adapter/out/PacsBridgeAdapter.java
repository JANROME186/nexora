package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsBridgePort;
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
}
