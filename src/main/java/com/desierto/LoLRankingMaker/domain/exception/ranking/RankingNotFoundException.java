package com.desierto.LoLRankingMaker.domain.exception.ranking;

import com.desierto.LoLRankingMaker.domain.exception.NotFoundException;

public class RankingNotFoundException extends NotFoundException {

  public RankingNotFoundException(Long id) {
    super("Ranking with id: " + id + " not found");
  }
}
