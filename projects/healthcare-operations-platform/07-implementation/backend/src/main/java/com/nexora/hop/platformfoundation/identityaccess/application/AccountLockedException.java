package com.nexora.hop.platformfoundation.identityaccess.application;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}
