package com.nexora.hop.platformfoundation.publicweb;

import org.springframework.http.HttpStatus;

/**
 * Structured exception carrying an HTTP status, an error {@code code} and an i18n {@code
 * messageKey}. Thrown by public-classified controllers and translated by
 * {@link PublicWebExceptionHandler} into an RFC7807-inspired error envelope identical in shape to
 * the one BCM-PLT-004/005 introduced in MVP-MOD-008-BE-002.
 */
public class PublicWebException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String code;

    public PublicWebException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }
}
