package com.desierto.Ranky.domain.builder;

import static com.desierto.Ranky.domain.valueobject.Rank.Tier.IRON;

import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;

public class RankBuilder {

  private Tier tier;
  private Integer division;

  public RankBuilder() {
    tier = IRON;
    division = 4;
  }

  public RankBuilder tier(Tier tier) {
    this.tier = tier;
    return this;
  }

  public RankBuilder division(Integer division) {
    this.division = division;
    return this;
  }

  public Rank build() {
    return new Rank(this.tier, this.division);
  }
}
