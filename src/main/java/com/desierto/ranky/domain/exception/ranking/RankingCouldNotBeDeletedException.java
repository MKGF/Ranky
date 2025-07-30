package com.desierto.ranky.domain.exception.ranking;

public class RankingCouldNotBeDeletedException extends RuntimeException {

  public RankingCouldNotBeDeletedException() {
    super(
        "Ranking could not be deleted. Try again later and if the problem persists, contact the code owners.");
  }

}
