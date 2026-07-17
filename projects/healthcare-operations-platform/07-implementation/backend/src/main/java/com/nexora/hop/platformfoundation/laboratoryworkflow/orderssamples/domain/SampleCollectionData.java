package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.time.Instant;

/**
 * Data captured at the moment of physical sample collection (BCM-LAB-002, VO-COL-003).
 */
public record SampleCollectionData(
        String collectorId,
        String collectionSite,
        CollectionMethod collectionMethod,
        String containerUsed,
        Instant collectedAt,
        PatientConditionAtCollection patientConditionAtCollection) {
}
