package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port;

public interface DicomGatewayPort {
    boolean testConnection(String aeTitle, String host, int port);
    String echoCEcho(String aeTitle);
}
