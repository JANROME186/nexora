package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

/**
 * Event type for chain-of-custody events (BCM-LAB-002, VO-COL-007).
 * Every state transition on a Sample appends one chain-of-custody event.
 */
public enum CustodyEventType {
    collected,
    labeled,
    received,
    rejected,
    in_process,
    disposed
}
