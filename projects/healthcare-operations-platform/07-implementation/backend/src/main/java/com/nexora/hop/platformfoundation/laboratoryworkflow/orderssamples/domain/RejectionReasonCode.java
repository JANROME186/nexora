package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

/**
 * Structured rejection reason codes (BCM-LAB-002, VO-COL-006).
 * Used for both collection-time and reception-time rejection.
 */
public enum RejectionReasonCode {
    hemolysis,
    insufficient_volume,
    wrong_container,
    unlabeled,
    clotted,
    contaminated,
    expired_transport_window,
    patient_refused,
    other
}
