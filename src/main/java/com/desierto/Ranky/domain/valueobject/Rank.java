package com.desierto.Ranky.domain.valueobject;

import static com.desierto.Ranky.domain.valueobject.Rank.Tier.UNRANKED;

import com.desierto.Ranky.domain.converter.ValueEnumConverter;
import com.desierto.Ranky.domain.entity.ValueEnum;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Embeddable
public class Rank implements Comparable<Rank> {

  @Convert(converter = Tier.Converter.class)
  private Tier tier; //Tier
  @Min(0)
  @Max(4)
  private int division; //Division

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

  public enum Tier implements ValueEnum<String> {
    IRON("IRON"), BRONZE("BRONZE"), SILVER("SILVER"), GOLD("GOLD"), PLATINUM("PLATINUM"), DIAMOND(
        "DIAMOND"), MASTER("MASTER"), GRANDMASTER("GRANDMASTER"), CHALLENGER(
        "CHALLENGER"), UNRANKED("UNRANKED");

    private String value;


    Tier(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static class Converter extends ValueEnumConverter<Tier, String> {

      public Converter() {
        super(Tier.class);
      }
    }

  }

  public static Rank unranked() {
    return new Rank(UNRANKED, 0);
  }
}
