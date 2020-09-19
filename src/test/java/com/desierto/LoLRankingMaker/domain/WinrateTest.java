package com.desierto.LoLRankingMaker.domain;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.LoLRankingMaker.domain.valueobject.Winrate;
import org.junit.jupiter.api.Test;

public class WinrateTest {

  @Test
  public void givenSomeWinsAndLosses_calculatesPercentage() {
    Winrate winrate = Winrate.builder().wins(130).losses(
        126).build();

    assertEquals(valueOf(50.78), winrate.getPercentage());
  }

}
