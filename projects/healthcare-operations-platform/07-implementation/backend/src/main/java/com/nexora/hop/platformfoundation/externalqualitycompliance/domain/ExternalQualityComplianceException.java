package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

public class ExternalQualityComplianceException extends com.nexora.hop.platformfoundation.externalqualitycompliance.ExternalQualityComplianceException {
    public ExternalQualityComplianceException(String message) {
        super(message);
    }
    public ExternalQualityComplianceException(String code, String messageKey, String message) {
        super(code, messageKey, message);
    }
}
