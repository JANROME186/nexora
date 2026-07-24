package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

public class MarketplaceConflictException extends RuntimeException {

    private final String code;

    public MarketplaceConflictException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
