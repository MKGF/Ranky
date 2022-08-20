package com.desierto.Ranky.domain.exception;

public class ExcessiveParamsException extends RuntimeException {

  public ExcessiveParamsException() {
    super("Too many parameters introduced!");
  }
}
