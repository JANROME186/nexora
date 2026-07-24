package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.shared;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceExceptionHandler;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceExceptionHandler.MarketplaceApiErrorResponse;

/** Maps {@link BillingAdapterException} onto the shared marketplace error envelope (409/503). */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter")
public class BillingAdapterExceptionHandler {

    @ExceptionHandler(BillingAdapterException.class)
    ResponseEntity<MarketplaceApiErrorResponse> adapterUnavailable(BillingAdapterException exception) {
        HttpStatus status = MarketplaceErrorCodes.PROVIDER_ADAPTER_UNAVAILABLE.equals(exception.canonicalErrorCode())
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new MarketplaceApiErrorResponse(
                status.value(), exception.canonicalErrorCode(),
                MarketplaceExceptionHandler.messageKeyFor(exception.canonicalErrorCode()),
                exception.getMessage(), Instant.now()));
    }
}
