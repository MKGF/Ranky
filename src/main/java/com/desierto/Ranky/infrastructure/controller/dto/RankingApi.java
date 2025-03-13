package com.desierto.Ranky.infrastructure.controller.dto;

import com.desierto.Ranky.domain.entity.Ranking;

public record RankingApi(String name, Integer amountOfAccounts) {

  public static RankingApi fromDomain(Ranking ranking) {
    return new RankingApi(ranking.getId(), ranking.getAccounts().size());
  }

}
