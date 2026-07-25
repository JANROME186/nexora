package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.time.Instant;
import java.util.List;

/**
 * Laboratory result aggregate root (AGG-009, BCM-LAB-006).
 *
 * <p>This capability (BCM-LAB-006) creates the aggregate and captures its value.
 * BCM-LAB-008 holds delegated authority to mutate {@code technicalValidation} and {@code criticalFlag}.
 * BCM-LAB-009 holds delegated authority to mutate {@code medicalValidation}.
 * BCM-LAB-010 holds delegated authority to mutate {@code releaseRecord} and {@code amendments}.
 * No other capability may write LaboratoryResult persistence directly (INV-LPR-003).
 *
 * <p>Invariants from business-model.md:
 * <ul>
 *   <li>INV-LPR-001: Cannot be created without a received Sample, AnalyteSnapshot and ReferenceRangeSnapshot.</li>
 *   <li>INV-LPR-002: Cannot exist for a rejected Sample.</li>
 *   <li>INV-LPR-003: Only BCM-LAB-006/008/009/010 may mutate; AI capabilities may not validate/release/amend.</li>
 *   <li>INV-LPR-004: A released result is immutable except through a ResultAmendment.</li>
 *   <li>INV-LPR-005: Device-sourced values must reference a normalized BCM-PLT-004 message.</li>
 * </ul>
 */
public record LaboratoryResult(
        String resultId,
        String tenantId,
        String laboratoryId,
        String branchId,
        String orderId,
        String sampleId,
        AnalyteSnapshot analyteSnapshot,
        ReferenceRangeSnapshot referenceRangeSnapshot,
        ResultValue resultValue,
        CaptureSource captureSource,
        List<ProcessingIncident> processingIncidents,
        TechnicalValidationRecord technicalValidation,
        CriticalResultFlag criticalFlag,
        MedicalValidationRecord medicalValidation,
        ResultReleaseRecord releaseRecord,
        List<ResultAmendment> amendments,
        ResultStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
