package com.nexora.hop.platformfoundation.aioverlay.shared;

public class AiOverlayException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final AiOverlayErrorCode errorCode;

    public AiOverlayException(String message, AiOverlayErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiOverlayErrorCode getErrorCode() {
        return errorCode;
    }
}
