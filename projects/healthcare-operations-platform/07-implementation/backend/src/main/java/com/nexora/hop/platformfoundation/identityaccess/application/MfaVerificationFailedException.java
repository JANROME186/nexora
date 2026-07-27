package com.nexora.hop.platformfoundation.identityaccess.application;

/** Thrown when a user has MFA enabled and the supplied TOTP code does not verify. */
public class MfaVerificationFailedException extends RuntimeException {
    public MfaVerificationFailedException(String message) {
        super(message);
    }
}
