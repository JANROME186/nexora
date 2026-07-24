package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

public class ExternalQualityDomainException extends com.nexora.hop.platformfoundation.externalqualitycompliance.ExternalQualityComplianceException {
    public ExternalQualityDomainException(String message) {
        super(message);
    }
    public ExternalQualityDomainException(String code, String messageKey, String message) {
        super(code, messageKey, message);
    }
}
