package com.desierto.Ranky.domain.exception.ranking;

import com.desierto.Ranky.domain.exception.NotFoundException;

public class RankingNotFoundException extends NotFoundException {

  public RankingNotFoundException(Long id) {
    super("Ranking with id: " + id + " not found");
  }

  public RankingNotFoundException() {
    super("Ranking not found.");
  }
}
