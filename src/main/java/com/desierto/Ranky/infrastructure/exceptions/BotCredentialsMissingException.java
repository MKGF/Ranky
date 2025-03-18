package com.desierto.Ranky.infrastructure.exceptions;

public class BotCredentialsMissingException extends RuntimeException {

  public BotCredentialsMissingException(String message) {
    super(message);
  }
}
