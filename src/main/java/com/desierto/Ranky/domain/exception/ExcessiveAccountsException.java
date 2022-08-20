package com.desierto.Ranky.domain.exception;

public class ExcessiveAccountsException extends RuntimeException {

  public ExcessiveAccountsException() {
    super("To introduce more than one account '/addMultiple' should be used instead.");
  }
}
