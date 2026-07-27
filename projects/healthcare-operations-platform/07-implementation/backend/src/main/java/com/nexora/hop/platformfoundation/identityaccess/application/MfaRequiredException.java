package com.nexora.hop.platformfoundation.identityaccess.application;

/** Thrown when a user has MFA enabled and the login request did not include a TOTP code. */
public class MfaRequiredException extends RuntimeException {
    public MfaRequiredException(String message) {
        super(message);
    }
}
