package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.application;

/**
 * Raised when a referring doctor (COM-MOD-009-PORTAL-002 doctor portal) requests a patient's
 * result history for a patient they have not referred. Carries
 * {@code ResultsDeliveryErrorCodes.DELIVERY_DOCTOR_REFERRAL_MISMATCH}, the error code already
 * modeled for this scenario, as the message prefix so API consumers can branch on a stable
 * identifier instead of parsing prose (HOP-QA-ALIGN-005 message externalization baseline).
 */
public class ResultHistoryAccessDeniedException extends RuntimeException {

    public ResultHistoryAccessDeniedException(String message) {
        super(message);
    }
}
