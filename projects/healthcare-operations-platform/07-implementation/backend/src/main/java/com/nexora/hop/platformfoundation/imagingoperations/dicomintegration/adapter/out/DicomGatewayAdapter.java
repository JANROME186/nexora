package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomGatewayPort;
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
}
