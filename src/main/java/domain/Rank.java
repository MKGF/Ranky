package domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Rank implements Comparable<Rank> {

  @Override
  public int compareTo(Rank rank) {
    if (rank.tier.ordinal() > this.tier.ordinal()) {
      return 1;
    }
    if (rank.tier.ordinal() < this.tier.ordinal()) {
      return -1;
    }
    return Integer.compare(this.division, rank.division);
  }

  public enum Tier {
    IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER;

  }

  private Tier tier;
  @Min(0)
  @Max(4)
  private int division;

}
