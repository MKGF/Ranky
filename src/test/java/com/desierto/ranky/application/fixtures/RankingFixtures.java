package com.desierto.ranky.application.fixtures;

import com.desierto.ranky.domain.entity.Ranking;
import java.util.List;

public class RankingFixtures {

  public static Ranking aRanking() {
    return new Ranking("rankingId", List.of(AccountFixtures.anAccount()));
  }
}
