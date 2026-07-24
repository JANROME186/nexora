package com.nexora.hop.platformfoundation;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * PostgreSQL SQLSTATE class 22 (Data Exception) codes that indicate the client supplied
     * malformed or oversized data, not a server-side or constraint-conflict fault:
     * 22021 invalid_byte_sequence_for_encoding (e.g. a null byte in a string), 22001
     * string_data_right_truncation (a value longer than the column's declared length).
     */
    private static final Set<String> CLIENT_DATA_EXCEPTION_SQLSTATES = Set.of("22021", "22001");

    @ExceptionHandler(com.nexora.hop.platformfoundation.externalqualitycompliance.ExternalQualityComplianceException.class)
    public ResponseEntity<Object> handleExternalQualityComplianceException(
            com.nexora.hop.platformfoundation.externalqualitycompliance.ExternalQualityComplianceException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        HttpStatus status = ex.getCode().contains("NOT_FOUND") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        body.put("status", status.value());
        body.put("code", ex.getCode());
        body.put("messageKey", ex.getMessageKey());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException.class)
    public ResponseEntity<Object> handleDocumentManagementException(
            com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        HttpStatus status = ex.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        body.put("status", status.value());
        body.put("code", "DOCUMENT_MANAGEMENT_ERROR");
        body.put("messageKey", "document.error.generic");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler({
            org.apache.tomcat.util.http.InvalidParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Object> handleInvalidParameterException(Exception ex, WebRequest request) {
        return badRequestResponse(ex, request);
    }

    /**
     * A multipart upload (e.g. POST /api/documents) whose body is malformed, truncated by an
     * abrupt client disconnect mid-stream, or exceeds the configured size limit fails while Spring
     * is still parsing the request, before any controller or business logic runs. That is always a
     * client-input/transport problem, not a server fault, so it is remapped to 400 instead of the
     * default 500 (found by an OWASP ZAP Buffer Overflow active-scan probe against
     * DocumentManagementController.uploadDocument).
     */
    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(
            org.springframework.web.multipart.MultipartException ex, WebRequest request) {
        return badRequestResponse(ex, request);
    }

    /**
     * A request field or query parameter containing malformed or oversized data (e.g. a null byte,
     * or a value longer than a column's declared length) is structurally valid Java input but is
     * rejected by PostgreSQL's own data validation once it reaches a JDBC statement, surfacing here
     * as a DataIntegrityViolationException. That is a client-input problem, not a server fault, so
     * it is remapped to 400. Every other DataIntegrityViolationException cause (e.g. a real
     * unique-constraint conflict) is intentionally left unhandled by this method so it falls
     * through to whatever conflict handling (or default 500) already applies elsewhere.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        if (!isClientDataException(ex)) {
            throw ex;
        }
        return badRequestResponse(ex, request);
    }

    private ResponseEntity<Object> badRequestResponse(Exception ex, WebRequest request) {
        if (logger.isWarnEnabled()) {
            logger.warn("Invalid parameter detected: {}", ex.getMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Malformed or invalid request parameter");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private boolean isClientDataException(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException
                    && CLIENT_DATA_EXCEPTION_SQLSTATES.contains(sqlException.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}
