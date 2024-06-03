package com.desierto.Ranky.domain;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.desierto.Ranky.domain.valueobject.Winrate;
import org.junit.jupiter.api.Test;

public class WinrateTest extends BaseTest {

  @Test
  public void givenSomeWinsAndLosses_calculatesPercentage() {
    Winrate winrate = Winrate.builder().wins(130).losses(
        126).build();

    assertEquals(valueOf(50.78), winrate.getPercentage());
  }

}
