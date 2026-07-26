package com.nexora.hop.platformfoundation.aioverlay.shared;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.aioverlay")
public class AiOverlayExceptionHandler {

    private final MessageSource messageSource;

    AiOverlayExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AiOverlayNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleNotFound(AiOverlayNotFoundException ex, Locale locale) {
        return response(ex, locale, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AiOverlayException.class)
    ResponseEntity<Map<String, Object>> handleDomainException(AiOverlayException ex, Locale locale) {
        HttpStatus status = ex.getErrorCode() == AiOverlayErrorCode.AI_POLICY_BLOCKED
                ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return response(ex, locale, status);
    }

    private ResponseEntity<Map<String, Object>> response(
            AiOverlayException ex, Locale locale, HttpStatus status) {
        String message = messageSource.getMessage(
                ex.getErrorCode().getMessageKey(), null, ex.getMessage(), locale);
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "code", ex.getErrorCode().name(),
                "messageKey", ex.getErrorCode().getMessageKey(),
                "message", message));
    }
}
