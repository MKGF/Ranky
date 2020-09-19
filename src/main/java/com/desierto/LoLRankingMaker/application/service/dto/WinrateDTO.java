package com.desierto.LoLRankingMaker.application.service.dto;

import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import java.math.BigDecimal;
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
public class WinrateDTO {

  Integer wins;
  Integer losses;
  BigDecimal percentage;

  public static WinrateDTO fromDomain(Winrate winrate) {
    return new WinrateDTO(winrate.getWins(), winrate.getLosses(), winrate.getPercentage());
  }
}
