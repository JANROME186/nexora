package com.nexora.hop.platformfoundation.integrationinteroperability.shared;

public class InvalidIntegrationCommandException extends RuntimeException {

    private final String code;

    public InvalidIntegrationCommandException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
