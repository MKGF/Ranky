package com.desierto.Ranky.domain.valueobject;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class Winrate {

  Integer wins;
  Integer losses;

  public BigDecimal getPercentage() {
    Integer totalGames = wins + losses;
    if (totalGames == 0) {
      return ZERO;
    }
    return valueOf(wins)
        .divide(valueOf(totalGames), 4, RoundingMode.HALF_UP)
        .multiply(valueOf(100)).setScale(2, RoundingMode.HALF_UP);
  }

  public static Winrate unranked() {
    return new Winrate(0, 0);
  }
}
