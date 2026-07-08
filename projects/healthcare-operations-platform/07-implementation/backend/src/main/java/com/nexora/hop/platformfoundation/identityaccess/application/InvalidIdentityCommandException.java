package com.nexora.hop.platformfoundation.identityaccess.application;

public class InvalidIdentityCommandException extends RuntimeException {

    public InvalidIdentityCommandException(String message) {
        super(message);
    }
}
