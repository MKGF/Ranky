package com.desierto.Ranky.infrastructure.dto;

import com.desierto.Ranky.domain.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {

  String id;

  String name;

  String tagLine;

  public static AccountDTO fromDomain(Account account) {
    return new AccountDTO(account.getId(), account.getName(), account.getTagLine());
  }

  public Account toDomain() {
    return new Account(id, name, tagLine);
  }
}
