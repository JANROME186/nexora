package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

/**
 * Laboratory result lifecycle status (AGG-009, BCM-LAB-006).
 * Source model: bcm-lab-006-laboratory-processing/business-model.yaml VO-LPR-010.
 */
public enum ResultStatus {
    captured,
    pending_technical_validation,
    technically_validated,
    pending_medical_validation,
    medically_validated,
    released,
    amended
}
