package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

/**
 * Condition observed at the time of laboratory reception (BCM-LAB-005, VO-COL-005).
 */
public enum ReceptionCondition {
    acceptable,
    hemolyzed,
    insufficient_volume,
    wrong_container,
    unlabeled,
    clotted,
    other
}
