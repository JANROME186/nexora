package com.nexora.hop.platformfoundation.inventoryquality.shared;

import java.time.Instant;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Shared exception mapping for the nine BCM-INV controllers compiled in COM-MOD-010-BE-001.
 * Every response carries a first-class {@code code} field (RFC7807-inspired, per each
 * capability's {@code openapi-source.md error_model}) plus a {@code messageKey}
 * ({@code inventory.error.<code, lowercase>}) so a client can resolve a localized message
 * independently of the always-English {@code message} field. Mirrors the convention adopted by
 * BCM-PLT-004/005/010, further reducing TD-I18N-002.
 */
@RestControllerAdvice(basePackages = "com.nexora.hop.platformfoundation.inventoryquality")
public class InventoryExceptionHandler {

  @ExceptionHandler(InventoryEntityNotFoundException.class)
  ResponseEntity<InventoryApiErrorResponse> notFound(InventoryEntityNotFoundException exception) {
    return error(HttpStatus.NOT_FOUND, exception.code(), exception.getMessage());
  }

  @ExceptionHandler({InvalidInventoryCommandException.class, MethodArgumentNotValidException.class})
  ResponseEntity<InventoryApiErrorResponse> badRequest(Exception exception) {
    String code =
        exception instanceof InvalidInventoryCommandException invalid
            ? invalid.code()
            : InventoryErrorCodes.INVENTORY_COMMAND_INVALID;
    String message =
        exception instanceof InvalidInventoryCommandException invalid
            ? invalid.getMessage()
            : "Inventory command is invalid.";
    return error(HttpStatus.BAD_REQUEST, code, message);
  }

  @ExceptionHandler(InventoryConflictException.class)
  ResponseEntity<InventoryApiErrorResponse> conflict(InventoryConflictException exception) {
    return error(HttpStatus.CONFLICT, exception.code(), exception.getMessage());
  }

  private static ResponseEntity<InventoryApiErrorResponse> error(
      HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(
            new InventoryApiErrorResponse(
                status.value(), code, messageKeyFor(code), message, Instant.now()));
  }

  /** Deterministic catalog-key naming convention: {@code inventory.error.<code, lowercase>}. */
  static String messageKeyFor(String code) {
    return "inventory.error." + code.toLowerCase(Locale.ROOT);
  }

  public record InventoryApiErrorResponse(
      int status, String code, String messageKey, String message, Instant occurredAt) {}
}
