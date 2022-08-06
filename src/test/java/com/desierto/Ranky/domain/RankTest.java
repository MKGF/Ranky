package com.desierto.Ranky.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.Ranky.domain.builder.RankBuilder;
import com.desierto.Ranky.domain.valueobject.Rank;
import com.desierto.Ranky.domain.valueobject.Rank.Tier;
import org.junit.jupiter.api.Test;

public class RankTest {

  @Test
  public void gold_3_should_be_higher_than_silver_1() {
    Rank gold3 = new RankBuilder().tier(Tier.GOLD).division(3).build();
    Rank silver1 = new RankBuilder().tier(Tier.SILVER).division(1).build();

    assertEquals(gold3.compareTo(silver1), -1);
  }

  @Test
  public void gold_3_should_be_even_with_gold_3() {
    Rank gold3 = new RankBuilder().tier(Tier.GOLD).division(3).build();
    Rank gold3_2 = new RankBuilder().tier(Tier.GOLD).division(3).build();

    assertEquals(gold3.compareTo(gold3_2), 0);
  }

  @Test
  public void master_should_be_below_challenger() {
    Rank master = new RankBuilder().tier(Tier.MASTER).division(1).build();
    Rank challenger = new RankBuilder().tier(Tier.CHALLENGER).division(1).build();

    assertEquals(master.compareTo(challenger), 1);
  }


}