package com.nexora.hop.platformfoundation.datamigrationportability.shared;

public class MigrationEntityNotFoundException extends RuntimeException {

    private final String code;

    public MigrationEntityNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
