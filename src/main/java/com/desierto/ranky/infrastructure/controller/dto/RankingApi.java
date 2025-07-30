package com.desierto.ranky.infrastructure.controller.dto;

import com.desierto.ranky.domain.entity.Ranking;

public record RankingApi(String name, Integer amountOfAccounts) {

  public static RankingApi fromDomain(Ranking ranking) {
    return new RankingApi(ranking.getId(), ranking.getAccounts().size());
  }

}
