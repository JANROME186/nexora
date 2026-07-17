package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

/**
 * Named error code constants for Laboratory Workflow domain errors.
 * Mirrors the error_model declared in openapi-source.yaml for each capability package.
 * Named constants prevent magic strings across the module (HOP-QA-ALIGN-005 compliance).
 */
public final class LabWorkflowErrorCodes {

    // Sample Collection (BCM-LAB-002)
    public static final String SAMPLE_ORDER_LINE_NOT_ACCEPTED = "SAMPLE_ORDER_LINE_NOT_ACCEPTED";
    public static final String SAMPLE_SNAPSHOT_INCOMPLETE = "SAMPLE_SNAPSHOT_INCOMPLETE";
    public static final String SAMPLE_NOT_IDENTIFIABLE = "SAMPLE_NOT_IDENTIFIABLE";
    public static final String SAMPLE_CUSTODY_EVENT_MISSING = "SAMPLE_CUSTODY_EVENT_MISSING";
    public static final String SAMPLE_REJECTION_REASON_REQUIRED = "SAMPLE_REJECTION_REASON_REQUIRED";
    public static final String SAMPLE_BOUNDARY_VIOLATION = "SAMPLE_BOUNDARY_VIOLATION";
    public static final String SAMPLE_SCOPE_MISMATCH = "SAMPLE_SCOPE_MISMATCH";

    // Sample Labeling (BCM-LAB-003)
    public static final String LABEL_SAMPLE_STATUS_INVALID = "LABEL_SAMPLE_STATUS_INVALID";
    public static final String LABEL_MISMATCH_DETECTED = "LABEL_MISMATCH_DETECTED";
    public static final String LABEL_BOUNDARY_VIOLATION = "LABEL_BOUNDARY_VIOLATION";
    public static final String LABEL_SCOPE_MISMATCH = "LABEL_SCOPE_MISMATCH";
    public static final String LABEL_RELABEL_OVERRIDE_REQUIRED = "LABEL_RELABEL_OVERRIDE_REQUIRED";

    // Sample Reception (BCM-LAB-005)
    public static final String SAMPLE_UNLABELED_AT_RECEPTION = "SAMPLE_UNLABELED_AT_RECEPTION";
    public static final String SAMPLE_CONDITION_CHECK_FAILED = "SAMPLE_CONDITION_CHECK_FAILED";
    public static final String SAMPLE_TERMINAL_STATE_VIOLATION = "SAMPLE_TERMINAL_STATE_VIOLATION";
    public static final String SAMPLE_DISPOSAL_EVIDENCE_PRESERVATION_VIOLATION =
            "SAMPLE_DISPOSAL_EVIDENCE_PRESERVATION_VIOLATION";

    // Laboratory Processing (BCM-LAB-006)
    public static final String RESULT_SAMPLE_NOT_RECEIVED = "RESULT_SAMPLE_NOT_RECEIVED";
    public static final String RESULT_SNAPSHOT_INCOMPLETE = "RESULT_SNAPSHOT_INCOMPLETE";
    public static final String RESULT_VALUE_IMPLAUSIBLE = "RESULT_VALUE_IMPLAUSIBLE";
    public static final String RESULT_RAW_DEVICE_PROTOCOL_REJECTED = "RESULT_RAW_DEVICE_PROTOCOL_REJECTED";
    public static final String RESULT_UNRESOLVED_INCIDENT = "RESULT_UNRESOLVED_INCIDENT";
    public static final String RESULT_BOUNDARY_VIOLATION = "RESULT_BOUNDARY_VIOLATION";
    public static final String RESULT_SCOPE_MISMATCH = "RESULT_SCOPE_MISMATCH";

    // Technical Validation (BCM-LAB-008)
    public static final String RESULT_UNRESOLVED_INCIDENT_BLOCKS_VALIDATION =
            "RESULT_UNRESOLVED_INCIDENT_BLOCKS_VALIDATION";
    public static final String RESULT_SEGREGATION_OF_DUTIES_VIOLATION =
            "RESULT_SEGREGATION_OF_DUTIES_VIOLATION";
    public static final String RESULT_CRITICAL_FLAG_REQUIRED = "RESULT_CRITICAL_FLAG_REQUIRED";
    public static final String RESULT_CRITICAL_NOTIFICATION_TRACE_MISSING =
            "RESULT_CRITICAL_NOTIFICATION_TRACE_MISSING";

    // Medical Validation (BCM-LAB-009)
    public static final String RESULT_TECHNICAL_VALIDATION_REQUIRED =
            "RESULT_TECHNICAL_VALIDATION_REQUIRED";
    public static final String RESULT_UNVERIFIED_CREDENTIAL = "RESULT_UNVERIFIED_CREDENTIAL";
    public static final String RESULT_AI_VALIDATION_FORBIDDEN = "RESULT_AI_VALIDATION_FORBIDDEN";

    // Result Release (BCM-LAB-010)
    public static final String RESULT_MEDICAL_VALIDATION_REQUIRED = "RESULT_MEDICAL_VALIDATION_REQUIRED";
    public static final String RESULT_LINKED_SAMPLE_REJECTED = "RESULT_LINKED_SAMPLE_REJECTED";
    public static final String RESULT_RELEASED_VALUE_IMMUTABLE = "RESULT_RELEASED_VALUE_IMMUTABLE";
    public static final String RESULT_AMENDMENT_UNAUTHORIZED_OR_UNREASONED =
            "RESULT_AMENDMENT_UNAUTHORIZED_OR_UNREASONED";

    // Common
    public static final String SAMPLE_NOT_FOUND = "SAMPLE_NOT_FOUND";
    public static final String RESULT_NOT_FOUND = "RESULT_NOT_FOUND";

    private LabWorkflowErrorCodes() {
    }
}
