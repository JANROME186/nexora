package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

/**
 * Sample lifecycle status (AGG-008, BCM-LAB-002).
 * Source model: bcm-lab-002-sample-collection/business-model.yaml VO-COL-008.
 */
public enum SampleStatus {
    collected,
    labeled,
    in_transit,
    received,
    rejected,
    in_process,
    disposed
}
