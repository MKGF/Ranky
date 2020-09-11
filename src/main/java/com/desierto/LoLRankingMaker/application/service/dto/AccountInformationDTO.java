package com.desierto.LoLRankingMaker.application.service.dto;

import com.desierto.LoLRankingMaker.domain.valueobject.AccountInformation;
import com.desierto.LoLRankingMaker.domain.valueobject.Rank;
import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountInformationDTO {

  Rank rank;
  Winrate winrate;
  int leaguePoints;

  public static AccountInformationDTO fromDomain(AccountInformation accountInformation) {
    return new AccountInformationDTO(accountInformation.getRank(), accountInformation.getWinrate(),
        accountInformation.getLeaguePoints());
  }
}
