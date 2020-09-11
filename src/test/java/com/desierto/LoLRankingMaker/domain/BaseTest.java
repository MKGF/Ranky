package com.desierto.LoLRankingMaker.domain;

import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

  @BeforeAll
  static void setUp() {
    System.setProperty("api.key", "none");
    System.setProperty("riot.base.url", "none");
  }

}
