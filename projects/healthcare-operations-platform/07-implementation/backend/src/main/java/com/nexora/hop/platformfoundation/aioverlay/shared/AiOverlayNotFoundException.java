package com.nexora.hop.platformfoundation.aioverlay.shared;

public class AiOverlayNotFoundException extends AiOverlayException {

    private static final long serialVersionUID = 1L;

    public AiOverlayNotFoundException(String message) {
        super(message, AiOverlayErrorCode.AI_SESSION_NOT_FOUND);
    }
}
