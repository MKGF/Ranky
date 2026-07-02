package com.desierto.ranky.infrastructure.controller.dto;

import com.desierto.ranky.domain.entity.Account;
import com.fasterxml.jackson.annotation.JsonAlias;

public record AccountApi(String name, @JsonAlias("tag") String tagLine) {

  public Account toDomain() {
    return new Account(name, tagLine);
  }
}
