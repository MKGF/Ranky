package com.desierto.ranky.infrastructure.controller.dto;

import com.desierto.ranky.domain.entity.Account;

public record AccountApi(String name, String tag) {

  public Account toDomain() {
    return new Account(name, tag);
  }
}
