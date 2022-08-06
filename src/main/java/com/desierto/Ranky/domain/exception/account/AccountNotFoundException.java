package com.desierto.Ranky.domain.exception.account;

import com.desierto.Ranky.domain.exception.NotFoundException;

public class AccountNotFoundException extends NotFoundException {

  public AccountNotFoundException(Long id) {
    super("Account with id: " + id + " not found");
  }

  public AccountNotFoundException(String name) {
    super("Account with name: " + name + " not found");
  }

}
