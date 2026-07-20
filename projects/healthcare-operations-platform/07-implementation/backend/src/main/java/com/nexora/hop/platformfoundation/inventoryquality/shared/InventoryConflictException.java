package com.nexora.hop.platformfoundation.inventoryquality.shared;

/** Signals a state-machine or lifecycle guard prevents an inventory-quality command. */
public class InventoryConflictException extends RuntimeException {

  private final String code;

  public InventoryConflictException(String message, String code) {
    super(message);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
