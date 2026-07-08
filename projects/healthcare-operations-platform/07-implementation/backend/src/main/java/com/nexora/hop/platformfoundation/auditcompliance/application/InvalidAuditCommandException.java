package com.nexora.hop.platformfoundation.auditcompliance.application;

public class InvalidAuditCommandException extends RuntimeException {

    public InvalidAuditCommandException(String message) {
        super(message);
    }
}
