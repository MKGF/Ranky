package com.desierto.ranky.domain.exception.ranking;

import com.desierto.ranky.domain.exception.NotFoundException;

public class RankingNotFoundException extends NotFoundException {

  public RankingNotFoundException(String id) {
    super("Ranking with id: " + id + " not found");
  }

  public RankingNotFoundException() {
    super("Ranking not found.");
  }
}
