package com.nexora.hop.platformfoundation.integrationinteroperability.shared;

public class IntegrationEntityNotFoundException extends RuntimeException {

    private final String code;

    public IntegrationEntityNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
