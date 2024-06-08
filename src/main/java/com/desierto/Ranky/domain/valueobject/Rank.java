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
  private Division division;

  private int leaguePoints;

  private Winrate winrate;

  public static Rank unranked() {
    return new Rank(UNRANKED, Division.IV, 0, new Winrate());
  }

  @Override
  public int compareTo(Rank rank) {
    if (rank.tier.ordinal() > this.tier.ordinal()) {
      return 1;
    } else if (rank.tier.ordinal() < this.tier.ordinal()) {
      return -1;
    } else if (rank.division.compare(this.division) > 0) {
      return 1;
    } else if (rank.division.compare(this.division) < 0) {
      return -1;
    } else if (rank.leaguePoints > this.leaguePoints) {
      return 1;
    } else if (rank.leaguePoints < this.leaguePoints) {
      return -1;
    } else {
      return rank.winrate.compareTo(this.winrate);
    }
  }

  @Override
  public String toString() {
    return tier.toString() + " " + division.toString() + " " + leaguePoints + "lp | " + "Wins: "
        + winrate.getWins() + " Losses: " + winrate.getLosses() + " | Winrate: "
        + winrate.getPercentage();
  }

  public enum Tier implements ValueEnum<String> {
    UNRANKED("UNRANKED", "<:Unranked:1248786000533262419>"),
    IRON("IRON", "<:Iron:1248780238553612351>"),
    BRONZE("BRONZE", "<:Bronze:1248780338289971240>"),
    SILVER("SILVER", "<:Silver:1248780200271941642>"),
    GOLD("GOLD", "<:Gold:1248780142856241193>"),
    PLATINUM("PLATINUM", "<:Platinum:1248780113013903440>"),
    EMERALD("EMERALD", "<:Emerald:1248781094980161703>"),
    DIAMOND("DIAMOND", "<:Diamond:1248780302873133117>"),
    MASTER("MASTER", "<:Master:1248780273718399038>"),
    GRANDMASTER("GRANDMASTER", "<:Grandmaster:1248780022421131347>"),
    CHALLENGER("CHALLENGER", "<:Challenger:1248780069564977183>");

    private final String value;

    private final String emoji;


    Tier(String value, String emoji) {
      this.value = value;
      this.emoji = emoji;
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

    public String toString() {
      return emoji;
    }
  }
}
