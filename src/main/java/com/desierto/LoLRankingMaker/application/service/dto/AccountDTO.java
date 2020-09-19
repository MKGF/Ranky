package com.desierto.LoLRankingMaker.application.service.dto;

import com.desierto.LoLRankingMaker.domain.entity.Account;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AccountDTO {

  int id;
  String name;
  AccountInformationDTO accountInformation;

  public static AccountDTO fromDomain(Account account) {
    return new AccountDTO(account.getId(), account.getName(),
        AccountInformationDTO.fromDomain(account.getAccountInformation()));
  }
}
