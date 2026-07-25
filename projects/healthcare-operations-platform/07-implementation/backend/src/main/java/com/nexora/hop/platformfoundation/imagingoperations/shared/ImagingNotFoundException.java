package com.nexora.hop.platformfoundation.imagingoperations.shared;

public class ImagingNotFoundException extends ImagingDomainException {

    public ImagingNotFoundException(ImagingErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
