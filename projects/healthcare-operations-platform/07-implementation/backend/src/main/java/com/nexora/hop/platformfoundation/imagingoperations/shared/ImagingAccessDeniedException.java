package com.nexora.hop.platformfoundation.imagingoperations.shared;

/**
 * Raised when a PATIENT or REFERRING_DOCTOR caller (patient portal / doctor portal, HOP-HARD-APP-001
 * imaging delivery hardening) requests an imaging delivery package or radiology report for a
 * patient they do not own or have not referred, mirroring
 * {@code ResultHistoryAccessDeniedException}'s self-access boundary for laboratory results.
 */
public class ImagingAccessDeniedException extends ImagingDomainException {

    public ImagingAccessDeniedException(ImagingErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
