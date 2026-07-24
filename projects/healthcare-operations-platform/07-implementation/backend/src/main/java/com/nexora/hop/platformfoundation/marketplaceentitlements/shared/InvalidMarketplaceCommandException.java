package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

public class InvalidMarketplaceCommandException extends RuntimeException {

    private final String code;

    public InvalidMarketplaceCommandException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
