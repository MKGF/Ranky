package com.desierto.ranky.infrastructure.dto;

import com.desierto.ranky.domain.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDto {

  String id;

  public static AccountDto fromDomain(Account account) {
    return new AccountDto(account.getId());
  }

  public Account toDomain() {
    return new Account(id);
  }
}
