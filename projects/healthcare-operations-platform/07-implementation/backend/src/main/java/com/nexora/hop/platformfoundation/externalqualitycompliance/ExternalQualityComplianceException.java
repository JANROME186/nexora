package com.nexora.hop.platformfoundation.externalqualitycompliance;

public class ExternalQualityComplianceException extends RuntimeException {

    private final String code;
    private final String messageKey;

    public ExternalQualityComplianceException(String message) {
        this("QUALITY_COMPLIANCE_ERROR", "quality.error.generic", message);
    }

    public ExternalQualityComplianceException(String code, String messageKey, String message) {
        super(message);
        this.code = code != null ? code : "QUALITY_COMPLIANCE_ERROR";
        this.messageKey = messageKey != null ? messageKey : "quality.error.generic";
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
