package com.nexora.hop.platformfoundation.integrationinteroperability.shared;

public class IntegrationConflictException extends RuntimeException {

    private final String code;

    public IntegrationConflictException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
