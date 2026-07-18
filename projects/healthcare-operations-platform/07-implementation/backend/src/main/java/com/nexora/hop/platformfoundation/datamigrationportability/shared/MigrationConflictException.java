package com.nexora.hop.platformfoundation.datamigrationportability.shared;

public class MigrationConflictException extends RuntimeException {

    private final String code;

    public MigrationConflictException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
