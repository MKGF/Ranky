package com.desierto.ranky.infrastructure.exceptions;

public class BotCredentialsMissingException extends RuntimeException {

  public BotCredentialsMissingException(String message) {
    super(message);
  }
}
