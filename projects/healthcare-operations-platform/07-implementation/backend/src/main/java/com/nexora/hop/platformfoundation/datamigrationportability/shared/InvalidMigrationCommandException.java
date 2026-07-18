package com.nexora.hop.platformfoundation.datamigrationportability.shared;

public class InvalidMigrationCommandException extends RuntimeException {

    private final String code;

    public InvalidMigrationCommandException(String message, String code) {
        super(message);
        this.code = code;
    }

    public InvalidMigrationCommandException(String message) {
        this(message, MigrationErrorCodes.MIGRATION_COMMAND_INVALID);
    }

    public String code() {
        return code;
    }
}
