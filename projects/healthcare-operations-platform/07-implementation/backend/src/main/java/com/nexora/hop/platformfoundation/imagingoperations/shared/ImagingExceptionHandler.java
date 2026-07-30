package com.nexora.hop.platformfoundation.imagingoperations.shared;

import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.imagingoperations")
public class ImagingExceptionHandler {

    private final MessageSource messageSource;

    public ImagingExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ImagingNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ImagingNotFoundException ex, Locale locale) {
        String localizedMessage = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, ex.getMessage(), locale);
        Map<String, Object> body = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "code", ex.getErrorCode().name(),
                "messageKey", ex.getErrorCode().getMessageKey(),
                "message", localizedMessage
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ImagingAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(ImagingAccessDeniedException ex, Locale locale) {
        String localizedMessage = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, ex.getMessage(), locale);
        Map<String, Object> body = Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "code", ex.getErrorCode().name(),
                "messageKey", ex.getErrorCode().getMessageKey(),
                "message", localizedMessage
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ImagingDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(ImagingDomainException ex, Locale locale) {
        String localizedMessage = messageSource.getMessage(ex.getErrorCode().getMessageKey(), null, ex.getMessage(), locale);
        Map<String, Object> body = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "code", ex.getErrorCode().name(),
                "messageKey", ex.getErrorCode().getMessageKey(),
                "message", localizedMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
