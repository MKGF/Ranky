package com.desierto.Ranky.domain.repository;

import com.desierto.Ranky.domain.entity.Ranking;

public interface RankingRepository {

  Ranking update(Ranking ranking);

  Ranking create(Ranking ranking);

  boolean delete(String rankingId);

  Ranking find(String rankingName);
}
