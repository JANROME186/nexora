package com.nexora.hop.platformfoundation.identityaccess.application;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
