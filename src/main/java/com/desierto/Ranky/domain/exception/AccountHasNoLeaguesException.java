package com.desierto.Ranky.domain.exception;

public class AccountHasNoLeaguesException extends RuntimeException {

  public AccountHasNoLeaguesException() {
    super("Account has no rank in any queue");
  }
}
