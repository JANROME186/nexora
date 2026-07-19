package com.nexora.hop.platformfoundation.identityaccess.application;

public class AccountSuspendedException extends RuntimeException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
