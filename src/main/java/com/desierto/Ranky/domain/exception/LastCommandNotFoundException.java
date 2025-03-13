package com.desierto.Ranky.domain.exception;

public class LastCommandNotFoundException extends NotFoundException {

  public LastCommandNotFoundException() {
    super("You don't have a last command yet. Type in a command to have one.");
  }
}
