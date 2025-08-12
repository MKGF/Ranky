package com.desierto.ranky.infrastructure.exceptions;

import com.desierto.ranky.domain.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {

  public RoleNotFoundException() {
    super("Power role not found for the user");
  }
}
