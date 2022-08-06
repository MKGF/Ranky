package com.desierto.Ranky.application.service.dto;

import com.desierto.Ranky.domain.valueobject.AccountInformation;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class AccountInformationDTO {

  RankDTO rank;
  WinrateDTO winrate;
  int leaguePoints;
  String queueType;

  public static AccountInformationDTO fromDomain(AccountInformation accountInformation) {
    return new AccountInformationDTO(RankDTO.fromDomain(accountInformation.getRank()),
        WinrateDTO.fromDomain(accountInformation.getWinrate()),
        accountInformation.getLeaguePoints(), accountInformation.getQueueType());
  }
}
