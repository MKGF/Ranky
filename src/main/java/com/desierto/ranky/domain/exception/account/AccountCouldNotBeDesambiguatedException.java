package com.desierto.ranky.domain.exception.account;

import com.desierto.ranky.domain.entity.Account;

public class AccountCouldNotBeDesambiguatedException extends RuntimeException {

  public AccountCouldNotBeDesambiguatedException(Account account) {
    super(
        String.format(
            "Account %s can't be desambiguated, there is more than one account named like that. Please add the tag to confirm which account you want to remove.",
            account.getName()
        )
    );
  }
}
