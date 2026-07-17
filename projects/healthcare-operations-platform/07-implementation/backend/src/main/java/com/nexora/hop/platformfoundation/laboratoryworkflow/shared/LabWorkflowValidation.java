package com.nexora.hop.platformfoundation.laboratoryworkflow.shared;

/**
 * Shared validation helpers for Laboratory Workflow commands and service methods.
 */
public final class LabWorkflowValidation {

    private LabWorkflowValidation() {
    }

    public static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidLabWorkflowCommandException(message);
        }
        return value.strip();
    }

    public static <T> T requiredObject(T value, String message) {
        if (value == null) {
            throw new InvalidLabWorkflowCommandException(message);
        }
        return value;
    }

    public static String optionalText(String value) {
        return (value == null || value.isBlank()) ? null : value.strip();
    }
}
