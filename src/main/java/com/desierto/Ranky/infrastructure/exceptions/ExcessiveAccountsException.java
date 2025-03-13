package com.desierto.Ranky.infrastructure.exceptions;

public class ExcessiveAccountsException extends RuntimeException {

  public ExcessiveAccountsException() {
    super("To introduce more than one account '/addMultiple' should be used instead.");
  }
}
