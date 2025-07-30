package com.desierto.ranky.infrastructure.exceptions;

public class ExcessiveAccountsException extends RuntimeException {

  public ExcessiveAccountsException() {
    super("To introduce more than one account '/addMultiple' should be used instead.");
  }
}
