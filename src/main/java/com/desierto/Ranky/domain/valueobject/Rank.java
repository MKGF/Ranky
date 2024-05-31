package com.desierto.Ranky.domain.valueobject;

import static com.desierto.Ranky.domain.valueobject.Rank.Tier.UNRANKED;

import com.desierto.Ranky.domain.entity.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Rank implements Comparable<Rank> {

  private Tier tier;
  private int division;

  private int leaguePoints;

  private Winrate winrate;

  public static Rank unranked() {
    return new Rank(UNRANKED, 0, 0, new Winrate());
  }

  @Override
  public int compareTo(Rank rank) {
    if (rank.tier.ordinal() > this.tier.ordinal()) {
      return 1;
    } else if (rank.tier.ordinal() < this.tier.ordinal()) {
      return -1;
    } else if (rank.division > this.division) {
      return 1;
    } else if (rank.division < this.division) {
      return -1;
    } else if (rank.leaguePoints > this.leaguePoints) {
      return 1;
    } else if (rank.leaguePoints < this.leaguePoints) {
      return -1;
    } else {
      return rank.winrate.getPercentage().compareTo(this.winrate.getPercentage());
    }
  }

  @Override
  public String toString() {
    return tier.toString() + " " + division + " " + leaguePoints + "lp\n" + "Wins: "
        + winrate.getWins() + " Losses: " + winrate.getLosses() + " Ratio: "
        + winrate.getPercentage();
  }

  public enum Tier implements ValueEnum<String> {
    UNRANKED("UNRANKED"), IRON("IRON"), BRONZE("BRONZE"), SILVER("SILVER"), GOLD("GOLD"), PLATINUM(
        "PLATINUM"), EMERALD("EMERALD"), DIAMOND(
        "DIAMOND"), MASTER("MASTER"), GRANDMASTER("GRANDMASTER"), CHALLENGER(
        "CHALLENGER");

    private final String value;


    Tier(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static Tier fromString(String s) {
      for (Tier tier :
          Tier.values()) {
        if (tier.value.equalsIgnoreCase(s)) {
          return tier;
        }
      }
      return null;
    }

    public static class Converter {

      public Converter() {

      }
    }

  }
}
