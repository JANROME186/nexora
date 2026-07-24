package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

public class MarketplaceEntityNotFoundException extends RuntimeException {

    private final String code;

    public MarketplaceEntityNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
