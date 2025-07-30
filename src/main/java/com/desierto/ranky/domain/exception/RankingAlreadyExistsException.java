package com.desierto.ranky.domain.exception;

public class RankingAlreadyExistsException extends RuntimeException {

  public RankingAlreadyExistsException() {
    super("A ranking with the same name already exists. Please choose a different one.");
  }
}
