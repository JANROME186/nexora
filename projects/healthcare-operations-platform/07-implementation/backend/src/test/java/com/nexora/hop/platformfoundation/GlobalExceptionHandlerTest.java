package com.nexora.hop.platformfoundation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsInvalidParameterExceptionToBadRequestBody() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/platform/tenants");

        ResponseEntity<Object> response =
                handler.handleInvalidParameterException(
                        new IllegalArgumentException("bad param"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo("Malformed or invalid request parameter");
        assertThat(body.get("path")).isEqualTo("/api/platform/tenants");
        assertThat(body.get("timestamp")).isNotNull();
    }

    @Test
    void mapsTomcatInvalidParameterExceptionToBadRequestBody() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/results/delivery/authorize");

        ResponseEntity<Object> response =
                handler.handleInvalidParameterException(
                        new org.apache.tomcat.util.http.InvalidParameterException("bad"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body.get("path")).isEqualTo("/api/results/delivery/authorize");
    }

    @Test
    void mapsInvalidByteSequenceDataIntegrityViolationToBadRequestBody() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false))
                .thenReturn("uri=/api/clinical-operations/samples/collection-worklist");
        SQLException invalidByteSequence =
                new SQLException("invalid byte sequence for encoding \"UTF8\": 0x00", "22021");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("PreparedStatementCallback", invalidByteSequence);

        ResponseEntity<Object> response = handler.handleDataIntegrityViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.get("path")).isEqualTo("/api/clinical-operations/samples/collection-worklist");
    }

    @Test
    void mapsStringDataRightTruncationDataIntegrityViolationToBadRequestBody() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/revenue/cashier/sessions");
        SQLException valueTooLong =
                new SQLException("value too long for type character varying(36)", "22001");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("PreparedStatementCallback", valueTooLong);

        ResponseEntity<Object> response = handler.handleDataIntegrityViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.get("path")).isEqualTo("/api/revenue/cashier/sessions");
    }

    @Test
    void rethrowsOtherDataIntegrityViolationCausesUnchanged() {
        WebRequest request = mock(WebRequest.class);
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "unique constraint violated", new SQLException("duplicate key", "23505"));

        assertThatThrownBy(() -> handler.handleDataIntegrityViolationException(ex, request))
                .isSameAs(ex);
    }
}
