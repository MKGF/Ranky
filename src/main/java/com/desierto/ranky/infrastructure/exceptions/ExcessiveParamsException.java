package com.desierto.ranky.infrastructure.exceptions;

public class ExcessiveParamsException extends RuntimeException {

  public ExcessiveParamsException() {
    super("Too many parameters introduced!");
  }
}
