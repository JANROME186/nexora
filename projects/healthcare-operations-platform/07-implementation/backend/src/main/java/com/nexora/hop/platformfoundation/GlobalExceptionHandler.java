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

    @ExceptionHandler({
            org.apache.tomcat.util.http.InvalidParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Object> handleInvalidParameterException(Exception ex, WebRequest request) {
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
