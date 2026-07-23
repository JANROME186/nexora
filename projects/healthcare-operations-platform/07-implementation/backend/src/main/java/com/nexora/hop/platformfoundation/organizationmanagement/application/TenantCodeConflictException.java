package com.nexora.hop.platformfoundation.organizationmanagement.application;

/** BCM-ORG-001 invariant: "Tenant code must be unique globally." */
public class TenantCodeConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TenantCodeConflictException(String message) {
        super(message);
    }
}
