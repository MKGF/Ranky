package com.desierto.ranky.infrastructure.exceptions;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException() {
    super("Unauthorized access.");
  }
}
