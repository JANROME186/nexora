package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain;

import java.time.LocalDateTime;

public record DeliveryAuthorizationCheck(
        boolean resultReleased,
        boolean recipientOwnershipOrReferralVerified,
        boolean representativeAuthorizationValid,
        LocalDateTime checkedAt) {
}
