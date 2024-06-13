package com.desierto.Ranky.domain.valueobject;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Winrate implements Comparable<Winrate> {

  private Integer wins;
  private Integer losses;

  public BigDecimal getPercentage() {
    int totalGames = wins + losses;
    if (totalGames == 0) {
      return ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    return valueOf(wins)
        .divide(valueOf(totalGames), 4, RoundingMode.HALF_UP)
        .multiply(valueOf(100)).setScale(2, RoundingMode.HALF_UP);
  }

  public static Winrate unranked() {
    return new Winrate(0, 0);
  }

  @Override
  public int compareTo(@NotNull Winrate another) {
    return this.getPercentage().compareTo(another.getPercentage());
  }

  @Override
  public String toString() {
    return "Wins: " + wins + " Losses: " + losses + " Winrate: " + getPercentage() + "%";
  }
}
