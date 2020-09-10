package com.desierto.LoLRankingMaker.domain.valueobject;

import javax.validation.constraints.Max;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AccountInformation {

  Rank rank;
  Winrate winrate;
  @Max(value = 100)
  int leaguePoints;

}
