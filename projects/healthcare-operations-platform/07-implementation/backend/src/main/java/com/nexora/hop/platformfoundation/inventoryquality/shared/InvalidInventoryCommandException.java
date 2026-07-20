package com.nexora.hop.platformfoundation.inventoryquality.shared;

/** Signals an inventory-quality command failed input or precondition validation. */
public class InvalidInventoryCommandException extends RuntimeException {

  private final String code;

  public InvalidInventoryCommandException(String message, String code) {
    super(message);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
