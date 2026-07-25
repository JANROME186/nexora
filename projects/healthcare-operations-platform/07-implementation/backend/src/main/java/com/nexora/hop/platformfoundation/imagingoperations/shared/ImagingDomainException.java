package com.nexora.hop.platformfoundation.imagingoperations.shared;

public class ImagingDomainException extends RuntimeException {

    private final ImagingErrorCode errorCode;

    public ImagingDomainException(ImagingErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ImagingErrorCode getErrorCode() {
        return errorCode;
    }
}
