package com.desierto.ranky.domain.exception.guild;

import com.desierto.ranky.domain.exception.NotFoundException;

public class GuildNotFoundException extends NotFoundException {

  public GuildNotFoundException(String id) {
    super("Guild with id: " + id + " not found");
  }
}
