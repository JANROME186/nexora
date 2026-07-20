package com.nexora.hop.platformfoundation.inventoryquality.shared;

/** Signals a domain entity in the inventory-quality module was not found. */
public class InventoryEntityNotFoundException extends RuntimeException {

  private final String code;

  public InventoryEntityNotFoundException(String message, String code) {
    super(message);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
