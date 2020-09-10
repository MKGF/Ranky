package com.desierto.LoLRankingMaker.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Winrate {

  BigDecimal wins;
  BigDecimal losses;

  public BigDecimal getPercentage() {
    BigDecimal totalGames = wins.add(losses);
    return wins.divide(totalGames).multiply(BigDecimal.valueOf(100))
        .setScale(2, RoundingMode.FLOOR);
  }
}
